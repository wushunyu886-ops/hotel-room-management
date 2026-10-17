package com.hotel.room.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.format.DateTimeParseException;

/**
 * 全局异常处理器：把各类异常统一转换为 Result 结构返回
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：返回业务提示信息 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** 参数校验异常：提取第一个校验失败信息 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("请求参数校验失败");
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), message);
    }

    /** JSON 解析异常 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), "请求体格式错误");
    }

    /** 唯一键冲突：主要兜底入住/退房并发场景
     * （uk_active_bed_id 防同一床位重复入住、uk_active_guest_id 防同一旅客重复入住、uk_room_no 等） */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicate(DuplicateKeyException e) {
        log.warn("唯一键冲突: {}", e.getMessage());
        return Result.fail(ResultCode.CONFLICT.getCode(), "数据已存在，或床位/旅客已被并发操作占用，请刷新后重试");
    }

    /** 参数类型不匹配：查询参数类型转换失败（如日期格式错误、Long 传了非数字），统一转 400 */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, BindException.class, DateTimeParseException.class})
    public Result<Void> handleTypeMismatch(Exception e) {
        log.warn("参数类型不匹配: {}", e.getMessage());
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), "请求参数格式不正确，请检查日期等参数格式");
    }

    /** 数据完整性约束冲突：删除被外键引用的数据时兜底转 400 友好提示 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrity(DataIntegrityViolationException e) {
        log.warn("数据完整性约束冲突: {}", e.getMessage());
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), "该数据已被其他记录引用，无法执行该操作");
    }

    /**
     * 静态资源/路径不存在（Spring 6.1+ 会抛 NoResourceFoundException）：
     * 直接访问后端根路径或 favicon.ico 等未匹配资源时返回 404 而不是 500
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResource(NoResourceFoundException e) {
        return Result.fail(ResultCode.NOT_FOUND.getCode(), "资源不存在");
    }

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ResultCode.ERROR.getCode(), "系统繁忙，请稍后重试");
    }
}
