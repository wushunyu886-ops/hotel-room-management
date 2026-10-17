package com.hotel.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体（可选）
 */
@Data
@TableName("payment_record")
public class PaymentRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 账单 ID */
    private Long billId;

    /** 支付方式：CASH / WECHAT / ALIPAY */
    private String payMethod;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付时间 */
    private LocalDateTime payTime;

    private LocalDateTime createTime;
}
