package com.hotel.room.vo;

import com.hotel.room.enums.BedStatus;
import com.hotel.room.enums.Gender;
import com.hotel.room.enums.Orientation;
import lombok.Data;

/**
 * 床位列表 VO（联表带房间/房型信息）
 */
@Data
public class BedVO {

    private Long bedId;
    private Integer bedNo;
    private BedStatus bedStatus;
    private Long roomId;
    private String roomNo;
    private Orientation orientation;
    private String typeName;
    /** 房间当前性别限制 */
    private Gender currentGender;
    /** 占用旅客姓名 */
    private String guestName;
}
