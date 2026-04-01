package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.VenueVisitMapper;
import com.sau.gym.admin.service.VenueVisitService;
import com.sau.gym.model.entity.venue.VenueVisit;
import com.sau.gym.model.vo.venue.VenueVisitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/31 19:49
 */
@Service
public class VenueVisitServiceImpl implements VenueVisitService {

    @Autowired
    private VenueVisitMapper venueVisitMapper;

    //增加一次场馆访问
    @Override
    public void addVisit(Long venueId) {
        venueVisitMapper.addVisit(venueId);
    }

    //创建场馆的访问记录
    @Override
    public void addOne(Long id) {
        venueVisitMapper.addOne(id);
    }

    //场馆访问量分页查询
    @Override
    public PageInfo<VenueVisitVO> findByPage(Integer current, Integer limit) {
        PageHelper.startPage(current,limit);

        //查询所有访问量
        List<VenueVisitVO> list = venueVisitMapper.selectALL();

        PageInfo<VenueVisitVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //核心：缓存为空时，只查一次全量，初始化缓存
    @Override
    public List<VenueVisitVO> findAll() {
        return venueVisitMapper.selectALL();
    }
}
