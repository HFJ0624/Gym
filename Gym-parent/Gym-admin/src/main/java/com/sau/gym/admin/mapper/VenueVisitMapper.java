package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.venue.VenueVisit;
import com.sau.gym.model.vo.venue.VenueVisitVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VenueVisitMapper {

    //增加一次场馆访问
    void addVisit(Long venueId);

    //创建场馆的访问记录
    void addOne(Long id);

    //查询所有访问量
    List<VenueVisitVO> selectALL();
}
