package com.hotel.room.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 入住记录状态枚举
 */
@Getter
@AllArgsConstructor
public enum CheckInStatus {

    CHECKED_IN("在住"),
    CHECKED_OUT("已退房");

    private final String desc;
}
