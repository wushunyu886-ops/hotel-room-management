package com.hotel.room.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.room.common.BusinessException;
import com.hotel.room.dto.LoginDTO;
import com.hotel.room.dto.LoginResultVO;
import com.hotel.room.entity.SysUser;
import com.hotel.room.mapper.SysUserMapper;
import com.hotel.room.service.AuthService;
import com.hotel.room.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResultVO login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        // 用户名不存在或密码不匹配统一提示，避免泄露账号是否存在
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已禁用，请联系管理员");
        }
        LoginResultVO vo = new LoginResultVO();
        vo.setToken(jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name()));
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        return vo;
    }
}
