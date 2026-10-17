package com.hotel.room.mapper;

import com.hotel.room.vo.GenderCountVO;
import com.hotel.room.vo.IncomeTrendVO;
import com.hotel.room.vo.RoomTypeOccupancyVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 统计报表 Mapper
 */
public interface StatsMapper {

    /** 今日入住人数 */
    @Select("SELECT COUNT(*) FROM check_in_record WHERE status = 'CHECKED_IN' AND check_in_date = CURDATE()")
    Long todayCheckIns();

    /** 今日退房人数 */
    @Select("SELECT COUNT(*) FROM check_in_record WHERE status = 'CHECKED_OUT' AND check_out_date = CURDATE()")
    Long todayCheckOuts();

    /** 今日收入（今日生成账单金额合计） */
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM bill WHERE DATE(create_time) = CURDATE()")
    BigDecimal todayIncome();

    /** 当前在住旅客人数 */
    @Select("SELECT COUNT(*) FROM check_in_record WHERE status = 'CHECKED_IN'")
    Long inStayGuests();

    /** 各房间状态数量（柱状图） */
    @Select("SELECT status, COUNT(*) AS cnt FROM room GROUP BY status")
    List<Map<String, Object>> roomStatusCounts();

    /** 按房型统计入住率与收入（收入按退房日期归属区间） */
    @Select({
            "<script>",
            "SELECT rt.id AS roomTypeId, rt.type_name AS typeName, rt.orientation, ",
            "COUNT(DISTINCT r.id) AS totalRooms, ",
            "IFNULL(SUM(CASE WHEN r.status = 'OCCUPIED' THEN 1 ELSE 0 END), 0) AS occupiedRooms, ",
            "ROUND(IFNULL(SUM(CASE WHEN r.status = 'OCCUPIED' THEN 1 ELSE 0 END), 0) * 100.0 / COUNT(DISTINCT r.id), 2) AS occupancyRate, ",
            "IFNULL(SUM(CASE WHEN c.status = 'CHECKED_OUT' AND c.check_out_date BETWEEN #{start} AND #{end} THEN c.total_amount ELSE 0 END), 0) AS income ",
            "FROM room_type rt ",
            "LEFT JOIN room r ON r.room_type_id = rt.id ",
            "LEFT JOIN check_in_record c ON c.room_id = r.id AND c.status = 'CHECKED_OUT' ",
            "GROUP BY rt.id, rt.type_name, rt.orientation ORDER BY rt.id",
            "</script>"
    })
    List<RoomTypeOccupancyVO> occupancyByType(@Param("start") LocalDate start, @Param("end") LocalDate end);

    /** 按性别统计在住旅客人数 */
    @Select("SELECT g.gender, COUNT(*) AS count FROM guest g " +
            "JOIN check_in_record c ON c.guest_id = g.id AND c.status = 'CHECKED_IN' GROUP BY g.gender")
    List<GenderCountVO> genderCounts();

    /** 区间每日收入（[start, endExclusive)） */
    @Select("SELECT DATE(create_time) AS date, IFNULL(SUM(total_amount), 0) AS income " +
            "FROM bill WHERE create_time >= #{start} AND create_time < #{endExclusive} " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<IncomeTrendVO> incomeByDate(@Param("start") LocalDateTime start, @Param("endExclusive") LocalDateTime endExclusive);
}
