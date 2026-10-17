package com.hotel.room.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 床位状态枚举
 */
@Getter
@AllArgsConstructor
public enum BedStatus {

    FREE("空闲"),
    OCCUPIED("已占用"),
    MAINTENANCE("维修");

    private final String desc;
}
