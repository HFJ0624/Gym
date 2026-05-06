package com.sau.gym.model.dto.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/5 21:29
 */
@Data
@Schema(description = "Agent工具调用日志查询参数")
public class AgentToolLogQueryDto {

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页数量")
    private Integer pageSize = 10;


    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "一次Agent对话的追踪ID")
    private String traceId;

    @Schema(description = "工具名称")
    private String toolName;

    @Schema(description = "调用状态")
    private String status;

    @Schema(description = "用户原始输入关键字")
    private String userMessage;

    @Schema(description = "慢调用阈值，单位毫秒")
    private Long slowThresholdMs = 3000L;
}
