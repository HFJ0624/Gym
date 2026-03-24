package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.venue.CourtDto;
import com.sau.gym.model.entity.venue.Court;
import com.sau.gym.model.vo.court.CourtVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourtMapper {

    //场地查询方法
    List<CourtVO> findByPage(CourtDto courtDto);

    //添加场地
    void saveCourt(Court court);

    //修改场地
    void updateCourt(Court court);

    //删除场地
    void deleteById(Long courtId);

    //查询场馆对应的场地(前台)
    List<CourtVO> getAllCourt(Long venueId);
}
