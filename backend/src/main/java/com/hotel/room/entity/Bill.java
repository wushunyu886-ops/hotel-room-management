package com.hotel.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hotel.room.enums.PayStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单实体：退房时生成，记录房间/床号快照与结算金额
 */
@Data
@TableName("bill")
public class Bill {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 账单号（全局唯一） */
    private String billNo;

    /** 入住记录 ID */
    private Long checkInRecordId;

    /** 旅客 ID */
    private Long guestId;

    /** 房间号快照 */
    private String roomNo;

    /** 床号快照 */
    private Integer bedNo;

    /** 居住天数 */
    private Integer days;

    /** 每日单价快照 */
    private BigDecimal dailyPrice;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 支付状态：UNPAID / PAID */
    private PayStatus payStatus;

    /** 支付时间 */
    private LocalDateTime payTime;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
