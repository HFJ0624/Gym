package com.sau.gym.admin.controller;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.service.OrderService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.order.OrderDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.order.Order;
import com.sau.gym.model.vo.order.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:订单基本实现类
 * 日期: 2026/4/12 14:37
 */
@RestController
@RequestMapping(value = "/front/shoppingOrder")
public class FrontOrderController {

    @Autowired
    private OrderService orderService;

    //创建订单
    @Log(title = "创建订单",businessType = 1,operatorType = OperatorType.MANAGE)
    @PostMapping(value = "/create")
    public Result CreateShoppingOrder(@RequestBody OrderDto orderDto){
        orderService.CreateShoppingOrder(orderDto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    //获取购物订单列表
    @GetMapping(value = "/list")
    public Result<PageInfo<OrderDetailVO>> GetShoppingOrderList(@RequestParam Integer page, Integer limit, @RequestParam(required = false) Integer status){
        PageInfo<OrderDetailVO> pageInfo = orderService.getOrderList(page,limit,status);
        return Result.build(pageInfo,ResultCodeEnum.SUCCESS);
    }
}
