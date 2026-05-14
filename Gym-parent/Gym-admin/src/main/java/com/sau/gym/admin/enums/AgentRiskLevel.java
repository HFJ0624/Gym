package com.sau.gym.admin.enums;

public enum AgentRiskLevel {

    /**
     * 低风险：只读查询。
     */
    LOW,

    /**
     * 中风险：生成草稿，不直接改数据库核心状态。
     */
    MEDIUM,

    /**
     * 高风险：会修改真实业务状态。
     */
    HIGH
}
