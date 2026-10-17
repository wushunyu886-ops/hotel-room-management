package com.hotel.room.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.common.BusinessException;
import com.hotel.room.common.PageResult;
import com.hotel.room.dto.GuestDTO;
import com.hotel.room.dto.GuestQueryDTO;
import com.hotel.room.entity.Guest;
import com.hotel.room.mapper.GuestMapper;
import com.hotel.room.service.GuestService;
import com.hotel.room.util.BizValidator;
import com.hotel.room.util.IdCardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 旅客服务实现
 */
@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

    private final GuestMapper guestMapper;

    @Override
    public PageResult<Guest> page(GuestQueryDTO query) {
        LambdaQueryWrapper<Guest> wrapper = new LambdaQueryWrapper<Guest>()
                .like(query.getName() != null && !query.getName().isEmpty(), Guest::getName, query.getName())
                .like(query.getIdCard() != null && !query.getIdCard().isEmpty(), Guest::getIdCard, query.getIdCard())
                .like(query.getPhone() != null && !query.getPhone().isEmpty(), Guest::getPhone, query.getPhone())
                .orderByDesc(Guest::getCreateTime);
        Page<Guest> page = guestMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public Guest create(GuestDTO dto) {
        // 身份证号 18 位 + 校验位、手机号 11 位
        BizValidator.validIdCard(dto.getIdCard(), "身份证号格式不正确");
        BizValidator.validPhone(dto.getPhone(), "手机号格式不正确（11 位）");
        Long count = guestMapper.selectCount(new LambdaQueryWrapper<Guest>().eq(Guest::getIdCard, dto.getIdCard()));
        BizValidator.requireTrue(count == null || count == 0, "该身份证号已登记，请勿重复录入");

        Guest guest = new Guest();
        apply(dto, guest);
        guestMapper.insert(guest);
        return guest;
    }

    @Override
    public Guest update(Long id, GuestDTO dto) {
        Guest guest = guestMapper.selectById(id);
        BizValidator.notNull(guest, "旅客不存在");
        BizValidator.validIdCard(dto.getIdCard(), "身份证号格式不正确");
        BizValidator.validPhone(dto.getPhone(), "手机号格式不正确（11 位）");
        // 身份证号变更时校验唯一（排除自身）
        if (!guest.getIdCard().equals(dto.getIdCard())) {
            Long count = guestMapper.selectCount(new LambdaQueryWrapper<Guest>()
                    .eq(Guest::getIdCard, dto.getIdCard()).ne(Guest::getId, id));
            BizValidator.requireTrue(count == null || count == 0, "该身份证号已登记");
        }
        apply(dto, guest);
        guestMapper.updateById(guest);
        return guest;
    }

    @Override
    public Guest getById(Long id) {
        Guest guest = guestMapper.selectById(id);
        BizValidator.notNull(guest, "旅客不存在");
        return guest;
    }

    private void apply(GuestDTO dto, Guest guest) {
        guest.setName(dto.getName());
        guest.setIdCard(dto.getIdCard());
        guest.setGender(dto.getGender());
        guest.setPhone(dto.getPhone());
        guest.setAddress(dto.getAddress());
    }
}
