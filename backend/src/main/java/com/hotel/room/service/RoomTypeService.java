package com.hotel.room.service;

import com.hotel.room.common.PageResult;
import com.hotel.room.dto.RoomTypeDTO;
import com.hotel.room.dto.RoomTypeQueryDTO;
import com.hotel.room.entity.RoomType;

import java.util.List;

/**
 * 房间类型服务
 */
public interface RoomTypeService {

    PageResult<RoomType> page(RoomTypeQueryDTO query);

    List<RoomType> listAll();

    RoomType create(RoomTypeDTO dto);

    RoomType update(Long id, RoomTypeDTO dto);

    void delete(Long id);
}
