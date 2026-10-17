package com.hotel.room.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体（可选）
 */
@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人 ID */
    private Long userId;

    /** 模块 */
    private String module;

    /** 动作 */
    private String action;

    /** 详情 */
    private String detail;

    /** 操作 IP */
    private String ip;

    private LocalDateTime createTime;
}
