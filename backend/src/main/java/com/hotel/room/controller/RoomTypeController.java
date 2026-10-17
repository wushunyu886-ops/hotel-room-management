package com.hotel.room.controller;

import com.hotel.room.common.PageResult;
import com.hotel.room.common.Result;
import com.hotel.room.dto.RoomTypeDTO;
import com.hotel.room.dto.RoomTypeQueryDTO;
import com.hotel.room.entity.RoomType;
import com.hotel.room.service.RoomTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 房间类型接口
 */
@Tag(name = "房间类型管理")
@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @Operation(summary = "分页查询房间类型（支持朝向/名称/状态筛选）")
    @GetMapping
    public Result<PageResult<RoomType>> page(RoomTypeQueryDTO query) {
        return Result.ok(roomTypeService.page(query));
    }

    @Operation(summary = "查询全部启用房型（下拉框用）")
    @GetMapping("/all")
    public Result<List<RoomType>> listAll() {
        return Result.ok(roomTypeService.listAll());
    }

    @Operation(summary = "新增房间类型")
    @PostMapping
    public Result<RoomType> create(@Valid @RequestBody RoomTypeDTO dto) {
        return Result.ok(roomTypeService.create(dto));
    }

    @Operation(summary = "修改房间类型")
    @PutMapping("/{id}")
    public Result<RoomType> update(@PathVariable Long id, @Valid @RequestBody RoomTypeDTO dto) {
        return Result.ok(roomTypeService.update(id, dto));
    }

    @Operation(summary = "删除房间类型（已被房间引用时禁止删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomTypeService.delete(id);
        return Result.ok();
    }
}
