package com.sau.gym.admin.controller.admin;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.BookingRefundAuditService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.refund.RefundAuditDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.refund.BookingRefundRequestVO;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/9 11:29
 */
@RestController
@RequestMapping("/admin/booking/refund")
public class BookingRefundController {

    private final BookingRefundAuditService bookingRefundAuditService;

    public BookingRefundController(BookingRefundAuditService bookingRefundAuditService) {
        this.bookingRefundAuditService = bookingRefundAuditService;
    }

    /**
     * 分页查询退款申请
     *
     * status:
     * 0 待审核
     * 1 已通过
     * 2 已拒绝
     */
    @GetMapping("/page")
    public Result<Page<BookingRefundRequestVO>> page(@RequestParam(defaultValue = "1") Integer current,
                                                     @RequestParam(defaultValue = "10") Integer limit,
                                                     @RequestParam(required = false) Integer status,
                                                     @RequestParam(required = false) String keyword) {

        PageInfo<BookingRefundRequestVO> pageInfo = bookingRefundAuditService.findByPage(current, limit, status, keyword);

        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    /**
     * 审核通过
     */
    @Log(title = "后台审核通过预约退款", businessType = 2, operatorType = OperatorType.MANAGE)
    @PostMapping("/approve")
    public Result approve(@RequestBody RefundAuditDto dto) {
        bookingRefundAuditService.approve(dto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 审核拒绝
     */
    @Log(title = "后台审核拒绝预约退款", businessType = 2, operatorType = OperatorType.MANAGE)
    @PostMapping("/reject")
    public Result reject(@RequestBody RefundAuditDto dto) {
        bookingRefundAuditService.reject(dto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }
}
