package com.hotel.room.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.common.PageResult;
import com.hotel.room.enums.BedStatus;
import com.hotel.room.mapper.BedMapper;
import com.hotel.room.service.BedService;
import com.hotel.room.vo.BedVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 床位服务实现
 */
@Service
@RequiredArgsConstructor
public class BedServiceImpl implements BedService {

    private final BedMapper bedMapper;

    @Override
    public PageResult<BedVO> page(Long pageNum, Long pageSize, String roomNo, BedStatus status, Long roomId) {
        Page<BedVO> page = new Page<>(pageNum, pageSize);
        List<BedVO> list = bedMapper.selectBedPage(page, roomNo, status, roomId);
        return new PageResult<>(list, page.getTotal(), page.getCurrent(), page.getSize());
    }
}
