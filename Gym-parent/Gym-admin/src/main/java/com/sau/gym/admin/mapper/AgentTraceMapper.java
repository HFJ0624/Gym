package com.sau.gym.admin.mapper;

import com.sau.gym.admin.agent.model.AgentTrace;
import com.sau.gym.admin.agent.model.AgentTraceStep;
import com.sau.gym.model.vo.agent.AgentTraceStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgentTraceMapper {

    /**
     * 插入调用链主记录。
     */
    int insertTrace(AgentTrace trace);

    /**
     * 插入调用链步骤。
     */
    int insertStep(AgentTraceStep step);

    /**
     * 结束调用链。
     */
    int finishTrace(@Param("traceId") String traceId,
                    @Param("finalReply") String finalReply,
                    @Param("status") String status,
                    @Param("errorMessage") String errorMessage,
                    @Param("totalCostMs") Long totalCostMs,
                    @Param("toolCount") Integer toolCount);

    /**
     * 分页查询调用链列表。
     */
    List<AgentTrace> selectTracePage(@Param("userId") Long userId, @Param("status") String status, @Param("keyword") String keyword);

    /**
     * 根据 traceId 查询主记录。
     */
    AgentTrace selectTraceByTraceId(@Param("traceId") String traceId);

    /**
     * 查询某个 traceId 的全部步骤。
     */
    List<AgentTraceStep> selectStepsByTraceId(@Param("traceId") String traceId);

    /**
     * 统计某个 traceId 的工具调用次数。
     *
     * 第一版先按 step_type = TOOL_CALL 统计。
     * 如果暂时没有工具埋点，这个值可能为 0。
     */
    int countToolSteps(@Param("traceId") String traceId);

    /**
     * 查询 Trace 统计数据。
     *
     * 第一版统计全量数据。
     * 后续可以扩展今日统计、时间范围统计。
     */
    AgentTraceStatsVO selectTraceStats();
}
