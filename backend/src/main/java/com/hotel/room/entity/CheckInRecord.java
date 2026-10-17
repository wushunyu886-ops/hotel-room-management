package com.hotel.room.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hotel.room.enums.CheckInStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入住记录实体
 * 在住期间 active_bed_id / active_guest_id 非空，退房后置 NULL，
 * 配合唯一索引 uk_active_bed_id / uk_active_guest_id 防止重复入住
 */
@Data
@TableName("check_in_record")
public class CheckInRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 旅客 ID */
    private Long guestId;

    /** 房间 ID */
    private Long roomId;

    /** 床位 ID */
    private Long bedId;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 退房日期（退房时写入） */
    private LocalDate checkOutDate;

    /** 状态：CHECKED_IN / CHECKED_OUT */
    private CheckInStatus status;

    /** 入住时房价快照（元/天） */
    private BigDecimal dailyPriceSnapshot;

    /** 居住天数（退房时计算） */
    private Integer days;

    /** 总金额（退房时计算） */
    private BigDecimal totalAmount;

    /** 在住时指向床位 ID，退房后置 NULL（IGNORED 允许把 null 写库，配合唯一索引防止重复入住） */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long activeBedId;

    /** 在住时指向旅客 ID，退房后置 NULL（IGNORED 允许把 null 写库，配合唯一索引防止重复入住） */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long activeGuestId;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
