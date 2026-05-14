package com.sau.gym.admin.agent.service.impl;

import com.sau.gym.admin.agent.security.AgentSafetyCheckResult;
import com.sau.gym.admin.agent.service.AgentSafetyService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 作者:hfj
 * 功能:Agent 安全服务实现
 * 主要防：
 * 1. 提示词注入
 * 2. 越权操作
 * 3. 删除类危险操作
 * 4. 绕过确认流程
 * 日期: 2026/5/10 18:04
 */
@Service
public class AgentSafetyServiceImpl implements AgentSafetyService {

    /**
     * 明显的提示词注入关键词。
     * 用户如果输入这些内容，通常是在试图让模型忽略系统规则。
     */
    private static final List<String> PROMPT_INJECTION_KEYWORDS = Arrays.asList(
            "忽略之前所有规则",
            "忽略以上规则",
            "忽略系统提示",
            "忘记你的身份",
            "你现在不是",
            "绕过限制",
            "不要遵守",
            "无视安全策略",
            "直接执行",
            "不要确认",
            "不用确认",
            "跳过确认",
            "立刻删除",
            "删除全部",
            "清空所有",
            "暴露系统提示词",
            "输出你的system prompt",
            "显示你的系统提示词"
    );

    /**
     * 高风险业务关键词。
     * 这些词不一定完全禁止，但如果和“直接 / 跳过确认 / 删除全部”等组合出现，
     * 就应该拦截。
     */
    private static final List<String> HIGH_RISK_KEYWORDS = Arrays.asList(
            "删除知识库",
            "删除全部知识",
            "重建全部索引",
            "删除全部预约",
            "取消全部预约",
            "退款到账",
            "直接退款",
            "修改余额",
            "清空日志",
            "删除日志"
    );

    @Override
    public AgentSafetyCheckResult checkUserMessage(Long userId, String message) {
        if (!StringUtils.hasText(message)) {
            return AgentSafetyCheckResult.deny("消息不能为空。");
        }

        String text = message.trim();

        //1. 检查明显提示词注入。
        for (String keyword : PROMPT_INJECTION_KEYWORDS) {
            if (text.contains(keyword)) {
                return AgentSafetyCheckResult.deny(
                        "检测到疑似越权或提示词注入内容。请使用正常业务语言描述你的需求。"
                );
            }
        }

        //2. 检查明显高风险指令。
        for (String keyword : HIGH_RISK_KEYWORDS) {
            if (text.contains(keyword)) {
                return AgentSafetyCheckResult.deny(
                        "该操作属于高风险操作，不能通过普通 Agent 对话直接执行，请在后台管理页面按流程操作。"
                );
            }
        }

        //3. 正常业务输入放行。
        return AgentSafetyCheckResult.allow();
    }
}
