package com.hotel.room.dto;

import com.hotel.room.enums.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 房间新增/修改参数
 */
@Data
public class RoomDTO {

    @NotBlank(message = "房间号不能为空")
    @Size(max = 20, message = "房间号不能超过 20 字符")
    private String roomNo;

    @NotNull(message = "房间类型不能为空")
    private Long roomTypeId;

    /** 楼层 */
    private Integer floor;

    /** 状态：AVAILABLE / OCCUPIED / MAINTENANCE（编辑时使用） */
    private RoomStatus status;
}
