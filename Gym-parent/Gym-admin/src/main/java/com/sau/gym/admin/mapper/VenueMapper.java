package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.venue.VenueDto;
import com.sau.gym.model.entity.venue.Venue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    //查找所有场馆(前台)
    List<Venue> getAllVenue(VenueDto venueDto);

    //在前台个人中心获取收藏的场馆详情
    List<Venue> selectByUserId(Long id);

    //修改场馆状态
    int updateVenueStatus(Long id, String status);

    List<Long> getVenueByRate();

    // 2. 根据ID集合批量查询场馆信息
    List<Venue> selectVenueBatchByIds(@Param("ids") List<Long> ids);
}
