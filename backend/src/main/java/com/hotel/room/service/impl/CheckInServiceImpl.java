package com.hotel.room.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.common.BusinessException;
import com.hotel.room.common.PageResult;
import com.hotel.room.dto.CheckInDTO;
import com.hotel.room.dto.CheckInQueryDTO;
import com.hotel.room.dto.CheckOutDTO;
import com.hotel.room.entity.Bed;
import com.hotel.room.entity.Bill;
import com.hotel.room.entity.CheckInRecord;
import com.hotel.room.entity.Guest;
import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomType;
import com.hotel.room.enums.BedStatus;
import com.hotel.room.enums.CheckInStatus;
import com.hotel.room.enums.Gender;
import com.hotel.room.enums.Orientation;
import com.hotel.room.enums.PayStatus;
import com.hotel.room.enums.RoomStatus;
import com.hotel.room.mapper.BedMapper;
import com.hotel.room.mapper.BillMapper;
import com.hotel.room.mapper.CheckInRecordMapper;
import com.hotel.room.mapper.GuestMapper;
import com.hotel.room.mapper.RoomMapper;
import com.hotel.room.mapper.RoomTypeMapper;
import com.hotel.room.service.CheckInService;
import com.hotel.room.util.BizValidator;
import com.hotel.room.vo.AvailableBedVO;
import com.hotel.room.vo.BillVO;
import com.hotel.room.vo.CheckInVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 入住/退房服务实现（核心业务）
 *
 * 并发安全设计：
 * 1. 事务保证原子性（@Transactional）
 * 2. SELECT ... FOR UPDATE 锁定床位行与房间行，串行化并发入住/退房
 * 3. 唯一索引 uk_active_bed_id / uk_active_guest_id 兜底防重复入住
 * 4. room.version 乐观锁防房间状态更新冲突
 * 5. DuplicateKeyException 由全局异常处理器转为友好提示
 */
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final GuestMapper guestMapper;
    private final BedMapper bedMapper;
    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final CheckInRecordMapper checkInRecordMapper;
    private final BillMapper billMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckInVO checkIn(CheckInDTO dto) {
        // 1. 校验旅客存在且未在住
        Guest guest = guestMapper.selectById(dto.getGuestId());
        BizValidator.notNull(guest, "旅客不存在");
        BizValidator.requireTrue(checkInRecordMapper.countActiveByGuest(guest.getId()) == 0,
                "该旅客已在住，不能重复入住");

        LocalDate checkInDate = dto.getCheckInDate() != null ? dto.getCheckInDate() : LocalDate.now();
        BizValidator.requireTrue(!checkInDate.isAfter(LocalDate.now()), "入住日期不能晚于今天");

        // 2. 确定床位：优先使用指定床位，否则按性别规则自动推荐
        Long bedId = dto.getBedId();
        if (bedId == null) {
            bedId = bedMapper.recommendBedId(guest.getGender(), dto.getOrientation(), dto.getRoomTypeId(), dto.getRoomId());
            BizValidator.notNull(bedId, "暂无可分配床位，请调整筛选条件后重试");
        }

        // 3. 锁定床位行：并发时第二个事务会阻塞到第一个提交，再校验状态
        Bed bed = bedMapper.selectByIdForUpdate(bedId);
        BizValidator.notNull(bed, "床位不存在");
        BizValidator.requireTrue(bed.getStatus() == BedStatus.FREE, "该床位已被占用，请重新选择");

        // 4. 锁定房间行：串行化同房间的并发操作
        //    仅拒绝维修房；已入住房间允许同性别续住（性别规则见步骤 5）
        Room room = roomMapper.selectByIdForUpdate(bed.getRoomId());
        BizValidator.notNull(room, "房间不存在");
        BizValidator.requireTrue(room.getStatus() == RoomStatus.AVAILABLE || room.getStatus() == RoomStatus.OCCUPIED,
                "房间当前不可用（" + room.getStatus().getDesc() + "）");

        // 5. 性别规则：空房可任意性别，非空房必须与房间内性别一致
        if (room.getCurrentGender() != null && room.getCurrentGender() != guest.getGender()) {
            throw new BusinessException("该房间已住" + room.getCurrentGender().getDesc() + "性，不能混住");
        }

        // 6. 未住满校验（在住人数 < 房型床位数）
        RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
        BizValidator.notNull(roomType, "房间类型不存在");
        BizValidator.requireTrue(roomType.getStatus() != null && roomType.getStatus() == 1, "房间类型已停用");
        long activeBeds = checkInRecordMapper.countActiveByRoom(room.getId());
        BizValidator.requireTrue(activeBeds < roomType.getBedCount(), "该房间已住满");

        // 7. 插入入住记录（active_bed_id / active_guest_id 非空；并发重复由唯一索引拦截）
        CheckInRecord record = new CheckInRecord();
        record.setGuestId(guest.getId());
        record.setRoomId(room.getId());
        record.setBedId(bed.getId());
        record.setCheckInDate(checkInDate);
        record.setStatus(CheckInStatus.CHECKED_IN);
        record.setDailyPriceSnapshot(roomType.getDailyPrice());
        record.setActiveBedId(bed.getId());
        record.setActiveGuestId(guest.getId());
        checkInRecordMapper.insert(record);

        // 8. 更新床位为已占用
        Bed bedUpdate = new Bed();
        bedUpdate.setId(bed.getId());
        bedUpdate.setStatus(BedStatus.OCCUPIED);
        bedMapper.updateById(bedUpdate);

        // 9. 更新房间为已入住；空房首次入住时设置房间性别限制（乐观锁 version 防冲突）
        Room roomUpdate = new Room();
        roomUpdate.setId(room.getId());
        roomUpdate.setStatus(RoomStatus.OCCUPIED);
        if (room.getCurrentGender() == null) {
            roomUpdate.setCurrentGender(guest.getGender());
        }
        roomUpdate.setVersion(room.getVersion());
        roomMapper.updateById(roomUpdate);

        return buildCheckInVO(record, guest, room, bed, roomType);
    }

    @Override
    public PageResult<CheckInVO> page(CheckInQueryDTO query) {
        Page<CheckInVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        List<CheckInVO> list = checkInRecordMapper.selectCheckInPage(page, query.getStatus(), query.getGuestName(),
                query.getRoomNo(), query.getStartDate(), query.getEndDate());
        return new PageResult<>(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<AvailableBedVO> availableBeds(Gender gender, Orientation orientation, Long roomTypeId, Long roomId) {
        BizValidator.notNull(gender, "请先选择旅客性别");
        return bedMapper.listAvailableBeds(gender, orientation, roomTypeId, roomId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillVO checkOut(Long recordId, CheckOutDTO dto) {
        // 1. 锁定入住记录行：防止同一记录被并发退房
        CheckInRecord record = checkInRecordMapper.selectByIdForUpdate(recordId);
        BizValidator.notNull(record, "入住记录不存在");
        BizValidator.requireTrue(record.getStatus() == CheckInStatus.CHECKED_IN, "该记录已退房，请勿重复操作");

        LocalDate checkOutDate = dto.getCheckOutDate() != null ? dto.getCheckOutDate() : LocalDate.now();
        BizValidator.requireTrue(!checkOutDate.isBefore(record.getCheckInDate()), "退房日期不能早于入住日期");

        // 2. 锁定房间行：串行化同房间并发退房（避免“是否还有在住”误判）
        Room room = roomMapper.selectByIdForUpdate(record.getRoomId());
        BizValidator.notNull(room, "房间不存在");

        // 3. 结算：居住天数 = 退房日期 - 入住日期，住一晚算一天、不足一天按一天（最少 1 天）
        long days = Math.max(ChronoUnit.DAYS.between(record.getCheckInDate(), checkOutDate), 1);
        BigDecimal totalAmount = BigDecimal.valueOf(days).multiply(record.getDailyPriceSnapshot());

        // 4. 更新入住记录为已退房，清空 active 字段（释放唯一索引占用）
        CheckInRecord recordUpdate = new CheckInRecord();
        recordUpdate.setId(record.getId());
        recordUpdate.setStatus(CheckInStatus.CHECKED_OUT);
        recordUpdate.setCheckOutDate(checkOutDate);
        recordUpdate.setDays((int) days);
        recordUpdate.setTotalAmount(totalAmount);
        recordUpdate.setActiveBedId(null);
        recordUpdate.setActiveGuestId(null);
        checkInRecordMapper.updateById(recordUpdate);

        // 5. 释放床位
        Bed bed = bedMapper.selectById(record.getBedId());
        Bed bedUpdate = new Bed();
        bedUpdate.setId(bed.getId());
        bedUpdate.setStatus(BedStatus.FREE);
        bedMapper.updateById(bedUpdate);

        // 6. 房间无在住旅客则恢复空房、清除性别限制（乐观锁）
        if (checkInRecordMapper.countActiveByRoom(room.getId()) == 0) {
            Room roomUpdate = new Room();
            roomUpdate.setId(room.getId());
            roomUpdate.setStatus(RoomStatus.AVAILABLE);
            roomUpdate.setCurrentGender(null);
            roomUpdate.setVersion(room.getVersion());
            roomMapper.updateById(roomUpdate);
        }

        // 7. 生成账单（未支付），记录房间/床号快照
        Bill bill = new Bill();
        bill.setBillNo(generateBillNo());
        bill.setCheckInRecordId(record.getId());
        bill.setGuestId(record.getGuestId());
        bill.setRoomNo(room.getRoomNo());
        bill.setBedNo(bed.getBedNo());
        bill.setDays((int) days);
        bill.setDailyPrice(record.getDailyPriceSnapshot());
        bill.setTotalAmount(totalAmount);
        bill.setPayStatus(PayStatus.UNPAID);
        billMapper.insert(bill);

        return billMapper.selectBillDetail(bill.getId());
    }

    /** 组装入住记录 VO */
    private CheckInVO buildCheckInVO(CheckInRecord record, Guest guest, Room room, Bed bed, RoomType roomType) {
        CheckInVO vo = new CheckInVO();
        vo.setRecordId(record.getId());
        vo.setGuestId(guest.getId());
        vo.setGuestName(guest.getName());
        vo.setIdCard(guest.getIdCard());
        vo.setGender(guest.getGender());
        vo.setRoomId(room.getId());
        vo.setRoomNo(room.getRoomNo());
        vo.setOrientation(room.getOrientation());
        vo.setBedId(bed.getId());
        vo.setBedNo(bed.getBedNo());
        vo.setTypeName(roomType.getTypeName());
        vo.setCheckInDate(record.getCheckInDate());
        vo.setStatus(record.getStatus());
        vo.setDailyPriceSnapshot(record.getDailyPriceSnapshot());
        vo.setStayDays((int) Math.max(ChronoUnit.DAYS.between(record.getCheckInDate(), LocalDate.now()), 0));
        return vo;
    }

    /** 生成账单号：B + 年月日时分秒 + 3 位随机数 */
    private String generateBillNo() {
        return "B" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
    }
}
