package com.hotel.room.dto;

import com.hotel.room.enums.Orientation;
import com.hotel.room.enums.RoomStatus;
import lombok.Data;

/**
 * 房间分页查询参数
 */
@Data
public class RoomQueryDTO {

    private Long pageNum = 1L;
    private Long pageSize = 10L;
    /** 房间号模糊查询 */
    private String roomNo;
    /** 朝向筛选 */
    private Orientation orientation;
    /** 房间类型筛选 */
    private Long roomTypeId;
    /** 状态筛选 */
    private RoomStatus status;
}
