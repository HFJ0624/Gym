package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.venue.BookingRefundRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
