package com.hotel.room.dto;

import com.hotel.room.enums.CheckInStatus;
import lombok.Data;

import java.time.LocalDate;

/**
 * 入住记录分页查询参数
 */
@Data
public class CheckInQueryDTO {

    private Long pageNum = 1L;
    private Long pageSize = 10L;
    /** 状态：CHECKED_IN / CHECKED_OUT，为空查全部 */
    private CheckInStatus status;
    /** 旅客姓名模糊查询 */
    private String guestName;
    /** 房间号模糊查询 */
    private String roomNo;
    /** 入住日期范围 */
    private LocalDate startDate;
    private LocalDate endDate;
}
