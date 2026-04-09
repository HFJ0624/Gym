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

    //添加预约场地
    boolean saveCourtBook(BookingDto bookingDto);

    //查询所有预约记录
    Map<String, Object> getCourtOrder(Long userId);

    //统计所有预约总数
    List<CourtBooking> countAllBook();
}
