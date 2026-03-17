package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.venue.VenueBookDto;
import com.sau.gym.model.vo.venue.VenueBookVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VenueBookingMapper {

    //查询场馆预约
    List<VenueBookVO> findByPage(VenueBookDto venueBookDto);

    //删除预约场馆
    void deleteById(Long id);
}
