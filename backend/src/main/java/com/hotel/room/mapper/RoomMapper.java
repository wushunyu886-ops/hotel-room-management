package com.hotel.room.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.entity.Room;
import com.hotel.room.enums.Orientation;
import com.hotel.room.enums.RoomStatus;
import com.hotel.room.vo.AvailableRoomVO;
import com.hotel.room.vo.RoomVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房间 Mapper
 */
public interface RoomMapper extends BaseMapper<Room> {

    /**
     * 锁定房间行（SELECT ... FOR UPDATE），用于入住/退房事务，防止并发状态误判
     */
    @Select("SELECT * FROM room WHERE id = #{id} FOR UPDATE")
    Room selectByIdForUpdate(@Param("id") Long id);

    /**
     * 查询空房列表（仪表盘使用）
     */
    @Select("SELECT r.id AS roomId, r.room_no AS roomNo, r.orientation, r.floor, " +
            "rt.type_name AS typeName, rt.bed_count AS bedCount, r.status " +
            "FROM room r JOIN room_type rt ON r.room_type_id = rt.id " +
            "WHERE r.status = 'AVAILABLE' ORDER BY r.room_no")
    List<AvailableRoomVO> listAvailableRooms();

    /**
     * 分页查询房间（联表房型信息 + 在住人数）
     */
    @Select({
            "<script>",
            "SELECT r.id, r.room_no AS roomNo, r.room_type_id AS roomTypeId, r.orientation, r.floor, ",
            "r.status, r.current_gender AS currentGender, r.version, ",
            "rt.type_name AS typeName, rt.bed_count AS bedCount, ",
            "(SELECT COUNT(*) FROM check_in_record c WHERE c.room_id = r.id AND c.status = 'CHECKED_IN') AS occupiedBeds ",
            "FROM room r JOIN room_type rt ON r.room_type_id = rt.id ",
            "WHERE 1=1",
            "<if test='roomNo != null and roomNo != \"\"'>AND r.room_no LIKE CONCAT('%', #{roomNo}, '%')</if>",
            "<if test='orientation != null'>AND r.orientation = #{orientation}</if>",
            "<if test='roomTypeId != null'>AND r.room_type_id = #{roomTypeId}</if>",
            "<if test='status != null'>AND r.status = #{status}</if>",
            "ORDER BY r.room_no",
            "</script>"
    })
    List<RoomVO> selectRoomPage(Page<RoomVO> page,
                                @Param("roomNo") String roomNo,
                                @Param("orientation") Orientation orientation,
                                @Param("roomTypeId") Long roomTypeId,
                                @Param("status") RoomStatus status);
}
