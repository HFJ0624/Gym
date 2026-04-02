package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.venue.VenueCollectDto;
import com.sau.gym.model.vo.venue.VenueCollectVO;

import java.util.Map;

public interface VenueCollectService {

    //场馆收藏分页查询
    PageInfo<VenueCollectVO> findByPage(Integer current, Integer limit, VenueCollectDto venueCollectDto);

    //获取当前用户收藏的场馆列表
    Map<String, Object> selectAllVenuesByUserId(Long id);

    //用户收藏场馆
    void CollectVenue(Long id, Long venueId);

    //用户取消收藏场馆
    void UnCollectVenue(Long id, Long venueId);
}
