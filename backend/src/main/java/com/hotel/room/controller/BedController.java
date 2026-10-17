package com.hotel.room.controller;

import com.hotel.room.common.PageResult;
import com.hotel.room.common.Result;
import com.hotel.room.enums.BedStatus;
import com.hotel.room.service.BedService;
import com.hotel.room.vo.BedVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 床位接口
 */
@Tag(name = "床位管理")
@RestController
@RequestMapping("/api/beds")
@RequiredArgsConstructor
public class BedController {

    private final BedService bedService;

    @Operation(summary = "分页查询床位（按房间号/状态/房间筛选）")
    @GetMapping
    public Result<PageResult<BedVO>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                          @RequestParam(defaultValue = "10") Long pageSize,
                                          @RequestParam(required = false) String roomNo,
                                          @RequestParam(required = false) BedStatus status,
                                          @RequestParam(required = false) Long roomId) {
        return Result.ok(bedService.page(pageNum, pageSize, roomNo, status, roomId));
    }
}
