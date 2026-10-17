package com.hotel.room.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.entity.CheckInRecord;
import com.hotel.room.enums.CheckInStatus;
import com.hotel.room.vo.CheckInVO;
import com.hotel.room.vo.StayGuestVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 入住记录 Mapper
 */
public interface CheckInRecordMapper extends BaseMapper<CheckInRecord> {

    /**
     * 锁定入住记录行（退房事务使用）
     */
    @Select("SELECT * FROM check_in_record WHERE id = #{id} FOR UPDATE")
    CheckInRecord selectByIdForUpdate(@Param("id") Long id);

    /** 统计旅客在住记录数（唯一索引 uk_active_guest_id 兜底） */
    @Select("SELECT COUNT(*) FROM check_in_record WHERE guest_id = #{guestId} AND status = 'CHECKED_IN'")
    long countActiveByGuest(@Param("guestId") Long guestId);

    /** 统计房间在住记录数（退房时判断房间是否已空） */
    @Select("SELECT COUNT(*) FROM check_in_record WHERE room_id = #{roomId} AND status = 'CHECKED_IN'")
    long countActiveByRoom(@Param("roomId") Long roomId);

    /** 统计房间全部记录数（删除房间时校验是否被引用） */
    @Select("SELECT COUNT(*) FROM check_in_record WHERE room_id = #{roomId}")
    long countByRoom(@Param("roomId") Long roomId);

    /**
     * 分页查询入住记录（联表旅客/房间/床位/房型）
     */
    @Select({
            "<script>",
            "SELECT c.id AS recordId, c.guest_id AS guestId, g.name AS guestName, g.id_card AS idCard, g.gender, ",
            "c.room_id AS roomId, r.room_no AS roomNo, r.orientation, c.bed_id AS bedId, b.bed_no AS bedNo, ",
            "rt.type_name AS typeName, c.check_in_date AS checkInDate, c.check_out_date AS checkOutDate, ",
            "c.status, c.daily_price_snapshot AS dailyPriceSnapshot, ",
            "DATEDIFF(IFNULL(c.check_out_date, CURDATE()), c.check_in_date) AS stayDays ",
            "FROM check_in_record c ",
            "JOIN guest g ON g.id = c.guest_id ",
            "JOIN room r ON r.id = c.room_id ",
            "JOIN bed b ON b.id = c.bed_id ",
            "JOIN room_type rt ON rt.id = r.room_type_id ",
            "WHERE 1=1",
            "<if test='status != null'>AND c.status = #{status}</if>",
            "<if test='guestName != null and guestName != \"\"'>AND g.name LIKE CONCAT('%', #{guestName}, '%')</if>",
            "<if test='roomNo != null and roomNo != \"\"'>AND r.room_no LIKE CONCAT('%', #{roomNo}, '%')</if>",
            "<if test='startDate != null'>AND c.check_in_date &gt;= #{startDate}</if>",
            "<if test='endDate != null'>AND c.check_in_date &lt;= #{endDate}</if>",
            "ORDER BY c.create_time DESC",
            "</script>"
    })
    List<CheckInVO> selectCheckInPage(Page<CheckInVO> page,
                                      @Param("status") CheckInStatus status,
                                      @Param("guestName") String guestName,
                                      @Param("roomNo") String roomNo,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    /**
     * 今日待退房列表：在住满 1 天及以上的旅客，按入住时间最久排序（仪表盘）
     */
    @Select("SELECT c.id AS recordId, c.guest_id AS guestId, g.name AS guestName, g.id_card AS idCard, g.gender, " +
            "r.room_no AS roomNo, b.bed_no AS bedNo, rt.type_name AS typeName, " +
            "c.check_in_date AS checkInDate, c.daily_price_snapshot AS dailyPrice, " +
            "DATEDIFF(CURDATE(), c.check_in_date) AS stayDays " +
            "FROM check_in_record c " +
            "JOIN guest g ON g.id = c.guest_id " +
            "JOIN room r ON r.id = c.room_id " +
            "JOIN bed b ON b.id = c.bed_id " +
            "JOIN room_type rt ON rt.id = r.room_type_id " +
            "WHERE c.status = 'CHECKED_IN' AND DATEDIFF(CURDATE(), c.check_in_date) >= 1 " +
            "ORDER BY c.check_in_date ASC LIMIT 10")
    List<StayGuestVO> selectDueCheckOutList();
}
