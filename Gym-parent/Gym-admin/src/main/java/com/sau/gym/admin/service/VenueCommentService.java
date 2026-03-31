package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.venue.VenueCommentDto;
import com.sau.gym.model.entity.venue.VenueComment;
import com.sau.gym.model.vo.venue.VenueCommentVO;

public interface VenueCommentService {

    //体育场馆评论查询方法
    PageInfo<VenueCommentVO> findByPage(Integer current, Integer limit, VenueCommentDto venueCommentDto);

    //添加体育场馆评论
    void saveVenueComment(VenueComment venueComment);

    //修改场馆评论
    void updateVenueComment(VenueComment venueComment);

    //删除场馆评论
    void deleteById(Long id);

    //场馆评论查询方法(前台)
    PageInfo<VenueCommentVO> findByPageComment(Integer current, Integer limit, Integer venueId);
}
