package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.venue.VenueCollectDto;
import com.sau.gym.model.vo.venue.VenueCollectVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VenueCollectMapper {

    //场馆收藏分页查询
    List<VenueCollectVO> findByPage(VenueCollectDto venueCollectDto);
}
