package com.hotel.room.common;

import lombok.Getter;

/**
 * 统一业务状态码
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证或登录已过期"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "数据冲突"),
    ERROR(500, "系统内部异常");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
