package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.BeverageMapper;
import com.sau.gym.admin.service.BeverageService;
import com.sau.gym.model.dto.shopping.BeverageDto;
import com.sau.gym.model.entity.shopping.Beverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/10 10:57
 */
@Service
public class BeverageServiceImpl implements BeverageService {

    @Autowired
    private BeverageMapper beverageMapper;

    //零食饮品查询方法
    @Override
    public PageInfo<Beverage> findByPage(Integer current, Integer limit, BeverageDto beverageDto) {
        PageHelper.startPage(current,limit);

        List<Beverage> list = beverageMapper.findByPage(beverageDto);
        PageInfo<Beverage> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //添加商品
    @Override
    public void saveBeverage(Beverage beverage) {
        beverageMapper.saveBeverage(beverage);
    }

    //修改商品
    @Override
    public void updateBeverage(Beverage beverage) {
        beverageMapper.updateBeverage(beverage);
    }

    //删除商品
    @Override
    public void deleteById(Long beverageId) {
        beverageMapper.deleteById(beverageId);
    }

    //前台查询商品的详情信息
    @Override
    public Beverage selectById(Long id) {
        Beverage beverage = beverageMapper.selectById(id);
        return beverage;
    }
}
