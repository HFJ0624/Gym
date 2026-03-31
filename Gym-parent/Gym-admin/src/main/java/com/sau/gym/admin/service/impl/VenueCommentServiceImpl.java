package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.VenueCommentMapper;
import com.sau.gym.admin.service.VenueCommentService;
import com.sau.gym.model.dto.venue.VenueCommentDto;
import com.sau.gym.model.entity.venue.VenueComment;
import com.sau.gym.model.vo.venue.VenueCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/30 10:22
 */
@Service
public class VenueCommentServiceImpl implements VenueCommentService {

    @Autowired
    private VenueCommentMapper venueCommentMapper;

    //体育场馆评论查询方法
    @Override
    public PageInfo<VenueCommentVO> findByPage(Integer current, Integer limit, VenueCommentDto venueCommentDto) {
        //构建分页对象
        PageHelper.startPage(current,limit);

        //查询所有场馆评论
        List<VenueCommentVO> list = venueCommentMapper.findByPage(venueCommentDto);
        PageInfo<VenueCommentVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //添加体育场馆评论
    @Override
    public void saveVenueComment(VenueComment venueComment) {
        venueCommentMapper.saveVenueComment(venueComment);
    }

    //修改场馆评论
    @Override
    public void updateVenueComment(VenueComment venueComment) {
        venueCommentMapper.updateVenueComment(venueComment);
    }

    //删除场馆评论
    @Override
    public void deleteById(Long id) {
        venueCommentMapper.deleteById(id);
    }

    //场馆评论查询方法(前台)
    @Override
    public PageInfo<VenueCommentVO> findByPageComment(Integer current, Integer limit, Integer venueId) {
        //构建分页对象
        PageHelper.startPage(current,limit);

        //场馆评论查询方法(前台)
        List<VenueCommentVO> list = venueCommentMapper.findByPageComment(venueId);
        PageInfo<VenueCommentVO> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
}
