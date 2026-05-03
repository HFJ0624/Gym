package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.VenueMapper;
import com.sau.gym.admin.service.VenueService;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.dto.venue.VenueDto;
import com.sau.gym.model.dto.venue.VenueStatusDto;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.venue.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

        Date date = new Date();
        venue.setCreateTime(date);
        venue.setUpdateTime(date);
        //保存到数据库
        venueMapper.saveVenue(venue);
    }

    //修改场馆
    @Override
    public void updateVenue(Venue venue) {
        venue.setUpdateTime(new Date());
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

    //查找所有场馆(前台)
    @Override
    public Map<String, Object> getAllVenue(VenueDto venueDto) {
        //查询所有场馆
        List<Venue> allVenue = venueMapper.getAllVenue(venueDto);

        //构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("allVenue",allVenue);
        return resultMap;
    }

    //修改场馆状态
    @Override
    public Boolean updateVenueStatus(VenueStatusDto venueStatusDto) {
        if (venueStatusDto == null){
            return false;
        }

        int row = venueMapper.updateVenueStatus(venueStatusDto.getId(),venueStatusDto.getStatus());
        return row > 0;
    }

    //获取好评前六的场馆
    @Override
    public Map<String, Object> getVenueByRate() {
        //1.获取好评前六的id集合
        List<Long> list = venueMapper.getVenueByRate();

        //2.获取前六的场馆
        List<Venue> venueList = venueMapper.selectVenueBatchByIds(list);

        //构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("venueList",venueList);
        return resultMap;
    }
}
