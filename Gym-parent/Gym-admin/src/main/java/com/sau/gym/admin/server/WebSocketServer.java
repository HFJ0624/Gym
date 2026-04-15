package com.sau.gym.admin.server;

import com.alibaba.fastjson2.JSON;
import com.sau.gym.admin.service.ChatService;
import com.sau.gym.admin.utils.SpringContextUtil;
import com.sau.gym.model.dto.chat.MessageDTO;
import com.sau.gym.model.entity.chat.ChatConversation;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 作者:hfj
 * 功能:websocket服务端
 * 日期: 2026/4/13 15:59
 */
@Slf4j
@Component
@ServerEndpoint("/admin/chatAdmin/ws/chat/{role}/{userId}")
public class WebSocketServer {

    // 在线用户
    public static final Map<Long, Session> USER_SESSION_MAP = new ConcurrentHashMap<>();
    // 在线客服
    public static final Map<Long, Session> ADMIN_SESSION_MAP = new ConcurrentHashMap<>();

    // 🔥 这里不要直接初始化！！！会导致启动报错！
    // 改为：在方法里获取，不要在构造时初始化
    private static ChatService chatService;

    /**
     * 初始化时获取一次 chatService
     */
    private static void initChatService() {
        if (chatService == null) {
            chatService = SpringContextUtil.getBean(ChatService.class);
        }
    }

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("role") String role,
                       @PathParam("userId") Long userId) {
        if ("user".equals(role)) {
            USER_SESSION_MAP.put(userId, session);
            log.info("用户上线：{}，在线：{}", userId, USER_SESSION_MAP.size());
        } else if ("admin".equals(role)) {
            ADMIN_SESSION_MAP.put(userId, session);
            log.info("客服上线：{}，在线：{}", userId, ADMIN_SESSION_MAP.size());
        }
    }

    @OnClose
    public void onClose(@PathParam("role") String role,
                        @PathParam("userId") Long userId) {
        if ("user".equals(role)) {
            USER_SESSION_MAP.remove(userId);
        } else if ("admin".equals(role)) {
            ADMIN_SESSION_MAP.remove(userId);
        }
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId) {
        try {
            // 🔥 在这里获取，绝对不报错
            initChatService();

            MessageDTO dto = JSON.parseObject(message, MessageDTO.class);

            // ==============================================
            // ✅ 【终极正确】自动创建会话，绝对不会 null
            // ==============================================
            Long senderId = dto.getSenderId();
            ChatConversation conversation = chatService.getConversationByUserId(senderId);

            // 如果会话不存在 → 自动创建！
            if (conversation == null) {
                conversation = new ChatConversation();
                conversation.setUserId(senderId);
                conversation.setUserName(dto.getUserName());
                // 👇 补全用户头像
                conversation.setUserAvatar(dto.getUserAvatar());
                // 👇 补全客服ID（固定为1号客服，可根据需求修改）
                conversation.setAdminId(1L);
                // 👇 补全状态（0=待回复，1=已回复，2=已关闭）
                conversation.setStatus(0);
                chatService.createConversation(conversation);
            }

            // 设置会话ID → 绝对不会空指针
            dto.setConversationId(conversation.getId());

            // 保存消息
            chatService.saveMessage(dto);

            sendToTarget(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToTarget(MessageDTO dto) {
        try {
            String msg = JSON.toJSONString(dto);
            if ("user".equals(dto.getSenderType())) {
                for (Session session : ADMIN_SESSION_MAP.values()) {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(msg);
                    }
                }
            } else if ("admin".equals(dto.getSenderType())) {
                Session session = USER_SESSION_MAP.get(dto.getReceiveUserId());
                if (session != null && session.isOpen()) {
                    session.getBasicRemote().sendText(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}
