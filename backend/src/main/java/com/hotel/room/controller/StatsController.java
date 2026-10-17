package com.hotel.room.controller;

import com.hotel.room.common.Result;
import com.hotel.room.service.StatsService;
import com.hotel.room.vo.IncomeTrendVO;
import com.hotel.room.vo.OccupancyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计报表接口
 */
@Tag(name = "统计报表")
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "入住率统计（按房型/性别，默认近 7 天）")
    @GetMapping("/occupancy")
    public Result<OccupancyVO> occupancy(@RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.ok(statsService.occupancy(startDate, endDate));
    }

    @Operation(summary = "每日收入趋势（默认近 7 天，缺失日期补 0）")
    @GetMapping("/income")
    public Result<List<IncomeTrendVO>> income(@RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.ok(statsService.incomeTrend(startDate, endDate));
    }
}
