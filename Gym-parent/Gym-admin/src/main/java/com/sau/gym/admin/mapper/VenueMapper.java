package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.venue.VenueDto;
import com.sau.gym.model.entity.venue.Venue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VenueMapper {

    //根据条件查询所有数据
    List<Venue> findByPage(VenueDto venueDto);
}
