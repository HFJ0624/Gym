package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.*;
import com.sau.gym.admin.service.OrderService;
import com.sau.gym.model.dto.order.OrderDto;
import com.sau.gym.model.entity.order.Order;
import com.sau.gym.model.entity.order.OrderItem;
import com.sau.gym.model.entity.shopping.Beverage;
import com.sau.gym.model.entity.shopping.Cart;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.entity.user.UserBalance;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/12 14:46
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private BeverageMapper beverageMapper;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    //创建订单
    @Transactional
    @Override
    public void CreateShoppingOrder(OrderDto orderDto) {

        //获取当前的用户信息
        User user = AuthContextUtil.get();

        //1.获取选中的购物车id
        List<Long> cartIds = orderDto.getCartIds();
        Cart cart = null;
        Beverage beverage;

        //2.判断库存是否充足,库存不够不创建订单
        for (Long cartId : cartIds){
            cart = cartMapper.getByCartId(cartId);
            beverage = beverageMapper.selectById(cart.getGoodsId());
            if (beverage.getStock() < cart.getQuantity()){
                return;
            }
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        //3.计算订单总金额
        for (Long cartId : cartIds) {
            //获取购物车商品信息
            cart = cartMapper.getByCartId(cartId);
            BigDecimal itemTotal = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));

            //累加
            totalPrice = totalPrice.add(itemTotal);
        }

        //4.创建订单
        Order order = new Order();
        order.setPayTime(new Date());
        order.setOrderNo(getSecure32RandomNumber());
        order.setRemark(orderDto.getRemark());
        order.setUserId(user.getId());
        order.setTotalPrice(totalPrice);
        UserBalance userBalance = userBalanceMapper.GetBalanceById(user.getId());
        if (userBalance.getBalance().compareTo(totalPrice) >= 0){
            //用余额支付并更新余额
            order.setStatus(1);

            //更新余额
            BigDecimal surplus = userBalance.getBalance().subtract(totalPrice);
            userBalanceMapper.updateBalance(user.getId(),surplus);

            //更新库存
            for (Long cartId : cartIds){
                cart = cartMapper.getByCartId(cartId);
                beverage = beverageMapper.selectById(cart.getGoodsId());
                //更新库存
                beverageMapper.updateStock(beverage.getId(),beverage.getStock() - cart.getQuantity());
            }
        }else {
            //余额不够支付,显示为待支付
            order.setStatus(0);
        }
        //插入到数据库
        orderMapper.insertOrder(order);

        //5.插入订单明细
        OrderItem orderItem;
        for (Long cartId : cartIds){
            //获取购物车商品信息
            cart = cartMapper.getByCartId(cartId);
            orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setImage(cart.getImage());
            orderItem.setGoodsId(cart.getGoodsId());
            orderItem.setGoodsName(cart.getGoodsName());
            orderItem.setOrderNo(order.getOrderNo());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(cart.getPrice());

            //插入订单明细
            orderItemMapper.insertOrderItem(orderItem);
        }

        //6.删除购物车数据
        for (Long cartId : cartIds){
            cartMapper.DeleteCartItem(cartId);
        }

    }

    /***
     *
     * @return 生成32位订单编码
     */
    public static String getSecure32RandomNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(32);

        for (int i = 0; i < 32; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
