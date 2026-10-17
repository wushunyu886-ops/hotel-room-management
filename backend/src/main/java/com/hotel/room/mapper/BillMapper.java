package com.hotel.room.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.entity.Bill;
import com.hotel.room.enums.PayStatus;
import com.hotel.room.vo.BillVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 账单 Mapper
 */
public interface BillMapper extends BaseMapper<Bill> {

    /**
     * 分页查询账单（联表旅客与入住记录）
     */
    @Select({
            "<script>",
            "SELECT b.id, b.bill_no AS billNo, b.check_in_record_id AS checkInRecordId, b.guest_id AS guestId, ",
            "g.name AS guestName, g.id_card AS idCard, b.room_no AS roomNo, b.bed_no AS bedNo, ",
            "b.days, b.daily_price AS dailyPrice, b.total_amount AS totalAmount, ",
            "b.pay_status AS payStatus, b.pay_time AS payTime, b.create_time AS createTime, ",
            "c.check_in_date AS checkInDate, c.check_out_date AS checkOutDate ",
            "FROM bill b ",
            "JOIN guest g ON g.id = b.guest_id ",
            "LEFT JOIN check_in_record c ON c.id = b.check_in_record_id ",
            "WHERE 1=1",
            "<if test='billNo != null and billNo != \"\"'>AND b.bill_no LIKE CONCAT('%', #{billNo}, '%')</if>",
            "<if test='guestName != null and guestName != \"\"'>AND g.name LIKE CONCAT('%', #{guestName}, '%')</if>",
            "<if test='payStatus != null'>AND b.pay_status = #{payStatus}</if>",
            "<if test='startDate != null'>AND DATE(b.create_time) &gt;= #{startDate}</if>",
            "<if test='endDate != null'>AND DATE(b.create_time) &lt;= #{endDate}</if>",
            "ORDER BY b.create_time DESC",
            "</script>"
    })
    List<BillVO> selectBillPage(Page<BillVO> page,
                                @Param("billNo") String billNo,
                                @Param("guestName") String guestName,
                                @Param("payStatus") PayStatus payStatus,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    /**
     * 账单详情（联表）
     */
    @Select("SELECT b.id, b.bill_no AS billNo, b.check_in_record_id AS checkInRecordId, b.guest_id AS guestId, " +
            "g.name AS guestName, g.id_card AS idCard, b.room_no AS roomNo, b.bed_no AS bedNo, " +
            "b.days, b.daily_price AS dailyPrice, b.total_amount AS totalAmount, " +
            "b.pay_status AS payStatus, b.pay_time AS payTime, b.create_time AS createTime, " +
            "c.check_in_date AS checkInDate, c.check_out_date AS checkOutDate " +
            "FROM bill b " +
            "JOIN guest g ON g.id = b.guest_id " +
            "LEFT JOIN check_in_record c ON c.id = b.check_in_record_id " +
            "WHERE b.id = #{id}")
    BillVO selectBillDetail(@Param("id") Long id);
}
