package com.hotel.room.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.room.common.BusinessException;
import com.hotel.room.common.PageResult;
import com.hotel.room.dto.BillQueryDTO;
import com.hotel.room.entity.Bill;
import com.hotel.room.entity.PaymentRecord;
import com.hotel.room.enums.PayStatus;
import com.hotel.room.mapper.BillMapper;
import com.hotel.room.mapper.PaymentRecordMapper;
import com.hotel.room.service.BillService;
import com.hotel.room.util.BizValidator;
import com.hotel.room.vo.BillVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单服务实现
 */
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillMapper billMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    @Override
    public PageResult<BillVO> page(BillQueryDTO query) {
        Page<BillVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        List<BillVO> list = billMapper.selectBillPage(page, query.getBillNo(), query.getGuestName(),
                query.getPayStatus(), query.getStartDate(), query.getEndDate());
        return new PageResult<>(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public BillVO detail(Long id) {
        BillVO vo = billMapper.selectBillDetail(id);
        BizValidator.notNull(vo, "账单不存在");
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillVO pay(Long id) {
        Bill bill = billMapper.selectById(id);
        BizValidator.notNull(bill, "账单不存在");
        BizValidator.requireTrue(bill.getPayStatus() != PayStatus.PAID, "该账单已支付，请勿重复操作");

        // 更新账单支付状态与支付时间
        Bill update = new Bill();
        update.setId(id);
        update.setPayStatus(PayStatus.PAID);
        update.setPayTime(LocalDateTime.now());
        billMapper.updateById(update);

        // 记录支付流水
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setBillId(id);
        paymentRecord.setPayMethod("CASH");
        paymentRecord.setAmount(bill.getTotalAmount() == null ? BigDecimal.ZERO : bill.getTotalAmount());
        paymentRecord.setPayTime(LocalDateTime.now());
        paymentRecordMapper.insert(paymentRecord);

        return billMapper.selectBillDetail(id);
    }
}
