package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.shopping.OutfitDto;
import com.sau.gym.model.entity.shopping.Outfit;

public interface OutfitService {

    //器材查询方法
    PageInfo<Outfit> findByPage(Integer current, Integer limit, OutfitDto outfitDto);

    //添加器材
    void saveOutfit(Outfit outfit);

    //修改器材
    void updateOutfit(Outfit outfit);

    //删除器材
    void deleteById(Long id);
}
