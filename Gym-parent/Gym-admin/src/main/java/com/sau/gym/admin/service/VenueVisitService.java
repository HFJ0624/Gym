package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.entity.venue.VenueVisit;
import com.sau.gym.model.vo.venue.VenueVisitVO;

public interface VenueVisitService {

    //增加一次场馆访问
    void addVisit(Long venueId);

    //创建场馆的访问记录
    void addOne(Long id);

    //场馆访问量分页查询
    PageInfo<VenueVisitVO> findByPage(Integer current, Integer limit);
}
