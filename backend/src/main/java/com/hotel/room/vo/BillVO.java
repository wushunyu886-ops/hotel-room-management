package com.hotel.room.vo;

import com.hotel.room.enums.PayStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单 VO（联表查询旅客、入住记录信息）
 */
@Data
public class BillVO {

    private Long id;
    private String billNo;
    private Long checkInRecordId;
    private Long guestId;
    private String guestName;
    private String idCard;
    private String roomNo;
    private Integer bedNo;
    private Integer days;
    private BigDecimal dailyPrice;
    private BigDecimal totalAmount;
    private PayStatus payStatus;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
