package com.hotel.room.dto;

import lombok.Data;

/**
 * 旅客分页查询参数
 */
@Data
public class GuestQueryDTO {

    private Long pageNum = 1L;
    private Long pageSize = 10L;
    /** 姓名模糊查询 */
    private String name;
    /** 身份证号模糊查询 */
    private String idCard;
    /** 手机号模糊查询 */
    private String phone;
}
