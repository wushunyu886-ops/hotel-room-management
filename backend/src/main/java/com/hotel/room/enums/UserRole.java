package com.hotel.room.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统用户角色枚举
 */
@Getter
@AllArgsConstructor
public enum UserRole {

    ADMIN("管理员"),
    RECEPTIONIST("前台");

    private final String desc;
}
