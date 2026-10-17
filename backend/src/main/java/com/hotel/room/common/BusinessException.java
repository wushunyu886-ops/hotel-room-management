package com.hotel.room.common;

import lombok.Getter;

/**
 * 业务异常：用于抛出可预期的业务错误，由全局异常处理器统一转换为友好提示
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BAD_REQUEST.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
