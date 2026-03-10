package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.venue.CourtDto;
import com.sau.gym.model.entity.venue.Court;
import com.sau.gym.model.vo.court.CourtVO;

public interface CourtService {

    //场地查询方法
    PageInfo<CourtVO> findByPage(Integer current, Integer limit, CourtDto courtDto);
}
