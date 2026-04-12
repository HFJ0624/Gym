package com.sau.gym.admin.service;

import com.sau.gym.model.dto.shopping.CartDto;
import com.sau.gym.model.dto.shopping.CartItemDto;
import com.sau.gym.model.vo.cart.CartListVO;

public interface CartService {


    //获取购物车详情
    CartListVO GetCartList(Long id);

    //添加商品到购物车
    void AddToCart(CartDto cartDto);

    //更新购物车数量
    void UpdateCartItem(CartItemDto cartItemDto);

    //删除购物车商品
    void DeleteCartItem(Long id);
}
