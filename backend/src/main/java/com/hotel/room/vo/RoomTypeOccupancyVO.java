package com.hotel.room.vo;

import com.hotel.room.enums.Orientation;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房型入住率与收入统计 VO
 */
@Data
public class RoomTypeOccupancyVO {

    private Long roomTypeId;
    private String typeName;
    private Orientation orientation;
    private Long totalRooms;
    private Long occupiedRooms;
    private BigDecimal occupancyRate;
    /** 期间收入（按退房日期归属） */
    private BigDecimal income;
}
