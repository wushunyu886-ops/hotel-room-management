package com.hotel.room.dto;

import com.hotel.room.enums.PayStatus;
import lombok.Data;

import java.time.LocalDate;

/**
 * 账单分页查询参数
 */
@Data
public class BillQueryDTO {

    private Long pageNum = 1L;
    private Long pageSize = 10L;
    /** 账单号模糊查询 */
    private String billNo;
    /** 旅客姓名模糊查询 */
    private String guestName;
    /** 支付状态 */
    private PayStatus payStatus;
    /** 账单创建日期范围 */
    private LocalDate startDate;
    private LocalDate endDate;
}
