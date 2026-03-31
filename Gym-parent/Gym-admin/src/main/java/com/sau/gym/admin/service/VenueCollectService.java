package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.venue.VenueCollectDto;
import com.sau.gym.model.vo.venue.VenueCollectVO;

public interface VenueCollectService {

    //场馆收藏分页查询
    PageInfo<VenueCollectVO> findByPage(Integer current, Integer limit, VenueCollectDto venueCollectDto);
}
