package com.hotel.room.vo;

import com.hotel.room.enums.BedStatus;
import com.hotel.room.enums.Gender;
import com.hotel.room.enums.Orientation;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 可用床位信息（办理入住时推荐/选择）
 */
@Data
public class AvailableBedVO {

    private Long bedId;
    private Integer bedNo;
    private Long roomId;
    private String roomNo;
    private Integer floor;
    private Orientation orientation;
    private String typeName;
    private BigDecimal dailyPrice;
    /** 房间当前性别限制（空房为 NULL） */
    private Gender currentGender;
    private BedStatus bedStatus;
}
