package com.hotel.room.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 房间朝向枚举
 */
@Getter
@AllArgsConstructor
public enum Orientation {

    SOUTH("朝南"),
    NORTH("朝北");

    private final String desc;
}
