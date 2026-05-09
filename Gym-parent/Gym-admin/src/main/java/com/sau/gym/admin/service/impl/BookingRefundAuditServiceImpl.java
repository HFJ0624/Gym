package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.*;
import com.sau.gym.admin.service.BookingRefundAuditService;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.dto.refund.RefundAuditDto;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.finance.BalanceRecord;
import com.sau.gym.model.entity.finance.PaymentRecord;
import com.sau.gym.model.entity.finance.RefundRecord;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.entity.user.UserBalance;
import com.sau.gym.model.vo.refund.BookingRefundRequestVO;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

/**
 * 作者:hfj
 * 功能:预约退款审核服务实现
 * 核心规则：
 * 1. 只有 status=0 的退款申请可以审核
 * 2. 审核通过后才真正加回 user_balance
 * 3. 审核通过会插入 refund_record 和 balance_record
 * 4. 使用事务保证余额、流水、审核状态一致
 * 日期: 2026/5/9 11:26
 */
@Service
public class BookingRefundAuditServiceImpl implements BookingRefundAuditService {

    private final BookingRefundRequestMapper refundRequestMapper;
    private final UserBalanceMapper userBalanceMapper;
    private final BalanceRecordMapper balanceRecordMapper;
    private final RefundRecordMapper refundRecordMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    public BookingRefundAuditServiceImpl(BookingRefundRequestMapper refundRequestMapper,
                                         UserBalanceMapper userBalanceMapper,
                                         BalanceRecordMapper balanceRecordMapper,
                                         RefundRecordMapper refundRecordMapper,
                                         PaymentRecordMapper paymentRecordMapper) {
        this.refundRequestMapper = refundRequestMapper;
        this.userBalanceMapper = userBalanceMapper;
        this.balanceRecordMapper = balanceRecordMapper;
        this.refundRecordMapper = refundRecordMapper;
        this.paymentRecordMapper = paymentRecordMapper;
    }

    /**
     * 分页查询退款申请
     */
    @Override
    public PageInfo<BookingRefundRequestVO> findByPage(Integer current, Integer limit, Integer status, String keyword) {
        PageHelper.startPage(current, limit);

        List<BookingRefundRequestVO> list = refundRequestMapper.findRefundPage(status, keyword);

        return new PageInfo<>(list);
    }

    /**
     * 审核通过
     *
     * 重点：
     * 这个方法会真正把钱加回 user_balance。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approve(RefundAuditDto dto) {
        if (dto == null || dto.getId() == null) {
            throw new SauException(ResultCodeEnum.DATA_ERROR);
        }

        User admin = AuthContextUtil.get();
        Long auditUserId = admin == null ? null : admin.getId();

        /*
         * 1. 查询退款申请详情
         */
        BookingRefundRequestVO request = refundRequestMapper.selectRefundDetail(dto.getId());

        if (request == null) {
            throw new SauException(ResultCodeEnum.DATA_ERROR);
        }

        if (request.getStatus() == null || request.getStatus() != 0) {
            throw new SauException(ResultCodeEnum.DATA_ERROR);
        }

        /*
         * 2. 先把申请状态从待审核改为已通过
         *
         * SQL 里带 status=0 条件。
         * 如果 rows = 0，说明已经被别人审核过了，防止重复退款。
         */
        int rows = refundRequestMapper.approveRefund(
                dto.getId(),
                auditUserId,
                dto.getAuditRemark()
        );

        if (rows <= 0) {
            throw new SauException(ResultCodeEnum.DATA_ERROR);
        }

        /*
         * 3. 查询用户余额
         */
        UserBalance userBalance = userBalanceMapper.GetBalanceById(request.getUserId());

        if (userBalance == null) {
            userBalanceMapper.insertOne(request.getUserId());
            userBalance = userBalanceMapper.GetBalanceById(request.getUserId());
        }

        BigDecimal beforeBalance = userBalance.getBalance() == null
                ? BigDecimal.ZERO
                : userBalance.getBalance();

        BigDecimal refundAmount = request.getRefundAmount() == null
                ? BigDecimal.ZERO
                : request.getRefundAmount();

        BigDecimal afterBalance = beforeBalance.add(refundAmount);

        Date now = new Date();

        /*
         * 4. 更新用户余额
         */
        userBalanceMapper.updateBalance(request.getUserId(), afterBalance, now);

        /*
         * 5. 插入余额流水
         */
        BalanceRecord balanceRecord = new BalanceRecord();
        balanceRecord.setUserId(request.getUserId());
        balanceRecord.setType(3);
        balanceRecord.setAmount(refundAmount);
        balanceRecord.setBeforeBalance(beforeBalance);
        balanceRecord.setAfterBalance(afterBalance);
        balanceRecord.setOrderNo(request.getOrderNo());
        balanceRecord.setRemark("后台审核通过预约退款，退款金额：" + refundAmount + "元，退款申请ID：" + request.getId());
        balanceRecord.setCreateTime(now);

        balanceRecordMapper.insertOne(balanceRecord);

        /*
         * 6. 插入退款流水
         */
        RefundRecord refundRecord = new RefundRecord();
        refundRecord.setRefundNo(generateRefundNo());
        refundRecord.setRefundAmount(refundAmount);
        refundRecord.setOrderType(1);
        refundRecord.setOrderNo(request.getOrderNo());
        refundRecord.setStatus(1);
        refundRecord.setUserId(request.getUserId());
        refundRecord.setCreateTime(now);
        refundRecord.setRefundTime(now);

        /*
         * 查询原支付流水，用于记录 payNo。
         * 如果查不到，也不阻断退款，因为余额已经由订单号和退款申请关联。
         */
        PaymentRecord paymentRecord = paymentRecordMapper.selectOne(request.getOrderNo());
        if (paymentRecord != null) {
            refundRecord.setPayNo(paymentRecord.getPayNo());
        }

        refundRecordMapper.insertOne(refundRecord);
    }

    /**
     * 审核拒绝
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reject(RefundAuditDto dto) {
        if (dto == null || dto.getId() == null) {
            throw new SauException(ResultCodeEnum.DATA_ERROR);
        }

        User admin = AuthContextUtil.get();
        Long auditUserId = admin == null ? null : admin.getId();

        int rows = refundRequestMapper.rejectRefund(
                dto.getId(),
                auditUserId,
                dto.getAuditRemark()
        );

        if (rows <= 0) {
            throw new SauException(ResultCodeEnum.DATA_ERROR);
        }
    }

    /**
     * 生成退款流水号
     */
    private String generateRefundNo() {
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder("RF");

        for (int i = 0; i < 18; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}
