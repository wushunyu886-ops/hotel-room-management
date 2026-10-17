package com.hotel.room.dto;

import com.hotel.room.enums.Orientation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房间类型新增/修改参数
 */
@Data
public class RoomTypeDTO {

    @NotNull(message = "朝向不能为空")
    private Orientation orientation;

    @NotBlank(message = "类型名称不能为空")
    @Size(max = 50, message = "类型名称不能超过 50 字符")
    private String typeName;

    @NotNull(message = "床位数不能为空")
    @Min(value = 1, message = "床位数至少为 1")
    @Max(value = 10, message = "床位数不能超过 10")
    private Integer bedCount;

    @NotNull(message = "每日住宿费不能为空")
    @DecimalMin(value = "0.01", message = "每日住宿费必须大于 0")
    private BigDecimal dailyPrice;

    /** 状态：1-启用 0-停用 */
    private Integer status;
}
