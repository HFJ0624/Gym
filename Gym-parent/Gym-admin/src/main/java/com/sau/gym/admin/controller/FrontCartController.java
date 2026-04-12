package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.CartService;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.enums.OperatorType;
import com.sau.gym.model.dto.shopping.CartDto;
import com.sau.gym.model.dto.shopping.CartItemDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.cart.CartListVO;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 作者:hfj
 * 功能:购物车功能实现
 * 日期: 2026/4/12 9:45
 */
@RestController
@RequestMapping(value = "/front/cart")
public class FrontCartController {

    @Autowired
    private CartService cartService;

    //获取购物车详情
    @GetMapping(value = "/list")
    public Result GetCartList(){
        User user = AuthContextUtil.get();
        CartListVO list =  cartService.GetCartList(user.getId());
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }

    //添加商品到购物车
    @Log(title = "添加商品到购物车",businessType = 1,operatorType = OperatorType.OTHER)
    @PostMapping(value = "/add")
    public Result AddToCart(@RequestBody CartDto cartDto){
        cartService.AddToCart(cartDto);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //更新购物车数量
    @Log(title = "更新购物车数量",businessType = 2,operatorType = OperatorType.OTHER)
    @PutMapping(value = "/update")
    public Result UpdateCartItem(@RequestBody CartItemDto cartItemDto){
        cartService.UpdateCartItem(cartItemDto);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }

    //删除购物车商品
    @Log(title = "删除购物车商品",businessType = 3,operatorType = OperatorType.OTHER)
    @DeleteMapping(value = "/delete/{id}")
    public Result DeleteCartItem(@PathVariable Long id){
        cartService.DeleteCartItem(id);
        return Result.build(null,ResultCodeEnum.SUCCESS);
    }
}
