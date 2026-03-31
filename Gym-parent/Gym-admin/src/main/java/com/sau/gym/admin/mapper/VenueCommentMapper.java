package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.venue.VenueCommentDto;
import com.sau.gym.model.entity.venue.VenueComment;
import com.sau.gym.model.vo.venue.VenueCommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VenueCommentMapper {

    //查询所有场馆评论
    List<VenueCommentVO> findByPage(VenueCommentDto venueCommentDto);

    //添加体育场馆评论
    void saveVenueComment(VenueComment venueComment);

    //修改场馆评论
    void updateVenueComment(VenueComment venueComment);

    //删除场馆评论
    void deleteById(Long id);

    //场馆评论查询方法(前台)
    List<VenueCommentVO> findByPageComment(Integer venueId);
}
