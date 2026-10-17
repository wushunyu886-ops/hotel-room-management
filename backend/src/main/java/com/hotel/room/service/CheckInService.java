package com.hotel.room.service;

import com.hotel.room.common.PageResult;
import com.hotel.room.dto.CheckInDTO;
import com.hotel.room.dto.CheckInQueryDTO;
import com.hotel.room.dto.CheckOutDTO;
import com.hotel.room.enums.Gender;
import com.hotel.room.enums.Orientation;
import com.hotel.room.vo.AvailableBedVO;
import com.hotel.room.vo.BillVO;
import com.hotel.room.vo.CheckInVO;

import java.util.List;

/**
 * 入住/退房服务（核心业务，事务 + 行锁保证并发安全）
 */
public interface CheckInService {

    /** 办理入住：支持指定床位或自动推荐，校验性别规则与床位状态 */
    CheckInVO checkIn(CheckInDTO dto);

    /** 分页查询入住记录 */
    PageResult<CheckInVO> page(CheckInQueryDTO query);

    /** 查询可用床位（入住弹窗选择/自动推荐） */
    List<AvailableBedVO> availableBeds(Gender gender, Orientation orientation, Long roomTypeId, Long roomId);

    /** 办理退房：计算天数与金额、释放床位、生成账单 */
    BillVO checkOut(Long recordId, CheckOutDTO dto);
}
