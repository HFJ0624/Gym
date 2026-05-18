package com.sau.gym.admin.controller.agent;

import com.sau.gym.admin.mcp.service.GymMcpChatService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:后台 MCP Agent
 * 日期: 2026/5/15 21:21
 */
@RestController
@RequestMapping("/admin/agent/mcp")
public class AdminMcpAgentController {

    @Autowired
    private GymMcpChatService gymMcpChatService;

    @PostMapping("/chat")
    public Result chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");

        //从当前登录上下文获取管理员用户。
        User currentUser = AuthContextUtil.get();

        //MCP Agent 的 ChatMemory 需要 memoryId。
        Long userId = currentUser.getId();

        String reply = gymMcpChatService.chat(userId, message);

        Map<String, Object> data = new HashMap<>();
        data.put("reply", reply);

        return Result.build(data, ResultCodeEnum.SUCCESS);
    }
}
