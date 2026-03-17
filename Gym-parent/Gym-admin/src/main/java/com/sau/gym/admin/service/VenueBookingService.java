package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.venue.VenueBookDto;
import com.sau.gym.model.vo.venue.VenueBookVO;

public interface VenueBookingService {

    //场馆预约的查询功能
    PageInfo<VenueBookVO> findByPage(Integer current, Integer limit, VenueBookDto venueBookDto);

    //删除预约场馆
    void deleteById(Long id);
}
