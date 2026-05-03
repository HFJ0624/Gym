package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.order.OrderDto;
import com.sau.gym.model.dto.order.OrdersDto;
import com.sau.gym.model.entity.order.Order;
import com.sau.gym.model.vo.order.OrderDetailVO;
import com.sau.gym.model.vo.order.OrderVO;

import java.util.Map;

public interface OrderService {

    //创建订单
    void CreateShoppingOrder(OrderDto orderDto);

    //获取购物订单列表
    PageInfo<OrderDetailVO> getOrderList(Integer page, Integer limit, Integer status);

    //订单查询列表
    PageInfo<OrderVO> findByPage(Integer current, Integer limit, OrdersDto ordersDto);

    //统计七天的订单营业额数据
    Map<String, Object> getAllTurnover();
}
