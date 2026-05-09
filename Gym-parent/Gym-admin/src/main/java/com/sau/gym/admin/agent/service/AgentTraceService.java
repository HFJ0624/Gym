package com.sau.gym.admin.agent.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.agent.model.AgentTrace;
import com.sau.gym.admin.agent.model.AgentTraceStep;
import com.sau.gym.model.vo.agent.AgentTraceStatsVO;

import java.util.List;

public interface AgentTraceService {

    /**
     * 开始一次 Trace。
     */
    void startTrace(String traceId, Long userId, String username, String userMessage);

    /**
     * 记录步骤。
     */
    void addStep(String stepType,
                 String stepName,
                 String inputData,
                 String outputData,
                 String status,
                 String errorMessage,
                 Long costMs);

    /**
     * 成功结束 Trace。
     */
    void finishSuccess(String traceId, String finalReply, Long totalCostMs);

    /**
     * 失败结束 Trace。
     */
    void finishFailed(String traceId, String finalReply, String errorMessage, Long totalCostMs);

    /**
     * 分页查询 Trace。
     */
    PageInfo<AgentTrace> page(Integer current, Integer limit, Long userId, String status, String keyword);

    /**
     * 查询 Trace 主记录。
     */
    AgentTrace detail(String traceId);

    /**
     * 查询 Trace 步骤。
     */
    List<AgentTraceStep> steps(String traceId);

    /**
     * 查询 Trace 统计数据。
     */
    AgentTraceStatsVO stats();
}
