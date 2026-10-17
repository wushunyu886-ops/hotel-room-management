package com.hotel.room.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 房间状态枚举
 */
@Getter
@AllArgsConstructor
public enum RoomStatus {

    AVAILABLE("空闲"),
    OCCUPIED("已入住"),
    MAINTENANCE("维修");

    private final String desc;
}
