package com.hotel.room.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hotel.room.entity.SysUser;
import com.hotel.room.enums.UserRole;
import com.hotel.room.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动数据初始化器：
 * 确保存在默认管理员 admin / 123456（密码以 BCrypt 存储），
 * 若数据库中已有 admin 但密码不是 123456，则重置，保证文档账号始终可用
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        SysUser admin = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
        String encoded = passwordEncoder.encode("123456");
        if (admin == null) {
            admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword(encoded);
            admin.setRealName("系统管理员");
            admin.setRole(UserRole.ADMIN);
            admin.setStatus(1);
            sysUserMapper.insert(admin);
            log.info("已初始化默认管理员账号 admin / 123456");
        } else if (!passwordEncoder.matches("123456", admin.getPassword())) {
            admin.setPassword(encoded);
            sysUserMapper.updateById(admin);
            log.info("已重置默认管理员 admin 的密码为 123456");
        }
    }
}
