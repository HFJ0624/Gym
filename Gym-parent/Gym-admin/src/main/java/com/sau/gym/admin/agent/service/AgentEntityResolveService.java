package com.sau.gym.admin.agent.service;

import com.sau.gym.admin.agent.model.AgentEntityResolveResult;

public interface AgentEntityResolveService {

    /**
     * 解析用户输入。
     *
     * @param userText 用户输入
     * @return 解析结果
     */
    AgentEntityResolveResult resolve(String userText);
}
