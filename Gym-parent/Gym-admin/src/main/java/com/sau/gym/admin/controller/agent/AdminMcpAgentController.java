package com.sau.gym.admin.controller.agent;

import com.sau.gym.admin.mcp.service.GymMcpChatService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/15 21:21
 */
@RestController
@RequestMapping("/admin/agent/mcp")
public class AdminMcpAgentController {

    @Autowired
    private GymMcpChatService gymMcpChatService;

    @PostMapping("/chat")
    public Result chat(@RequestParam(required = false) Long userId,
                       @RequestBody Map<String, String> body) {

        String message = body.get("message");

        String reply = gymMcpChatService.chat(userId, message);

        Map<String, Object> data = new HashMap<>();
        data.put("reply", reply);
        return Result.build(data, ResultCodeEnum.SUCCESS);
    }
}
