package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.venue.Venue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RagBusinessSyncMapper {

    /**
     * 查询所有启用且未删除的场馆。
     * @return 场馆列表
     */
    List<Venue> selectEnabledVenues();
}
