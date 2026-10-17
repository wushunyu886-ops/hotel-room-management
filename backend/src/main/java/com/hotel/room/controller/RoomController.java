package com.hotel.room.controller;

import com.hotel.room.common.PageResult;
import com.hotel.room.common.Result;
import com.hotel.room.dto.RoomDTO;
import com.hotel.room.dto.RoomQueryDTO;
import com.hotel.room.service.RoomService;
import com.hotel.room.vo.RoomBedVO;
import com.hotel.room.vo.RoomVO;
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
 * 房间接口
 */
@Tag(name = "房间管理")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "分页查询房间")
    @GetMapping
    public Result<PageResult<RoomVO>> page(RoomQueryDTO query) {
        return Result.ok(roomService.page(query));
    }

    @Operation(summary = "新增房间（自动按房型生成床位）")
    @PostMapping
    public Result<RoomVO> create(@Valid @RequestBody RoomDTO dto) {
        return Result.ok(roomService.create(dto));
    }

    @Operation(summary = "修改房间（可停用/启用、变更房型并同步床位）")
    @PutMapping("/{id}")
    public Result<RoomVO> update(@PathVariable Long id, @Valid @RequestBody RoomDTO dto) {
        return Result.ok(roomService.update(id, dto));
    }

    @Operation(summary = "删除房间（存在入住记录或床位占用时禁止删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "查看某房间所有床位状态")
    @GetMapping("/{id}/beds")
    public Result<List<RoomBedVO>> beds(@PathVariable Long id) {
        return Result.ok(roomService.listBeds(id));
    }
}
