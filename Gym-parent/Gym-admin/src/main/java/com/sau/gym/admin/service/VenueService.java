package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.venue.VenueDto;
import com.sau.gym.model.entity.venue.Venue;

public interface VenueService {

    //场馆列表查询方法
    PageInfo<Venue> findByPage(Integer current, Integer limit, VenueDto venueDto);
}
