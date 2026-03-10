package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.CourtMapper;
import com.sau.gym.admin.service.CourtService;
import com.sau.gym.model.dto.venue.CourtDto;
import com.sau.gym.model.entity.venue.Court;
import com.sau.gym.model.vo.court.CourtVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/10 9:24
 */
@Service
public class CourtServiceImpl implements CourtService {

    @Autowired
    private CourtMapper courtMapper;

    //场地查询方法
    @Override
    public PageInfo<CourtVO> findByPage(Integer current, Integer limit, CourtDto courtDto) {
        //设置分页参数
        PageHelper.startPage(current,limit);

        //根据条件查询所有数据
        List<CourtVO> list = courtMapper.findByPage(courtDto);

        //封装pageInfo对象
        PageInfo<CourtVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
}
