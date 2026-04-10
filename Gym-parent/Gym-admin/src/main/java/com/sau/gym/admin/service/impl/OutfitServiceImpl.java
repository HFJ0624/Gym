package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.OutfitMapper;
import com.sau.gym.admin.service.OutfitService;
import com.sau.gym.model.dto.shopping.OutfitDto;
import com.sau.gym.model.entity.shopping.Outfit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/10 18:13
 */
@Service
public class OutfitServiceImpl implements OutfitService {

    @Autowired
    private OutfitMapper outfitMapper;

    //器材查询方法
    @Override
    public PageInfo<Outfit> findByPage(Integer current, Integer limit, OutfitDto outfitDto) {
        PageHelper.startPage(current,limit);

        List<Outfit> list = outfitMapper.findByPage(outfitDto);
        PageInfo<Outfit> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //添加器材
    @Override
    public void saveOutfit(Outfit outfit) {
        outfitMapper.saveOutfit(outfit);
    }

    //修改器材
    @Override
    public void updateOutfit(Outfit outfit) {
        outfitMapper.updateOutfit(outfit);
    }

    //删除器材
    @Override
    public void deleteById(Long id) {
        outfitMapper.deleteById(id);
    }
}
