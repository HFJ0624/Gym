package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.venue.CourtBookDto;
import com.sau.gym.model.entity.venue.CourtBooking;
import com.sau.gym.model.vo.court.CourtBookVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourtBookingMapper {

    //根据条件查询所有数据
    List<CourtBookVO> findByPage(CourtBookDto courtBookDto);


    //删除预约场地
    void deleteById(Long id);

    //插入预约数据
    void saveCourtBook(CourtBooking courtBooking);
}
