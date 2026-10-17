package com.hotel.room.vo;

import com.hotel.room.enums.Gender;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 在住旅客信息（含房间床位与入住天数）
 */
@Data
public class StayGuestVO {

    private Long recordId;
    private Long guestId;
    private String guestName;
    private String idCard;
    private Gender gender;
    private String roomNo;
    private Integer bedNo;
    private String typeName;
    private LocalDate checkInDate;
    /** 入住时房价快照 */
    private BigDecimal dailyPrice;
    /** 已入住天数 */
    private Integer stayDays;
}
