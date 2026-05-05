package com.sau.gym.admin.agent.service.impl;

import com.sau.gym.admin.agent.assistant.GymAgentAssistant;
import com.sau.gym.admin.agent.store.AgentDraftStore;
import com.sau.gym.admin.agent.store.PendingDraft;
import com.sau.gym.admin.agent.store.PendingDraftType;
import com.sau.gym.admin.agent.tool.GymBookingTools;
import com.sau.gym.admin.agent.tool.GymShoppingTools;
import com.sau.gym.admin.mapper.ChatRecordMapper;
import com.sau.gym.admin.mapper.UserMapper;
import com.sau.gym.admin.agent.service.AgentService;
import com.sau.gym.model.dto.agent.AgentChatDto;
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
    public String chat(Long userId, AgentChatDto agentChatDto) {
        String message = agentChatDto.getMessage();

        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("消息不能为空");
        }

        //去掉空白部分
        message = message.trim();

        String reply;

        try {
            //1. 优先处理确认/取消类指令。
            reply = handlePendingAction(userId, message);
            if (reply != null) {
                saveChatRecord(userId, message, reply);
                return reply;
            }

            //2. 构造带页面上下文的Agent输入。
            String agentInput = buildAgentInput(message, agentChatDto.getVenueId(), agentChatDto.getCourtId());

            // 2. 走 LangChain4j agent
            reply = gymAgentAssistant.chat(userId, agentInput);

            // 3. 落库
            saveChatRecord(userId, message, reply);
            return reply;
        } catch (Exception e) {
            e.printStackTrace();
            reply = "AI服务异常，请稍后再试。";
            saveChatRecord(userId, message, reply);
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

    /**
     * 构造传给 Agent 的输入内容。
     * @param userMessage 用户原始消息
     * @param venueId 当前页面场馆ID，可为空
     * @param courtId 当前页面场地ID，可为空
     * @return 增强后的 Agent 输入
     */
    private String buildAgentInput(String userMessage, Long venueId, Long courtId) {
        StringBuilder builder = new StringBuilder();

        builder.append("用户问题：")
                .append(userMessage)
                .append("\n");

        if (venueId != null) {
            builder.append("当前页面场馆ID：")
                    .append(venueId)
                    .append("\n");
        }

        if (courtId != null) {
            builder.append("当前页面场地ID：")
                    .append(courtId)
                    .append("\n");
        }

        builder.append("如果用户问题中的“这个场馆”“这个场地”“这里”指代不明确，")
                .append("请优先使用上述页面上下文。");

        return builder.toString();
    }
}