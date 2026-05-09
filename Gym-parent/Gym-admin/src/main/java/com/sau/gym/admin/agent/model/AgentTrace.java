package com.sau.gym.admin.agent.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:Agent 调用链主表实体
 * 日期: 2026/5/9 14:41
 */
@Data
public class AgentTrace {

    private Long id;

    /**
     * 调用链ID。
     */
    private String traceId;

    /**
     * 用户ID。
     */
    private Long userId;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 用户输入。
     */
    private String userMessage;

    /**
     * 最终回复。
     */
    private String finalReply;

    /**
     * 状态：RUNNING / SUCCESS / FAILED。
     */
    private String status;

    /**
     * 异常信息。
     */
    private String errorMessage;

    /**
     * 总耗时。
     */
    private Long totalCostMs;

    /**
     * 工具调用次数。
     */
    private Integer toolCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Integer isDeleted;
}
