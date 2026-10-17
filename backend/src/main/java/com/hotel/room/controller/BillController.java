package com.hotel.room.controller;

import com.hotel.room.common.PageResult;
import com.hotel.room.common.Result;
import com.hotel.room.dto.BillQueryDTO;
import com.hotel.room.service.BillService;
import com.hotel.room.vo.BillVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账单接口
 */
@Tag(name = "账单管理")
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @Operation(summary = "分页查询账单")
    @GetMapping
    public Result<PageResult<BillVO>> page(BillQueryDTO query) {
        return Result.ok(billService.page(query));
    }

    @Operation(summary = "账单详情")
    @GetMapping("/{id}")
    public Result<BillVO> detail(@PathVariable Long id) {
        return Result.ok(billService.detail(id));
    }

    @Operation(summary = "账单支付（扩展接口）")
    @PostMapping("/{id}/pay")
    public Result<BillVO> pay(@PathVariable Long id) {
        return Result.ok(billService.pay(id));
    }
}
