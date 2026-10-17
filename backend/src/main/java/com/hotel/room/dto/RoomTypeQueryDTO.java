package com.hotel.room.dto;

import com.hotel.room.enums.Orientation;
import lombok.Data;

/**
 * 房间类型分页查询参数
 */
@Data
public class RoomTypeQueryDTO {

    private Long pageNum = 1L;
    private Long pageSize = 10L;
    /** 朝向筛选 */
    private Orientation orientation;
    /** 类型名称模糊查询 */
    private String typeName;
    /** 状态：1-启用 0-停用 */
    private Integer status;
}
