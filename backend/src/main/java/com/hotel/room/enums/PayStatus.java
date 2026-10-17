package com.hotel.room.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 账单支付状态枚举
 */
@Getter
@AllArgsConstructor
public enum PayStatus {

    UNPAID("未支付"),
    PAID("已支付");

    private final String desc;
}
