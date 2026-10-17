package com.hotel.room.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 退房参数
 */
@Data
public class CheckOutDTO {

    /** 退房日期（可选，默认当天） */
    private LocalDate checkOutDate;
}
