package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.VenueBookingMapper;
import com.sau.gym.admin.service.VenueBookingService;
import com.sau.gym.model.dto.venue.VenueBookDto;
import com.sau.gym.model.vo.venue.VenueBookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/17 9:25
 */
@Service
public class VenueBookingServiceImpl implements VenueBookingService {

    @Autowired
    private VenueBookingMapper venueBookingMapper;

    //场馆预约的查询功能
    @Override
    public PageInfo<VenueBookVO> findByPage(Integer current, Integer limit, VenueBookDto venueBookDto) {

        //设置分页参数
        PageHelper.startPage(current,limit);

        //查询场馆预约
        List<VenueBookVO> list =  venueBookingMapper.findByPage(venueBookDto);

        PageInfo<VenueBookVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }


    //删除预约场馆
    @Override
    public void deleteById(Long id) {
        venueBookingMapper.deleteById(id);
    }
}
