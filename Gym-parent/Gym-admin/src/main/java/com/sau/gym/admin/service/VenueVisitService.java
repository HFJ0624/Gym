package com.sau.gym.admin.service;

public interface VenueVisitService {

    //增加一次场馆访问
    void addVisit(Long venueId);

    //创建场馆的访问记录
    void addOne(Long id);
}
