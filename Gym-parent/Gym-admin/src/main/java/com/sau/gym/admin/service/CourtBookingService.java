package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.venue.BookingDto;
import com.sau.gym.model.dto.venue.CourtBookDto;
import com.sau.gym.model.entity.venue.CourtBooking;
import com.sau.gym.model.vo.court.CourtBookVO;

import java.util.List;
import java.util.Map;

public interface CourtBookingService {

    //场地预约的查询功能
    PageInfo<CourtBookVO> findByPage(Integer current, Integer limit, CourtBookDto courtBookDto);

    //删除预约场地
    void deleteById(Long id);

    //下单预约场地
    boolean saveCourtBook(BookingDto bookingDto);

    //查询所有预约记录
    PageInfo<CourtBookVO> getCourtOrder(Long userId,Integer current,Integer limit);

    //统计所有预约总数
    List<CourtBooking> countAllBook();

    //前台用户取消预约订单
    void cancelOrder(Long orderId,String reason);

    /**
     * 统一取消预约入口。
     * 说明：
     * 前台取消预约、Agent 取消预约，都应该走这个方法。
     *
     * @param userId 当前操作用户ID
     * @param orderId 预约订单ID
     * @param reason 取消原因
     * @param source 来源：FRONT / AGENT
     */
    void cancelOrderByUserId(Long userId, Long orderId, String reason, String source);
}
