package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.VenueMapper;
import com.sau.gym.admin.service.VenueService;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.dto.venue.VenueDto;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.venue.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //添加场馆
    @Override
    public void saveVenue(Venue venue) {
        String venueName = venue.getVenueName();

        //命名重复判断
        Venue dbVenue = venueMapper.selectOne(venueName);
        if (dbVenue != null){
            throw new SauException(ResultCodeEnum.VENUE_NAME_EXIST);
        }

        //保存到数据库
        venueMapper.saveVenue(venue);
    }

    //修改场馆
    @Override
    public void updateVenue(Venue venue) {
        venueMapper.updateVenue(venue);
    }

    //删除场馆
    @Override
    public void deleteById(Long venueId) {
        venueMapper.deleteById(venueId);
    }

    //查找所有场馆
    @Override
    public Map<String, Object> findAllVenue() {
        //查询所有场馆
        List<Venue> allVenue = venueMapper.findAllVenue();

        //构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("allVenue",allVenue);
        return resultMap;
    }
}
