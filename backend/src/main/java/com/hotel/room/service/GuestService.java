package com.hotel.room.service;

import com.hotel.room.common.PageResult;
import com.hotel.room.dto.GuestDTO;
import com.hotel.room.dto.GuestQueryDTO;
import com.hotel.room.entity.Guest;

/**
 * 旅客服务
 */
public interface GuestService {

    PageResult<Guest> page(GuestQueryDTO query);

    /** 旅客登记（身份证号唯一 + 校验） */
    Guest create(GuestDTO dto);

    Guest update(Long id, GuestDTO dto);

    Guest getById(Long id);
}
