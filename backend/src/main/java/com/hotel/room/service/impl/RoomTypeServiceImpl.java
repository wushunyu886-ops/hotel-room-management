package com.hotel.room.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.common.BusinessException;
import com.hotel.room.common.PageResult;
import com.hotel.room.dto.RoomTypeDTO;
import com.hotel.room.dto.RoomTypeQueryDTO;
import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomType;
import com.hotel.room.mapper.RoomMapper;
import com.hotel.room.mapper.RoomTypeMapper;
import com.hotel.room.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 房间类型服务实现
 */
@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeMapper roomTypeMapper;
    private final RoomMapper roomMapper;

    @Override
    public PageResult<RoomType> page(RoomTypeQueryDTO query) {
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<RoomType>()
                .eq(query.getOrientation() != null, RoomType::getOrientation, query.getOrientation())
                .like(query.getTypeName() != null && !query.getTypeName().isEmpty(), RoomType::getTypeName, query.getTypeName())
                .eq(query.getStatus() != null, RoomType::getStatus, query.getStatus())
                .orderByAsc(RoomType::getId);
        Page<RoomType> page = roomTypeMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<RoomType> listAll() {
        return roomTypeMapper.selectList(new LambdaQueryWrapper<RoomType>()
                .eq(RoomType::getStatus, 1)
                .orderByAsc(RoomType::getId));
    }

    @Override
    public RoomType create(RoomTypeDTO dto) {
        RoomType roomType = new RoomType();
        copyProperties(dto, roomType);
        roomType.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        roomTypeMapper.insert(roomType);
        return roomType;
    }

    @Override
    public RoomType update(Long id, RoomTypeDTO dto) {
        RoomType roomType = roomTypeMapper.selectById(id);
        if (roomType == null) {
            throw new BusinessException("房间类型不存在");
        }
        copyProperties(dto, roomType);
        roomTypeMapper.updateById(roomType);
        return roomType;
    }

    @Override
    public void delete(Long id) {
        // 已被房间引用时不允许删除（外键约束兜底）
        Long count = roomMapper.selectCount(new LambdaQueryWrapper<Room>().eq(Room::getRoomTypeId, id));
        if (count != null && count > 0) {
            throw new BusinessException("该房型下已存在房间，无法删除");
        }
        roomTypeMapper.deleteById(id);
    }

    private void copyProperties(RoomTypeDTO dto, RoomType roomType) {
        roomType.setOrientation(dto.getOrientation());
        roomType.setTypeName(dto.getTypeName());
        roomType.setBedCount(dto.getBedCount());
        roomType.setDailyPrice(dto.getDailyPrice());
    }
}
