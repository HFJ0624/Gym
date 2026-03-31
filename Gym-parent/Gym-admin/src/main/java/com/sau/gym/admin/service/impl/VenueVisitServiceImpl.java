package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.VenueVisitMapper;
import com.sau.gym.admin.service.VenueVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/31 19:49
 */
@Service
public class VenueVisitServiceImpl implements VenueVisitService {

    @Autowired
    private VenueVisitMapper venueVisitMapper;

    //增加一次场馆访问
    @Override
    public void addVisit(Long venueId) {
        venueVisitMapper.addVisit(venueId);
    }

    //创建场馆的访问记录
    @Override
    public void addOne(Long id) {
        venueVisitMapper.addOne(id);
    }
}
