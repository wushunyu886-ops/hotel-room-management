package com.hotel.room.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.hotel.room.enums.Gender;
import com.hotel.room.enums.Orientation;
import com.hotel.room.enums.RoomStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 房间实体
 */
@Data
@TableName("room")
public class Room {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 房间号（全局唯一） */
    private String roomNo;

    /** 房间类型 ID */
    private Long roomTypeId;

    /** 朝向：SOUTH / NORTH */
    private Orientation orientation;

    /** 楼层 */
    private Integer floor;

    /** 状态：AVAILABLE / OCCUPIED / MAINTENANCE */
    private RoomStatus status;

    /** 当前房间旅客性别：MALE / FEMALE / NULL（空房无限制，IGNORED 允许退房时把 null 写库以清除性别限制） */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Gender currentGender;

    /** 乐观锁版本号 */
    @Version
    private Integer version;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
