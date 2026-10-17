package com.hotel.room.service;

import com.hotel.room.dto.LoginDTO;
import com.hotel.room.dto.LoginResultVO;

/**
 * 认证服务
 */
public interface AuthService {

    /** 登录：校验用户名密码，签发 JWT */
    LoginResultVO login(LoginDTO dto);
}
