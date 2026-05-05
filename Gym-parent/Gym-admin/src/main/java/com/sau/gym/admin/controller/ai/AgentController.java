package com.sau.gym.admin.controller.ai;

import com.sau.gym.admin.agent.service.AgentService;
import com.sau.gym.model.dto.agent.AgentChatDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:agent智能体聊天
 * 日期: 2026/3/30 20:46
 */
@RestController
@RequestMapping(value = "/front/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    //构建agent智能聊天
    @PostMapping("/chat")
    public Result chat(@RequestParam Long userId, @RequestBody AgentChatDto agentChatDto) {
        String reply = agentService.chat(userId, agentChatDto);
        return Result.build(reply, ResultCodeEnum.SUCCESS);
    }
}
