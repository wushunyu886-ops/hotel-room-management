package com.hotel.room.vo;

import com.hotel.room.enums.Orientation;
import com.hotel.room.enums.RoomStatus;
import lombok.Data;

/**
 * 空房信息
 */
@Data
public class AvailableRoomVO {

    private Long roomId;
    private String roomNo;
    private Orientation orientation;
    private Integer floor;
    private String typeName;
    private Integer bedCount;
    private RoomStatus status;
}
