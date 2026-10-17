package com.hotel.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hotel.room.enums.Orientation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间类型实体：朝向 + 类型名决定床位数与每日住宿费
 */
@Data
@TableName("room_type")
public class RoomType {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 朝向：SOUTH / NORTH */
    private Orientation orientation;

    /** 类型名称：朝南两人间 / 朝南三人间 / 朝北三人间 / 朝北四人间 */
    private String typeName;

    /** 床位数 */
    private Integer bedCount;

    /** 每日住宿费（元） */
    private BigDecimal dailyPrice;

    /** 状态：1-启用 0-停用 */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
