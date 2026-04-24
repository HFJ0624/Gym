package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.agent.GymAgentAssistant;
import com.sau.gym.admin.agent.store.AgentDraftStore;
import com.sau.gym.admin.agent.store.PendingDraft;
import com.sau.gym.admin.agent.store.PendingDraftType;
import com.sau.gym.admin.agent.tool.GymBookingTools;
import com.sau.gym.admin.agent.tool.GymShoppingTools;
import com.sau.gym.admin.mapper.ChatRecordMapper;
import com.sau.gym.admin.mapper.UserMapper;
import com.sau.gym.admin.service.AgentService;
import com.sau.gym.model.entity.chat.ChatRecord;
import com.sau.gym.model.entity.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 作者:hfj
 * 功能:Agent 主服务
 * 日期: 2026/3/10 16:28
 */
@Service
public class AgentServiceImpl implements AgentService {

    private final GymAgentAssistant gymAgentAssistant;
    private final AgentDraftStore agentDraftStore;
    private final GymBookingTools gymBookingTools;
    private final GymShoppingTools gymShoppingTools;
    private final ChatRecordMapper chatRecordMapper;
    private final UserMapper userMapper;

    public AgentServiceImpl(GymAgentAssistant gymAgentAssistant,
                            AgentDraftStore agentDraftStore,
                            GymBookingTools gymBookingTools,
                            GymShoppingTools gymShoppingTools,
                            ChatRecordMapper chatRecordMapper,
                            UserMapper userMapper) {
        this.gymAgentAssistant = gymAgentAssistant;
        this.agentDraftStore = agentDraftStore;
        this.gymBookingTools = gymBookingTools;
        this.gymShoppingTools = gymShoppingTools;
        this.chatRecordMapper = chatRecordMapper;
        this.userMapper = userMapper;
    }

    @Override
    public String chat(Long userId, String userMessage) {
        String reply;
        try {
            // 1. 优先处理确认/取消
            reply = handlePendingAction(userId, userMessage);
            if (reply != null) {
                saveChatRecord(userId, userMessage, reply);
                return reply;
            }

            // 2. 走 LangChain4j agent
            reply = gymAgentAssistant.chat(userId, userMessage);

            // 3. 落库
            saveChatRecord(userId, userMessage, reply);
            return reply;
        } catch (Exception e) {
            e.printStackTrace();
            reply = "AI服务异常，请稍后再试。";
            saveChatRecord(userId, userMessage, reply);
            return reply;
        }
    }

    private String handlePendingAction(Long userId, String userMessage) {
        PendingDraft draft = agentDraftStore.get(userId);
        if (draft == null) {
            return null;
        }

        String msg = userMessage == null ? "" : userMessage.trim();

        if (isCancel(msg)) {
            agentDraftStore.clear(userId);
            return "已取消本次待确认操作。";
        }

        if (isConfirmBooking(msg) && draft.type() == PendingDraftType.BOOKING) {
            return gymBookingTools.confirmPendingBooking(userId);
        }

        if (isConfirmShopping(msg) && draft.type() == PendingDraftType.SHOPPING) {
            return gymShoppingTools.confirmPendingShopping(userId);
        }

        // 有草稿但用户没有明确确认/取消，则继续交给模型
        return null;
    }

    private boolean isCancel(String msg) {
        return "取消".equals(msg)
                || "算了".equals(msg)
                || "不用了".equals(msg)
                || "放弃".equals(msg);
    }

    private boolean isConfirmBooking(String msg) {
        return "确认预约".equals(msg) || "确认".equals(msg);
    }

    private boolean isConfirmShopping(String msg) {
        return "确认下单".equals(msg) || "确认".equals(msg);
    }

    private void saveChatRecord(Long userId, String userMessage, String aiReply) {
        ChatRecord record = new ChatRecord();
        record.setUserId(userId);

        User user = userMapper.selectById(userId);
        if (user != null) {
            record.setUsername(user.getUsername());
        }

        record.setSessionId(UUID.randomUUID().toString());
        record.setUserMessage(userMessage);
        record.setAiReply(aiReply);
        record.setCreateTime(LocalDateTime.now());

        chatRecordMapper.insertChatRecord(record);
    }
}