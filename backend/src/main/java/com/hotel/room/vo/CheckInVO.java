package com.hotel.room.vo;

import com.hotel.room.enums.CheckInStatus;
import com.hotel.room.enums.Gender;
import com.hotel.room.enums.Orientation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入住记录列表 VO（联表查询旅客/房间/床位信息）
 */
@Data
public class CheckInVO {

    private Long recordId;
    private Long guestId;
    private String guestName;
    private String idCard;
    private Gender gender;
    private Long roomId;
    private String roomNo;
    private Orientation orientation;
    private Long bedId;
    private Integer bedNo;
    private String typeName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private CheckInStatus status;
    private BigDecimal dailyPriceSnapshot;
    /** 已入住天数 */
    private Integer stayDays;
}
