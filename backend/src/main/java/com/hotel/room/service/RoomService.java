package com.hotel.room.service;

import com.hotel.room.common.PageResult;
import com.hotel.room.dto.RoomDTO;
import com.hotel.room.dto.RoomQueryDTO;
import com.hotel.room.vo.RoomBedVO;
import com.hotel.room.vo.RoomVO;

import java.util.List;

/**
 * 房间服务
 */
public interface RoomService {

    PageResult<RoomVO> page(RoomQueryDTO query);

    /** 新增房间并按其房型自动生成床位 */
    RoomVO create(RoomDTO dto);

    RoomVO update(Long id, RoomDTO dto);

    void delete(Long id);

    /** 查看某房间所有床位状态 */
    List<RoomBedVO> listBeds(Long roomId);
}
