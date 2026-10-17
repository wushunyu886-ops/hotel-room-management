package com.hotel.room.vo;

import com.hotel.room.enums.Gender;
import com.hotel.room.enums.Orientation;
import com.hotel.room.enums.RoomStatus;
import lombok.Data;

/**
 * 房间列表 VO（联表房型信息与在住人数）
 */
@Data
public class RoomVO {

    private Long id;
    private String roomNo;
    private Long roomTypeId;
    private Orientation orientation;
    private Integer floor;
    private RoomStatus status;
    private Gender currentGender;
    private Integer version;
    private String typeName;
    private Integer bedCount;
    /** 房间当前在住人数 */
    private Long occupiedBeds;
}
