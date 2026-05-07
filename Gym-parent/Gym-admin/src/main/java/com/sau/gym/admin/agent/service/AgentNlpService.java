package com.sau.gym.admin.agent.service;

import com.sau.gym.admin.agent.model.AgentNlpResult;

public interface AgentNlpService {

    /**
     * 分析用户输入。
     *
     * @param text 用户输入
     * @return NLP 分析结果
     */
    AgentNlpResult analyze(String text);
}
