package com.sau.gym.admin.controller.admin;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.OrderService;
import com.sau.gym.model.dto.order.OrdersDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.order.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:后台订单基本功能
 * 日期: 2026/4/16 14:31
 */
@RestController
@RequestMapping(value = "/admin/order/orderInfo")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //订单查询列表
    @PostMapping(value = "/findByPage/{current}/{limit}")
    public Result<PageInfo<OrderVO>> findByPage(@PathVariable("current") Integer current, @PathVariable("limit") Integer limit,@RequestBody OrdersDto ordersDto){
        PageInfo<OrderVO> pageInfo = orderService.findByPage(current,limit,ordersDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }
}
