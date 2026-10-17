package com.hotel.room.vo;

import com.hotel.room.enums.BedStatus;
import lombok.Data;

import java.time.LocalDate;

/**
 * 房间床位状态（查看某房间所有床位）
 */
@Data
public class RoomBedVO {

    private Long bedId;
    private Integer bedNo;
    private BedStatus bedStatus;
    /** 占用旅客姓名（空闲为空） */
    private String guestName;
    /** 入住日期（空闲为空） */
    private LocalDate checkInDate;
}
