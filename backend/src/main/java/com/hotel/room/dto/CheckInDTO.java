package com.hotel.room.dto;

import com.hotel.room.enums.Orientation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 办理入住参数
 * bedId 为空时由系统按性别规则自动推荐床位
 */
@Data
public class CheckInDTO {

    @NotNull(message = "请选择旅客")
    private Long guestId;

    /** 指定床位（可选）；为空则自动推荐 */
    private Long bedId;

    /** 指定房间（可选，用于限定推荐范围） */
    private Long roomId;

    /** 朝向筛选（可选） */
    private Orientation orientation;

    /** 房型筛选（可选） */
    private Long roomTypeId;

    /** 入住日期（可选，默认当天） */
    private LocalDate checkInDate;
}
