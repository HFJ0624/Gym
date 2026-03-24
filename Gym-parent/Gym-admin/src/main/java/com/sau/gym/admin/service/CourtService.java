package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.venue.CourtDto;
import com.sau.gym.model.entity.venue.Court;
import com.sau.gym.model.vo.court.CourtVO;

import java.util.Map;

public interface CourtService {

    //场地查询方法
    PageInfo<CourtVO> findByPage(Integer current, Integer limit, CourtDto courtDto);

    //添加场地
    void saveCourt(Court court);

    //修改场地
    void updateCourt(Court court);

    //删除场地
    void deleteById(Long courtId);

    //查询场馆对应的场地
    Map<String, Object> getAllCourt(Long venueId);
}
