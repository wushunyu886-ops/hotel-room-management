package com.hotel.room.service;

import com.hotel.room.common.PageResult;
import com.hotel.room.dto.BillQueryDTO;
import com.hotel.room.vo.BillVO;

/**
 * 账单服务
 */
public interface BillService {

    PageResult<BillVO> page(BillQueryDTO query);

    BillVO detail(Long id);

    /** 账单支付（扩展接口） */
    BillVO pay(Long id);
}
