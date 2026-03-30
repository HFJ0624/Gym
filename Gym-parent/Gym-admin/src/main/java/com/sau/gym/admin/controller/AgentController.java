package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.AgentService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public Result chat(@RequestBody Map<String, Object> map) {
        String message = map.get("message").toString();
        String reply = agentService.chat(message);
        return Result.build(reply, ResultCodeEnum.SUCCESS);
    }
}
