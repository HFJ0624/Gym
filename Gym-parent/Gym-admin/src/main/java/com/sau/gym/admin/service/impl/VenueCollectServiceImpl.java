package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.VenueCollectMapper;
import com.sau.gym.admin.service.VenueCollectService;
import com.sau.gym.model.dto.venue.VenueCollectDto;
import com.sau.gym.model.vo.venue.VenueCollectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/31 9:31
 */
@Service
public class VenueCollectServiceImpl implements VenueCollectService {

    @Autowired
    private VenueCollectMapper venueCollectMapper;

    //场馆收藏分页查询
    @Override
    public PageInfo<VenueCollectVO> findByPage(Integer current, Integer limit, VenueCollectDto venueCollectDto) {
        PageHelper.startPage(current,limit);

        List<VenueCollectVO> list = venueCollectMapper.findByPage(venueCollectDto);
        PageInfo<VenueCollectVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
}
