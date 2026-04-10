package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.shopping.OutfitDto;
import com.sau.gym.model.entity.shopping.Outfit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OutfitMapper {

    //器材查询方法
    List<Outfit> findByPage(OutfitDto outfitDto);

    //添加器材
    void saveOutfit(Outfit outfit);

    //修改器材
    void updateOutfit(Outfit outfit);

    //删除器材
    void deleteById(Long id);
}
