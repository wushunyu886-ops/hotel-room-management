package com.hotel.room.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.common.BusinessException;
import com.hotel.room.common.PageResult;
import com.hotel.room.dto.RoomDTO;
import com.hotel.room.dto.RoomQueryDTO;
import com.hotel.room.entity.Bed;
import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomType;
import com.hotel.room.enums.BedStatus;
import com.hotel.room.enums.RoomStatus;
import com.hotel.room.mapper.BedMapper;
import com.hotel.room.mapper.CheckInRecordMapper;
import com.hotel.room.mapper.RoomMapper;
import com.hotel.room.mapper.RoomTypeMapper;
import com.hotel.room.service.RoomService;
import com.hotel.room.util.BizValidator;
import com.hotel.room.vo.RoomBedVO;
import com.hotel.room.vo.RoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房间服务实现：新增房间时按房型自动生成床位
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final BedMapper bedMapper;
    private final CheckInRecordMapper checkInRecordMapper;

    @Override
    public PageResult<RoomVO> page(RoomQueryDTO query) {
        Page<RoomVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        List<RoomVO> list = roomMapper.selectRoomPage(page, query.getRoomNo(), query.getOrientation(),
                query.getRoomTypeId(), query.getStatus());
        return new PageResult<>(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomVO create(RoomDTO dto) {
        // 房间号全局唯一
        Long count = roomMapper.selectCount(new LambdaQueryWrapper<Room>().eq(Room::getRoomNo, dto.getRoomNo()));
        BizValidator.requireTrue(count == null || count == 0, "房间号已存在：" + dto.getRoomNo());

        RoomType roomType = roomTypeMapper.selectById(dto.getRoomTypeId());
        BizValidator.notNull(roomType, "房间类型不存在");

        // 新建房间默认空闲、无性别限制
        Room room = new Room();
        room.setRoomNo(dto.getRoomNo());
        room.setRoomTypeId(dto.getRoomTypeId());
        room.setOrientation(roomType.getOrientation());
        room.setFloor(dto.getFloor());
        room.setStatus(RoomStatus.AVAILABLE);
        room.setCurrentGender(null);
        room.setVersion(0);
        roomMapper.insert(room);

        // 按房型床位数自动生成床位（1 号床 ~ N 号床）
        for (int i = 1; i <= roomType.getBedCount(); i++) {
            Bed bed = new Bed();
            bed.setRoomId(room.getId());
            bed.setBedNo(i);
            bed.setStatus(BedStatus.FREE);
            bedMapper.insert(bed);
        }
        return toVO(room, roomType, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomVO update(Long id, RoomDTO dto) {
        Room room = roomMapper.selectById(id);
        BizValidator.notNull(room, "房间不存在");

        // 房间号唯一性校验（修改场景排除自身）
        if (!room.getRoomNo().equals(dto.getRoomNo())) {
            Long count = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                    .eq(Room::getRoomNo, dto.getRoomNo()).ne(Room::getId, id));
            BizValidator.requireTrue(count == null || count == 0, "房间号已存在：" + dto.getRoomNo());
        }

        long activeBeds = checkInRecordMapper.countActiveByRoom(id);
        // 有旅客在住时不允许改为空闲/维修
        if (activeBeds > 0 && dto.getStatus() != null && dto.getStatus() != RoomStatus.OCCUPIED) {
            throw new BusinessException("房间有旅客在住，不能修改为" + dto.getStatus().getDesc());
        }

        RoomType roomType = roomTypeMapper.selectById(dto.getRoomTypeId());
        BizValidator.notNull(roomType, "房间类型不存在");
        room.setRoomNo(dto.getRoomNo());
        room.setRoomTypeId(dto.getRoomTypeId());
        room.setOrientation(roomType.getOrientation());
        room.setFloor(dto.getFloor());
        if (dto.getStatus() != null) {
            room.setStatus(dto.getStatus());
            if (dto.getStatus() == RoomStatus.AVAILABLE) {
                room.setCurrentGender(null);
            }
        }
        roomMapper.updateById(room);

        // 房型变更时同步床位数量
        syncBeds(room.getId(), roomType.getBedCount());
        return toVO(room, roomType, activeBeds);
    }

    /**
     * 同步床位：床位数增加则补建床位；减少则删除空闲的多余床位（占用中禁止缩减）
     */
    private void syncBeds(Long roomId, int targetCount) {
        List<Bed> beds = bedMapper.selectList(new LambdaQueryWrapper<Bed>().eq(Bed::getRoomId, roomId));
        int current = beds.size();
        if (current < targetCount) {
            for (int i = current + 1; i <= targetCount; i++) {
                Bed bed = new Bed();
                bed.setRoomId(roomId);
                bed.setBedNo(i);
                bed.setStatus(BedStatus.FREE);
                bedMapper.insert(bed);
            }
        } else if (current > targetCount) {
            for (Bed bed : beds) {
                if (bed.getBedNo() > targetCount) {
                    BizValidator.requireTrue(bed.getStatus() == BedStatus.FREE,
                            "床位 " + bed.getBedNo() + " 号仍被占用，无法缩减床位数");
                    bedMapper.deleteById(bed.getId());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        long activeBeds = checkInRecordMapper.countActiveByRoom(id);
        BizValidator.requireTrue(activeBeds == 0, "房间有旅客在住，无法删除");
        long historyCount = checkInRecordMapper.countByRoom(id);
        BizValidator.requireTrue(historyCount == 0, "房间存在历史入住记录，无法删除");
        // 先删床位再删房间
        bedMapper.delete(new LambdaQueryWrapper<Bed>().eq(Bed::getRoomId, id));
        roomMapper.deleteById(id);
    }

    @Override
    public List<RoomBedVO> listBeds(Long roomId) {
        BizValidator.notNull(roomMapper.selectById(roomId), "房间不存在");
        return bedMapper.selectRoomBeds(roomId);
    }

    private RoomVO toVO(Room room, RoomType roomType, Long occupiedBeds) {
        RoomVO vo = new RoomVO();
        BeanUtils.copyProperties(room, vo);
        vo.setTypeName(roomType.getTypeName());
        vo.setBedCount(roomType.getBedCount());
        vo.setOccupiedBeds(occupiedBeds);
        return vo;
    }
}
