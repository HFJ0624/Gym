package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.venue.BookingRefundRequest;
import com.sau.gym.model.vo.refund.BookingRefundRequestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookingRefundRequestMapper {

    /**
     * 查询某个预约是否已经存在未删除的退款申请。
     *
     * @param bookingId 预约订单ID
     * @return 数量
     */
    int countByBookingId(@Param("bookingId") Long bookingId);

    /**
     * 插入退款申请。
     *
     * @param request 退款申请
     * @return 影响行数
     */
    int insertRefundRequest(BookingRefundRequest request);

    /**
     * 后台分页查询退款申请
     */
    List<BookingRefundRequestVO> findRefundPage(@Param("status") Integer status, @Param("keyword") String keyword);

    /**
     * 根据ID查询退款申请详情
     */
    BookingRefundRequestVO selectRefundDetail(@Param("id") Long id);

    /**
     * 审核通过
     * 注意：
     * 必须加 status = 0 条件，防止重复审核。
     */
    int approveRefund(@Param("id") Long id, @Param("auditUserId") Long auditUserId, @Param("auditRemark") String auditRemark);

    /**
     * 审核拒绝
     */
    int rejectRefund(@Param("id") Long id, @Param("auditUserId") Long auditUserId, @Param("auditRemark") String auditRemark);
}
