package com.hotel.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hotel.room.enums.BedStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 床位实体
 */
@Data
@TableName("bed")
public class Bed {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属房间 ID */
    private Long roomId;

    /** 床号（房间内唯一） */
    private Integer bedNo;

    /** 状态：FREE / OCCUPIED / MAINTENANCE */
    private BedStatus status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
