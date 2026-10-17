package com.hotel.room.controller;

import com.hotel.room.common.Result;
import com.hotel.room.service.StatsService;
import com.hotel.room.vo.DashboardStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页仪表盘接口
 */
@Tag(name = "仪表盘")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StatsService statsService;

    @Operation(summary = "首页统计数据：今日入住/退房/收入、房间状态、待退房、空房列表")
    @GetMapping("/stats")
    public Result<DashboardStatsVO> stats() {
        return Result.ok(statsService.dashboard());
    }
}
