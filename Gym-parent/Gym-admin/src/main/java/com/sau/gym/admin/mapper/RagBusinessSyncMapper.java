package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.vo.rag.RagCourtSyncVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RagBusinessSyncMapper {

    /**
     * 查询所有启用且未删除的场馆。
     * @return 场馆列表
     */
    List<Venue> selectEnabledVenues();

    /**
     * 查询所有启用且未删除的场地，并关联场馆信息。
     */
    List<RagCourtSyncVO> selectEnabledCourts();
}
