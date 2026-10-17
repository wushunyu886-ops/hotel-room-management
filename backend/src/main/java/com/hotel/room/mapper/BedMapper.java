package com.hotel.room.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.entity.Bed;
import com.hotel.room.enums.BedStatus;
import com.hotel.room.enums.Gender;
import com.hotel.room.enums.Orientation;
import com.hotel.room.vo.AvailableBedVO;
import com.hotel.room.vo.BedVO;
import com.hotel.room.vo.RoomBedVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 床位 Mapper
 */
public interface BedMapper extends BaseMapper<Bed> {

    /**
     * 锁定床位行：并发入住时防止同一床位被重复分配
     */
    @Select("SELECT * FROM bed WHERE id = #{id} FOR UPDATE")
    Bed selectByIdForUpdate(@Param("id") Long id);

    /**
     * 查看某房间所有床位状态（含占用旅客）
     */
    @Select("SELECT b.id AS bedId, b.bed_no AS bedNo, b.status AS bedStatus, " +
            "g.name AS guestName, c.check_in_date AS checkInDate " +
            "FROM bed b " +
            "LEFT JOIN check_in_record c ON c.bed_id = b.id AND c.status = 'CHECKED_IN' " +
            "LEFT JOIN guest g ON g.id = c.guest_id " +
            "WHERE b.room_id = #{roomId} ORDER BY b.bed_no")
    List<RoomBedVO> selectRoomBeds(@Param("roomId") Long roomId);

    /**
     * 自动推荐一个可用床位（入住时未指定床位）：
     * 条件：床位空闲 + 房间可用/在住未满 + 性别匹配 + 未住满；
     * 排序：空房间优先，其次同性别房间
     */
    @Select({
            "<script>",
            "SELECT b.id FROM bed b",
            "JOIN room r ON b.room_id = r.id",
            "JOIN room_type rt ON r.room_type_id = rt.id",
            "WHERE b.status = 'FREE' AND r.status IN ('AVAILABLE', 'OCCUPIED')",
            "AND (r.current_gender IS NULL OR r.current_gender = #{gender})",
            "AND (SELECT COUNT(*) FROM check_in_record c WHERE c.room_id = r.id AND c.status = 'CHECKED_IN') &lt; rt.bed_count",
            "<if test='orientation != null'>AND r.orientation = #{orientation}</if>",
            "<if test='roomTypeId != null'>AND r.room_type_id = #{roomTypeId}</if>",
            "<if test='roomId != null'>AND r.id = #{roomId}</if>",
            "ORDER BY CASE WHEN r.current_gender IS NULL THEN 0 WHEN r.current_gender = #{gender} THEN 1 ELSE 2 END, r.id, b.bed_no",
            "LIMIT 1",
            "</script>"
    })
    Long recommendBedId(@Param("gender") Gender gender,
                        @Param("orientation") Orientation orientation,
                        @Param("roomTypeId") Long roomTypeId,
                        @Param("roomId") Long roomId);

    /**
     * 查询可用床位列表（入住弹窗选择用，规则同推荐）
     */
    @Select({
            "<script>",
            "SELECT b.id AS bedId, b.bed_no AS bedNo, b.room_id AS roomId, r.room_no AS roomNo, r.floor, ",
            "r.orientation, rt.type_name AS typeName, rt.daily_price AS dailyPrice, r.current_gender AS currentGender, b.status AS bedStatus ",
            "FROM bed b",
            "JOIN room r ON b.room_id = r.id",
            "JOIN room_type rt ON r.room_type_id = rt.id",
            "WHERE b.status = 'FREE' AND r.status IN ('AVAILABLE', 'OCCUPIED')",
            "AND (r.current_gender IS NULL OR r.current_gender = #{gender})",
            "AND (SELECT COUNT(*) FROM check_in_record c WHERE c.room_id = r.id AND c.status = 'CHECKED_IN') &lt; rt.bed_count",
            "<if test='orientation != null'>AND r.orientation = #{orientation}</if>",
            "<if test='roomTypeId != null'>AND r.room_type_id = #{roomTypeId}</if>",
            "<if test='roomId != null'>AND r.id = #{roomId}</if>",
            "ORDER BY CASE WHEN r.current_gender IS NULL THEN 0 WHEN r.current_gender = #{gender} THEN 1 ELSE 2 END, r.id, b.bed_no",
            "</script>"
    })
    List<AvailableBedVO> listAvailableBeds(@Param("gender") Gender gender,
                                           @Param("orientation") Orientation orientation,
                                           @Param("roomTypeId") Long roomTypeId,
                                           @Param("roomId") Long roomId);

    /**
     * 分页查询床位（按房间号 / 状态 / 房间）
     */
    @Select({
            "<script>",
            "SELECT b.id AS bedId, b.bed_no AS bedNo, b.status AS bedStatus, b.room_id AS roomId, ",
            "r.room_no AS roomNo, r.orientation, rt.type_name AS typeName, r.current_gender AS currentGender, g.name AS guestName ",
            "FROM bed b",
            "JOIN room r ON b.room_id = r.id",
            "JOIN room_type rt ON rt.id = r.room_type_id",
            "LEFT JOIN check_in_record c ON c.bed_id = b.id AND c.status = 'CHECKED_IN'",
            "LEFT JOIN guest g ON g.id = c.guest_id",
            "WHERE 1=1",
            "<if test='roomNo != null and roomNo != \"\"'>AND r.room_no LIKE CONCAT('%', #{roomNo}, '%')</if>",
            "<if test='status != null'>AND b.status = #{status}</if>",
            "<if test='roomId != null'>AND b.room_id = #{roomId}</if>",
            "ORDER BY b.room_id, b.bed_no",
            "</script>"
    })
    List<BedVO> selectBedPage(Page<BedVO> page,
                              @Param("roomNo") String roomNo,
                              @Param("status") BedStatus status,
                              @Param("roomId") Long roomId);
}
