package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.venue.VenueCollectDto;
import com.sau.gym.model.entity.venue.VenueCollect;
import com.sau.gym.model.vo.venue.VenueCollectVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VenueCollectMapper {

    //场馆收藏分页查询
    List<VenueCollectVO> findByPage(VenueCollectDto venueCollectDto);

    //获取当前用户收藏的场馆列表
    List<VenueCollect> selectAllVenuesByUserId(Long id);

    //用户收藏场馆
    void CollectVenue(Long id, Long venueId);

    //用户取消收藏场馆
    void UnCollectVenue(Long id, Long venueId);
}
