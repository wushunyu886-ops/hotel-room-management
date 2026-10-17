package com.hotel.room.util;

import com.hotel.room.common.BusinessException;

/**
 * 业务校验工具类：统一前置校验，失败抛出 BusinessException
 */
public final class BizValidator {

    private BizValidator() {
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new BusinessException(message);
        }
    }

    public static void notBlank(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new BusinessException(message);
        }
    }

    public static void requireTrue(boolean condition, String message) {
        if (!condition) {
            throw new BusinessException(message);
        }
    }

    /** 校验身份证号（18 位，含校验位校验），为空时放行 */
    public static void validIdCard(String idCard, String message) {
        if (idCard != null && !idCard.isEmpty() && !IdCardUtil.isValidIdCard(idCard)) {
            throw new BusinessException(message);
        }
    }

    /** 校验手机号（11 位，1 开头），为空时放行 */
    public static void validPhone(String phone, String message) {
        if (phone != null && !phone.isEmpty() && !phone.matches("^1\\d{10}$")) {
            throw new BusinessException(message);
        }
    }
}
