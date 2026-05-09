package com.sau.gym.model.vo.agent;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:Agent Trace统计VO
 * 日期: 2026/5/9 15:59
 */
@Data
public class AgentTraceStatsVO {

    /**
     * 总调用次数。
     */
    private Long totalCount;

    /**
     * 成功次数。
     */
    private Long successCount;

    /**
     * 失败次数。
     */
    private Long failedCount;

    /**
     * 运行中数量。
     */
    private Long runningCount;

    /**
     * 成功率，百分比。
     */
    private BigDecimal successRate;

    /**
     * 平均耗时，毫秒。
     */
    private BigDecimal avgCostMs;

    /**
     * 最大耗时，毫秒。
     */
    private Long maxCostMs;

    /**
     * 平均工具调用次数。
     */
    private BigDecimal avgToolCount;
}
