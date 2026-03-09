package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.venue.VenueDto;
import com.sau.gym.model.entity.venue.Venue;

public interface VenueService {

    //场馆列表查询方法
    PageInfo<Venue> findByPage(Integer current, Integer limit, VenueDto venueDto);

    //添加场馆
    void saveVenue(Venue venue);

    //修改场馆
    void updateVenue(Venue venue);

    //删除场馆
    void deleteById(Long venueId);
}
