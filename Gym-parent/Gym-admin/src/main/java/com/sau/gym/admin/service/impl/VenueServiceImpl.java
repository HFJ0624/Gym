package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.VenueMapper;
import com.sau.gym.admin.service.VenueService;
import com.sau.gym.model.dto.venue.VenueDto;
import com.sau.gym.model.entity.venue.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/7 10:53
 */
@Service
public class VenueServiceImpl implements VenueService {

    @Autowired
    private VenueMapper venueMapper;

    //场馆列表查询方法
    @Override
    public PageInfo<Venue> findByPage(Integer current, Integer limit, VenueDto venueDto) {

        //设置分页参数
        PageHelper.startPage(current,limit);

        //根据条件查询所有数据
        List<Venue> list = venueMapper.findByPage(venueDto);

        //封装pageInfo对象
        PageInfo<Venue> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
}
