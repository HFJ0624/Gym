package com.sau.gym.admin.agent.service.impl;

import com.sau.gym.admin.agent.assistant.GymAgentAssistant;
import com.sau.gym.admin.agent.context.AgentTraceContext;
import com.sau.gym.admin.agent.context.AgentTraceInfo;
import com.sau.gym.admin.agent.service.AgentDirectRouteService;
import com.sau.gym.admin.agent.service.AgentToolLogService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Autowired
    private AgentToolLogService agentToolLogService;

    private final AgentDirectRouteService agentDirectRouteService;


    public AgentServiceImpl(GymAgentAssistant gymAgentAssistant,
                            AgentDraftStore agentDraftStore,
                            GymBookingTools gymBookingTools,
                            GymShoppingTools gymShoppingTools,
                            ChatRecordMapper chatRecordMapper,
                            UserMapper userMapper,
                            AgentDirectRouteService agentDirectRouteService
                            ) {
        this.gymAgentAssistant = gymAgentAssistant;
        this.agentDraftStore = agentDraftStore;
        this.gymBookingTools = gymBookingTools;
        this.gymShoppingTools = gymShoppingTools;
        this.chatRecordMapper = chatRecordMapper;
        this.userMapper = userMapper;
        this.agentDirectRouteService = agentDirectRouteService;
    }

    @Override
    public String chat(Long userId, AgentChatDto agentChatDto) {
        String message = agentChatDto.getMessage();

        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("消息不能为空");
        }

        //去掉空白部分
        message = message.trim();

        //为本次 Agent对话生成一个traceId
        String traceId = UUID.randomUUID().toString().replace("-", "");
        String reply;

        try {

            //设置 Agent 调用链上下文,AOP 记录工具日志时，会从 ThreadLocal 里读取
            AgentTraceContext.set(new AgentTraceInfo(traceId, userId, message));

            //1. 优先处理确认/取消类指令。
            reply = handlePendingAction(userId, message);
            if (reply != null) {
                saveChatRecord(userId, message, reply);
                return reply;
            }

            //尝试走直达路由,命中后不会请求大模型,优化模型速度压力
            reply = agentDirectRouteService.tryHandle(userId, message, agentChatDto.getVenueId(), agentChatDto.getCourtId());
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

            if (e.getMessage() != null && e.getMessage().contains("timed out")) {
                reply = "AI模型响应超时，请稍后重试，或把问题描述得更简短一些。";
            } else {
                reply = "AI服务异常，请稍后再试。";
            }

            saveChatRecord(userId, message, reply);

            return reply;
        }finally {
            //必须清理 ThreadLocal,Tomcat 线程会复用，如果不 clear,下一次请求可能读到上一次用户的 trace 信息。
            AgentTraceContext.clear();
        }
    }

    private String handlePendingAction(Long userId, String message) {

        if (message == null) {
            return null;
        }

        String text = message.trim();

        //确认预约
        if (text.startsWith("确认预约")) {
            long start = System.currentTimeMillis();

            String token = extractConfirmToken(text, "确认预约");
            String reply = gymBookingTools.confirmPendingBooking(userId, token);

            agentToolLogService.record(
                    "confirmPendingBooking",
                    "用户确认预约草稿，执行真实预约业务",
                    this.getClass().getName(),
                    "handlePendingAction",
                    "{\"message\":\"" + text + "\"}",
                    reply,
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - start
            );

            return reply;
        }

        //确认商品下单
        if (text.startsWith("确认下单")) {
            long start = System.currentTimeMillis();

            String token = extractConfirmToken(text, "确认下单");
            String reply = gymShoppingTools.confirmPendingShopping(userId, token);

            agentToolLogService.record(
                    "confirmPendingShopping",
                    "用户确认商品下单草稿，执行真实商品下单业务",
                    this.getClass().getName(),
                    "handlePendingAction",
                    "{\"message\":\"" + text + "\"}",
                    reply,
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - start
            );

            return reply;
        }

        //取消当前待确认草稿
        if ("取消".equals(text) || "取消操作".equals(text)) {
            long start = System.currentTimeMillis();

            agentDraftStore.clear(userId);

            String reply = "已取消当前待确认操作。";

            agentToolLogService.record(
                    "clearPendingDraft",
                    "用户取消当前待确认草稿",
                    this.getClass().getName(),
                    "handlePendingAction",
                    "{\"message\":\"" + text + "\"}",
                    reply,
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - start
            );

            return reply;
        }

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

    /**
     * 从用户输入中提取确认码。
     */
    private String extractConfirmToken(String text, String prefix) {
        if (text == null || prefix == null) {
            return null;
        }

        String token = text.substring(prefix.length()).trim();

        if (token.isEmpty()) {
            return null;
        }

        return token;
    }
}