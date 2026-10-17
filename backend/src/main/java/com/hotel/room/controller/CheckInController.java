package com.hotel.room.controller;

import com.hotel.room.common.PageResult;
import com.hotel.room.common.Result;
import com.hotel.room.dto.CheckInDTO;
import com.hotel.room.dto.CheckInQueryDTO;
import com.hotel.room.dto.CheckOutDTO;
import com.hotel.room.enums.Gender;
import com.hotel.room.enums.Orientation;
import com.hotel.room.service.CheckInService;
import com.hotel.room.vo.AvailableBedVO;
import com.hotel.room.vo.BillVO;
import com.hotel.room.vo.CheckInVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 入住/退房接口（核心业务）
 */
@Tag(name = "入住退房管理")
@RestController
@RequestMapping("/api/check-ins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @Operation(summary = "办理入住（指定床位或自动推荐，含性别规则校验）")
    @PostMapping
    public Result<CheckInVO> checkIn(@Valid @RequestBody CheckInDTO dto) {
        return Result.ok(checkInService.checkIn(dto));
    }

    @Operation(summary = "分页查询入住记录")
    @GetMapping
    public Result<PageResult<CheckInVO>> page(CheckInQueryDTO query) {
        return Result.ok(checkInService.page(query));
    }

    @Operation(summary = "查询可用床位（入住弹窗推荐/选择）")
    @GetMapping("/available-beds")
    public Result<List<AvailableBedVO>> availableBeds(@RequestParam Gender gender,
                                                      @RequestParam(required = false) Orientation orientation,
                                                      @RequestParam(required = false) Long roomTypeId,
                                                      @RequestParam(required = false) Long roomId) {
        return Result.ok(checkInService.availableBeds(gender, orientation, roomTypeId, roomId));
    }

    @Operation(summary = "办理退房（结算天数与金额、释放床位、生成账单）")
    @PostMapping("/{id}/check-out")
    public Result<BillVO> checkOut(@PathVariable Long id, @RequestBody(required = false) CheckOutDTO dto) {
        return Result.ok(checkInService.checkOut(id, dto == null ? new CheckOutDTO() : dto));
    }
}
