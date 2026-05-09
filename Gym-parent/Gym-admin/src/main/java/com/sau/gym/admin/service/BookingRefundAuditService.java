package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.refund.RefundAuditDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.vo.refund.BookingRefundRequestVO;

public interface BookingRefundAuditService {

    /**
     * 分页查询退款申请
     */
    PageInfo<BookingRefundRequestVO> findByPage(Integer current, Integer limit, Integer status, String keyword);

    /**
     * 审核通过
     */
    void approve(RefundAuditDto dto);

    /**
     * 审核拒绝
     */
    void reject(RefundAuditDto dto);
}
