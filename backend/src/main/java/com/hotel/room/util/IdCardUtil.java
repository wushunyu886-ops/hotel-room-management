package com.hotel.room.util;

/**
 * 身份证号校验工具：18 位格式 + 加权校验位算法（GB 11643-1999）
 */
public final class IdCardUtil {

    private IdCardUtil() {
    }

    /** 加权因子 */
    private static final int[] WEIGHT = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /** 校验码表 */
    private static final char[] CHECK_CODE = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    public static boolean isValidIdCard(String idCard) {
        if (idCard == null) {
            return false;
        }
        String card = idCard.trim().toUpperCase();
        if (!card.matches("^\\d{17}[\\dX]$")) {
            return false;
        }
        // 前 6 位行政区划、第 7-14 位出生日期简单校验
        String birth = card.substring(6, 14);
        if (!birth.matches("^\\d{8}$")) {
            return false;
        }
        // 校验位计算
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (card.charAt(i) - '0') * WEIGHT[i];
        }
        return CHECK_CODE[sum % 11] == card.charAt(17);
    }
}
