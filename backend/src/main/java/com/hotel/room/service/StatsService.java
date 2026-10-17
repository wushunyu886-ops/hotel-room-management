package com.hotel.room.service;

import com.hotel.room.vo.DashboardStatsVO;
import com.hotel.room.vo.IncomeTrendVO;
import com.hotel.room.vo.OccupancyVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计报表服务
 */
public interface StatsService {

    /** 首页仪表盘统计 */
    DashboardStatsVO dashboard();

    /** 入住率统计（默认近 7 天） */
    OccupancyVO occupancy(LocalDate start, LocalDate end);

    /** 每日收入趋势（默认近 7 天，缺失日期补 0） */
    List<IncomeTrendVO> incomeTrend(LocalDate start, LocalDate end);
}
