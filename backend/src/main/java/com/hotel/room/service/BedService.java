package com.hotel.room.service;

import com.hotel.room.common.PageResult;
import com.hotel.room.enums.BedStatus;
import com.hotel.room.vo.BedVO;

/**
 * 床位服务
 */
public interface BedService {

    /** 分页查询床位（按房间号 / 状态 / 房间） */
    PageResult<BedVO> page(Long pageNum, Long pageSize, String roomNo, BedStatus status, Long roomId);
}
