package com.hotel.room.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 首页仪表盘统计数据
 */
@Data
public class DashboardStatsVO {

    /** 今日入住人数 */
    private Long todayCheckIns;
    /** 今日退房人数 */
    private Long todayCheckOuts;
    /** 今日收入（今日生成账单金额） */
    private BigDecimal todayIncome;
    /** 房间总数 */
    private Long totalRooms;
    /** 空房数 */
    private Long availableRooms;
    /** 已入住房间数 */
    private Long occupiedRooms;
    /** 维修房间数 */
    private Long maintenanceRooms;
    /** 当前在住旅客人数 */
    private Long inStayGuests;
    /** 入住率（%） */
    private BigDecimal occupancyRate;
    /** 今日待退房（在住满 1 天及以上的旅客） */
    private List<StayGuestVO> dueCheckOutList;
    /** 空房列表 */
    private List<AvailableRoomVO> availableRoomList;
}
