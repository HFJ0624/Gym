package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.venue.VenueDto;
import com.sau.gym.model.entity.venue.Venue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VenueMapper {

    //根据条件查询所有数据
    List<Venue> findByPage(VenueDto venueDto);

    //添加场馆
    void saveVenue(Venue venue);

    //修改场馆
    void updateVenue(Venue venue);

    //删除场馆
    void deleteById(Long venueId);

    //查找所有场馆
    List<Venue> findAllVenue();

    //命名重复判断
    Venue selectOne(String venueName);
}
