package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.VenueCollectMapper;
import com.sau.gym.admin.service.VenueCollectService;
import com.sau.gym.model.dto.venue.VenueCollectDto;
import com.sau.gym.model.entity.venue.VenueCollect;
import com.sau.gym.model.vo.venue.VenueCollectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //获取当前用户收藏的场馆列表
    @Override
    public Map<String, Object> selectAllVenuesByUserId(Long id) {
        //获取当前用户收藏的场馆列表
        List<VenueCollect> venueCollectList = venueCollectMapper.selectAllVenuesByUserId(id);

        //构建返回对象
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("venueCollectList",venueCollectList);
        return resultMap;
    }

    //用户收藏场馆
    @Override
    public void CollectVenue(Long id, Long venueId) {
        venueCollectMapper.CollectVenue(id,venueId);
    }

    //用户取消收藏场馆
    @Override
    public void UnCollectVenue(Long id, Long venueId) {
        venueCollectMapper.UnCollectVenue(id,venueId);
    }
}
