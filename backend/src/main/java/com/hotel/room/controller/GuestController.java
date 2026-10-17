package com.hotel.room.controller;

import com.hotel.room.common.PageResult;
import com.hotel.room.common.Result;
import com.hotel.room.dto.GuestDTO;
import com.hotel.room.dto.GuestQueryDTO;
import com.hotel.room.entity.Guest;
import com.hotel.room.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 旅客接口
 */
@Tag(name = "旅客管理")
@RestController
@RequestMapping("/api/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @Operation(summary = "分页查询旅客（按姓名/身份证/手机号筛选）")
    @GetMapping
    public Result<PageResult<Guest>> page(GuestQueryDTO query) {
        return Result.ok(guestService.page(query));
    }

    @Operation(summary = "旅客登记（身份证号唯一，18 位校验）")
    @PostMapping
    public Result<Guest> create(@Valid @RequestBody GuestDTO dto) {
        return Result.ok(guestService.create(dto));
    }

    @Operation(summary = "修改旅客信息")
    @PutMapping("/{id}")
    public Result<Guest> update(@PathVariable Long id, @Valid @RequestBody GuestDTO dto) {
        return Result.ok(guestService.update(id, dto));
    }

    @Operation(summary = "按 ID 查询旅客详情")
    @GetMapping("/{id}")
    public Result<Guest> detail(@PathVariable Long id) {
        return Result.ok(guestService.getById(id));
    }
}
