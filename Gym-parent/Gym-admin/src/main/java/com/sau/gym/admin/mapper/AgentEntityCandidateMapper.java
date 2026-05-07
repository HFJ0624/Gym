package com.sau.gym.admin.mapper;

import com.sau.gym.admin.agent.model.AgentEntityCandidate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AgentEntityCandidateMapper {

    /**
     * 查询场馆真实名称候选。
     */
    List<AgentEntityCandidate> selectVenueNameCandidates();

    /**
     * 查询场馆别名候选。
     */
    List<AgentEntityCandidate> selectVenueAliasCandidates();

    /**
     * 查询场地真实名称候选。
     */
    List<AgentEntityCandidate> selectCourtNameCandidates();

    /**
     * 查询场地别名候选。
     */
    List<AgentEntityCandidate> selectCourtAliasCandidates();
}
