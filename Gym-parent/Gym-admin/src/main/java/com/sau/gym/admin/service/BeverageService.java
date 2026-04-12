package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.shopping.BeverageDto;
import com.sau.gym.model.entity.shopping.Beverage;

public interface BeverageService {

    //零食饮品查询方法
    PageInfo<Beverage> findByPage(Integer current, Integer limit, BeverageDto beverageDto);

    //添加商品
    void saveBeverage(Beverage beverage);

    //修改商品
    void updateBeverage(Beverage beverage);

    //删除商品
    void deleteById(Long beverageId);

    //前台查询商品的详情信息
    Beverage selectById(Long id);
}
