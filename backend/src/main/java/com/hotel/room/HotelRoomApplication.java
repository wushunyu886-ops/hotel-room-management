package com.hotel.room;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 旅馆客房管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.hotel.room.mapper")
public class HotelRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelRoomApplication.class, args);
    }

    /**
     * BCrypt 密码编码器（仅使用 spring-security-crypto 工具类）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
