package com.sau.gym.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VenueVisitMapper {

    //增加一次场馆访问
    void addVisit(Long venueId);

    //创建场馆的访问记录
    void addOne(Long id);
}
