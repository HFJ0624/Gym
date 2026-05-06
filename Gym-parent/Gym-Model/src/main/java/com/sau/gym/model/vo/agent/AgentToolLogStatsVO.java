package com.sau.gym.model.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/6 10:26
 */
@Data
@Schema(description = "Agent工具调用日志统计VO")
public class AgentToolLogStatsVO {

    @Schema(description = "总调用次数")
    private Long totalCount;

    @Schema(description = "成功调用次数")
    private Long successCount;

    @Schema(description = "失败调用次数")
    private Long failCount;

    @Schema(description = "慢调用次数,大于3秒算秒调用")
    private Long slowCount;

    @Schema(description = "不同 traceId 数量")
    private Long traceCount;

    @Schema(description = "平均耗时，单位毫秒")
    private BigDecimal avgDurationMs;
}
