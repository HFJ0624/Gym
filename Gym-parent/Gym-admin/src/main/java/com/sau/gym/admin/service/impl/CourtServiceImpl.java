package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.CourtMapper;
import com.sau.gym.admin.service.CourtService;
import com.sau.gym.model.dto.venue.CourtDto;
import com.sau.gym.model.entity.venue.Court;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.vo.court.CourtVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //添加场地
    @Override
    public void saveCourt(Court court) {
        courtMapper.saveCourt(court);
    }

    //修改场地
    @Override
    public void updateCourt(Court court) {
        courtMapper.updateCourt(court);
    }

    //删除场地
    @Override
    public void deleteById(Long courtId) {
        courtMapper.deleteById(courtId);
    }

    //查询场馆对应的场地(前台)
    @Override
    public Map<String, Object> getAllCourt(Long venueId) {
        //查询对应场馆的所有场地
        List<CourtVO> allCourt = courtMapper.getAllCourt(venueId);

        //构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("allCourt",allCourt);
        return resultMap;
    }

    //查询一个场地的所有信息
    @Override
    public Map<String, Object> getCourt(Long courtId,Long venueId) {
        //查询场地信息
        List<CourtVO> court = courtMapper.getCourt(courtId,venueId);

        //构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("court",court);
        return resultMap;
    }
}
