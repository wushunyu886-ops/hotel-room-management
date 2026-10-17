package com.hotel.room.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 每日收入统计 VO
 */
@Data
public class IncomeTrendVO {

    private LocalDate date;
    private BigDecimal income;
}
