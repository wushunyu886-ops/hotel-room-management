package com.hotel.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hotel.room.enums.Gender;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 旅客实体
 */
@Data
@TableName("guest")
public class Guest {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 姓名 */
    private String name;

    /** 身份证号（全局唯一，18 位） */
    private String idCard;

    /** 性别：MALE / FEMALE */
    private Gender gender;

    /** 联系电话 */
    private String phone;

    /** 住址 */
    private String address;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
