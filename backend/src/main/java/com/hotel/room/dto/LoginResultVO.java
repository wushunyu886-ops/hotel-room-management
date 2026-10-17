package com.hotel.room.dto;

import com.hotel.room.enums.UserRole;
import lombok.Data;

/**
 * 登录结果 VO：Token + 用户信息
 */
@Data
public class LoginResultVO {

    private String token;
    private Long userId;
    private String username;
    private String realName;
    private UserRole role;
}
