package com.sau.gym.admin.agent.service;

import com.sau.gym.model.dto.agent.AgentChatDto;

public interface AgentService {

    //构建agent智能聊天
    String chat(Long userId, AgentChatDto agentChatDto);
}
