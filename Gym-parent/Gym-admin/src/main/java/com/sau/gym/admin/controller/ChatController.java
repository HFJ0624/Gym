package com.sau.gym.admin.controller;

import com.sau.gym.admin.service.ChatService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.chat.ChatConversation;
import com.sau.gym.model.entity.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/13 16:05
 */

@RestController
@RequestMapping("/admin/chat/chatAdmin")
public class ChatController {

    @Autowired
    private ChatService chatService;

    //获取历史聊天记录
    @GetMapping("/history/{userId}")
    public Result getHistory(@PathVariable Long userId) {
        List<ChatMessage> list = chatService.getHistory(userId);
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }

    //
    @GetMapping("/admin/users")
    public Result getAllUsers() {
        List<ChatConversation> list = chatService.getAllUserConversations();
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }
}
