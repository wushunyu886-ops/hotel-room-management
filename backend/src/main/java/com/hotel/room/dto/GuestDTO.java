package com.hotel.room.dto;

import com.hotel.room.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 旅客新增/修改参数
 */
@Data
public class GuestDTO {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过 50 字符")
    private String name;

    @NotBlank(message = "身份证号不能为空")
    @Size(max = 18, message = "身份证号长度不正确")
    private String idCard;

    @NotNull(message = "性别不能为空")
    private Gender gender;

    @Size(max = 20, message = "联系电话长度不正确")
    private String phone;

    @Size(max = 255, message = "住址不能超过 255 字符")
    private String address;
}
