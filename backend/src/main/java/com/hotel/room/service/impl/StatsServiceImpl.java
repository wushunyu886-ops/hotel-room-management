package com.hotel.room.service.impl;

import com.hotel.room.mapper.CheckInRecordMapper;
import com.hotel.room.mapper.RoomMapper;
import com.hotel.room.mapper.StatsMapper;
import com.hotel.room.service.StatsService;
import com.hotel.room.vo.AvailableRoomVO;
import com.hotel.room.vo.DashboardStatsVO;
import com.hotel.room.vo.GenderCountVO;
import com.hotel.room.vo.IncomeTrendVO;
import com.hotel.room.vo.OccupancyVO;
import com.hotel.room.vo.RoomTypeOccupancyVO;
import com.hotel.room.vo.StayGuestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计报表服务实现
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsMapper statsMapper;
    private final RoomMapper roomMapper;
    private final CheckInRecordMapper checkInRecordMapper;

    @Override
    public DashboardStatsVO dashboard() {
        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setTodayCheckIns(statsMapper.todayCheckIns());
        vo.setTodayCheckOuts(statsMapper.todayCheckOuts());
        vo.setTodayIncome(statsMapper.todayIncome());
        vo.setInStayGuests(statsMapper.inStayGuests());

        // 房间状态统计
        long total = 0, available = 0, occupied = 0, maintenance = 0;
        for (Map<String, Object> row : statsMapper.roomStatusCounts()) {
            String status = String.valueOf(row.get("status"));
            long cnt = ((Number) row.get("cnt")).longValue();
            total += cnt;
            switch (status) {
                case "AVAILABLE" -> available = cnt;
                case "OCCUPIED" -> occupied = cnt;
                case "MAINTENANCE" -> maintenance = cnt;
                default -> { }
            }
        }
        vo.setTotalRooms(total);
        vo.setAvailableRooms(available);
        vo.setOccupiedRooms(occupied);
        vo.setMaintenanceRooms(maintenance);
        // 入住率 = 已入住房间数 / 房间总数（保留两位小数）
        vo.setOccupancyRate(total == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(occupied).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP));
        vo.setDueCheckOutList(checkInRecordMapper.selectDueCheckOutList());
        vo.setAvailableRoomList(roomMapper.listAvailableRooms());
        return vo;
    }

    @Override
    public OccupancyVO occupancy(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        if (start == null) {
            start = today.minusDays(6);
        }
        if (end == null) {
            end = today;
        }
        OccupancyVO vo = new OccupancyVO();

        long total = 0, available = 0, occupied = 0, maintenance = 0;
        for (Map<String, Object> row : statsMapper.roomStatusCounts()) {
            String status = String.valueOf(row.get("status"));
            long cnt = ((Number) row.get("cnt")).longValue();
            total += cnt;
            switch (status) {
                case "AVAILABLE" -> available = cnt;
                case "OCCUPIED" -> occupied = cnt;
                case "MAINTENANCE" -> maintenance = cnt;
                default -> { }
            }
        }
        vo.setTotalRooms(total);
        vo.setOccupiedRooms(occupied);
        vo.setAvailableRooms(available);
        vo.setMaintenanceRooms(maintenance);
        vo.setOccupancyRate(total == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(occupied).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP));

        List<RoomTypeOccupancyVO> byRoomType = statsMapper.occupancyByType(start, end);
        vo.setByRoomType(byRoomType);
        List<GenderCountVO> byGender = statsMapper.genderCounts();
        vo.setByGender(byGender);
        return vo;
    }

    @Override
    public List<IncomeTrendVO> incomeTrend(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        if (start == null) {
            start = today.minusDays(6);
        }
        if (end == null) {
            end = today;
        }
        // 按日查询区间收入
        Map<LocalDate, BigDecimal> incomeMap = new HashMap<>();
        for (IncomeTrendVO item : statsMapper.incomeByDate(start.atStartOfDay(), end.plusDays(1).atStartOfDay())) {
            incomeMap.put(item.getDate(), item.getIncome() == null ? BigDecimal.ZERO : item.getIncome());
        }
        // 补齐无收入日期，保证前端折线图连续
        List<IncomeTrendVO> result = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            IncomeTrendVO item = new IncomeTrendVO();
            item.setDate(d);
            item.setIncome(incomeMap.getOrDefault(d, BigDecimal.ZERO));
            result.add(item);
        }
        return result;
    }
}
