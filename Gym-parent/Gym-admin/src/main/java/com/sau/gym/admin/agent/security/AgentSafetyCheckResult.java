package com.sau.gym.admin.agent.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/10 18:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentSafetyCheckResult {

    /**
     * 是否允许继续执行。
     */
    private boolean allowed;

    /**
     * 拒绝原因。
     */
    private String reason;

    /**
     * 通过。
     */
    public static AgentSafetyCheckResult allow() {
        return new AgentSafetyCheckResult(true, null);
    }

    /**
     * 拒绝。
     */
    public static AgentSafetyCheckResult deny(String reason) {
        return new AgentSafetyCheckResult(false, reason);
    }
}
