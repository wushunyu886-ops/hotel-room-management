package com.hotel.room.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 入住率统计 VO
 */
@Data
public class OccupancyVO {

    private Long totalRooms;
    private Long occupiedRooms;
    private Long availableRooms;
    private Long maintenanceRooms;
    private BigDecimal occupancyRate;
    /** 按房型统计 */
    private List<RoomTypeOccupancyVO> byRoomType;
    /** 按性别统计在住旅客 */
    private List<GenderCountVO> byGender;
}
