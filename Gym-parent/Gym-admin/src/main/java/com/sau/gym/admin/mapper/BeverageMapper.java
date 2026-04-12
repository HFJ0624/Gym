package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.shopping.BeverageDto;
import com.sau.gym.model.entity.shopping.Beverage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BeverageMapper {

    //零食饮品查询方法
    List<Beverage> findByPage(BeverageDto beverageDto);

    //添加商品
    void saveBeverage(Beverage beverage);

    //修改商品
    void updateBeverage(Beverage beverage);

    //删除商品
    void deleteById(Long beverageId);

    //前台查询商品的详情信息
    Beverage selectById(Long id);
}
