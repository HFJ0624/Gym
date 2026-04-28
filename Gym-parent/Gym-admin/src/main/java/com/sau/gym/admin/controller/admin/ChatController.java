package com.sau.gym.admin.controller.admin;

import com.sau.gym.admin.service.ChatService;
import com.sau.gym.admin.service.UserService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.chat.ChatConversation;
import com.sau.gym.model.entity.chat.ChatMessage;
import com.sau.gym.model.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作者:hfj
 * 功能:实现websocket的在线聊天功能
 * 日期: 2026/4/13 16:05
 */

@RestController
@RequestMapping("/admin/chat/chatAdmin")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    //获取历史聊天记录
    @GetMapping("/history/{userId}")
    public Result getHistory(@PathVariable Long userId) {
        // 固定客服ID = 1
        Long adminId = 1L;
        // 查询：该用户 + 客服 双向消息
        List<ChatMessage> list = chatService.getAdminUserChatHistory(adminId, userId);
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }


    //加载用户会话信息
    @GetMapping("/admin/users")
    public Result getAllUsers() {
        List<ChatConversation> list = chatService.getAllUserConversations();
        return Result.build(list, ResultCodeEnum.SUCCESS);
    }

    //加载客服个人信息
    @GetMapping("/admin/info")
    public Result getAdminInfo() {
        User user = userService.selectAdmin();
        return Result.build(user, ResultCodeEnum.SUCCESS);
    }
}
