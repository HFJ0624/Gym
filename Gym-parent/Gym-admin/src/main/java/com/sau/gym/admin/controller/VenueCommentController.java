package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.VenueCommentService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.venue.VenueCommentDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.venue.VenueComment;
import com.sau.gym.model.vo.venue.VenueCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:场馆评论基础功能
 * 日期: 2026/3/30 10:21
 */
@RestController
@RequestMapping(value = "/admin/bulletin/venueComment")
public class VenueCommentController {

    @Autowired
    private VenueCommentService venueCommentService;

    //体育场馆评论查询方法
    //current:当前页 limit:每页显示的数量
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<VenueCommentVO>> findByPage(@PathVariable(value = "current") Integer current, @PathVariable(value = "limit") Integer limit, @RequestBody VenueCommentDto venueCommentDto){
        PageInfo<VenueCommentVO> pageInfo = venueCommentService.findByPage(current,limit,venueCommentDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //添加体育场馆评论
    @Log(title = "添加体育场馆评论",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/saveVenueComment")
    public Result saveVenueComment(@RequestBody VenueComment venueComment){
        venueCommentService.saveVenueComment(venueComment);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //修改场馆评论
    @Log(title = "修改场馆评论",businessType = 2,operatorType = OperatorType.MANAGE)
    @PutMapping(value = "/updateVenueComment")
    public Result updateVenueComment(@RequestBody VenueComment venueComment){
        venueCommentService.updateVenueComment(venueComment);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //删除场馆评论
    @Log(title = "删除场馆评论",businessType = 3,operatorType = OperatorType.MANAGE)
    @DeleteMapping(value = "/deleteById/{id}")
    public Result deleteById(@PathVariable(value = "id") Long id){
        venueCommentService.deleteById(id);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
