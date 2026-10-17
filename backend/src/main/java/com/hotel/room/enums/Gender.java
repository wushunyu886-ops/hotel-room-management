package com.hotel.room.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
@AllArgsConstructor
public enum Gender {

    MALE("男"),
    FEMALE("女");

    private final String desc;

    public static Gender fromName(String name) {
        for (Gender g : values()) {
            if (g.name().equalsIgnoreCase(name)) {
                return g;
            }
        }
        return null;
    }
}
