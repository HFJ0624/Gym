package com.sau.gym.admin.agent.service.impl;

import com.sau.gym.admin.agent.assistant.GymAgentAssistant;
import com.sau.gym.admin.agent.context.AgentTraceContext;
import com.sau.gym.admin.agent.context.AgentTraceInfo;
import com.sau.gym.admin.agent.memory.AgentBusinessContext;
import com.sau.gym.admin.agent.service.*;
import com.sau.gym.admin.agent.store.AgentDraftStore;
import com.sau.gym.admin.agent.store.PendingDraft;
import com.sau.gym.admin.agent.store.PendingDraftType;
import com.sau.gym.admin.agent.tool.GymBookingTools;
import com.sau.gym.admin.agent.tool.GymShoppingTools;
import com.sau.gym.admin.mapper.ChatRecordMapper;
import com.sau.gym.admin.mapper.UserMapper;
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

    private final AgentContextEnhanceService contextEnhanceService;

    private final AgentCancelBookingService agentCancelBookingService;


    public AgentServiceImpl(GymAgentAssistant gymAgentAssistant,
                            AgentDraftStore agentDraftStore,
                            GymBookingTools gymBookingTools,
                            GymShoppingTools gymShoppingTools,
                            ChatRecordMapper chatRecordMapper,
                            UserMapper userMapper,
                            AgentDirectRouteService agentDirectRouteService,
                            AgentContextEnhanceService contextEnhanceService,
                            AgentCancelBookingService agentCancelBookingService
                            ) {
        this.gymAgentAssistant = gymAgentAssistant;
        this.agentDraftStore = agentDraftStore;
        this.gymBookingTools = gymBookingTools;
        this.gymShoppingTools = gymShoppingTools;
        this.chatRecordMapper = chatRecordMapper;
        this.userMapper = userMapper;
        this.agentDirectRouteService = agentDirectRouteService;
        this.contextEnhanceService = contextEnhanceService;
        this.agentCancelBookingService = agentCancelBookingService;
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

            //1.在处理本轮消息前，先读取并更新业务上下文。
            AgentBusinessContext agentBusinessContext = contextEnhanceService.prepareBeforeAgent(userId, message, agentChatDto);

            //设置 Agent 调用链上下文,AOP 记录工具日志时，会从 ThreadLocal 里读取
            AgentTraceContext.set(new AgentTraceInfo(traceId, userId, message));

            //1. 优先处理确认/取消类指令。
            reply = handlePendingAction(userId, message);
            if (reply != null) {
                saveChatRecord(userId, message, reply);
                return reply;
            }

            Long effectiveVenueId = contextEnhanceService.getEffectiveVenueId(agentChatDto, agentBusinessContext);
            Long effectiveCourtId = contextEnhanceService.getEffectiveCourtId(agentChatDto, agentBusinessContext);

            //尝试走直达路由,命中后不会请求大模型,优化模型速度压力
            reply = agentDirectRouteService.tryHandle(userId, message, effectiveVenueId, effectiveCourtId);
            if (reply != null) {
                saveChatRecord(userId, message, reply);
                return reply;
            }

            //2. 构造带业务上下文的 Agent 输入。上下文存在redis里面
            String agentInput = buildAgentInput(message, agentChatDto, agentBusinessContext);

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

        //确认取消预约
        if (text.startsWith("确认取消预约") || text.startsWith("确认取消")) {
            long start = System.currentTimeMillis();

            String token;

            if (text.startsWith("确认取消预约")) {
                token = extractConfirmToken(text, "确认取消预约");
            } else {
                token = extractConfirmToken(text, "确认取消");
            }

            String reply = agentCancelBookingService.confirmCancelBooking(userId, token);

            agentToolLogService.record(
                    "confirmCancelBooking",
                    "用户确认取消预约草稿，执行真实取消预约业务",
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
     *
     * 这里要明确区分三类信息：
     * 1. 用户原始问题
     * 2. 当前页面上下文
     * 3. Redis 中保存的业务上下文
     *
     * 这些内容最终都会传给大模型。
     */
    private String buildAgentInput(String userMessage,
                                   AgentChatDto agentChatDto,
                                   AgentBusinessContext businessContext) {
        StringBuilder builder = new StringBuilder();

        //Redis 业务上下文。
        String contextPrompt = contextEnhanceService.buildContextPrompt(businessContext);
        if (contextPrompt != null && !contextPrompt.trim().isEmpty()) {
            builder.append(contextPrompt).append("\n");
        }

        //当前页面上下文。这个上下文来自前端本次请求。例如用户正在某个场馆详情页聊天，前端就可以传 venueId。
        builder.append("【当前页面上下文】\n");

        if (agentChatDto != null && agentChatDto.getVenueId() != null) {
            builder.append("当前页面场馆ID：")
                    .append(agentChatDto.getVenueId())
                    .append("\n");
        }

        if (agentChatDto != null && agentChatDto.getCourtId() != null) {
            builder.append("当前页面场地ID：")
                    .append(agentChatDto.getCourtId())
                    .append("\n");
        }

        if ((agentChatDto == null || agentChatDto.getVenueId() == null)
                && businessContext != null
                && businessContext.getLastVenueId() != null) {
            builder.append("本次请求未传当前场馆ID，可参考最近场馆ID：")
                    .append(businessContext.getLastVenueId())
                    .append("\n");
        }

        if ((agentChatDto == null || agentChatDto.getCourtId() == null)
                && businessContext != null
                && businessContext.getLastCourtId() != null) {
            builder.append("本次请求未传当前场地ID，可参考最近场地ID：")
                    .append(businessContext.getLastCourtId())
                    .append("\n");
        }

        //用户原始问题。
        builder.append("\n【用户问题】\n")
                .append(userMessage)
                .append("\n");

        //强约束规则。这里要再次提醒模型：上下文不代表可以编造结果。预约动作必须走工具和后端 Service。
        builder.append("\n【处理要求】\n")
                .append("1. 如果用户说“这个场馆”“这个场地”“这里”“刚才那个”，优先结合业务上下文和页面上下文。\n")
                .append("2. 如果上下文中已有场馆ID、场地ID、日期、开始时间、结束时间，可以用于生成预约草稿。\n")
                .append("3. 如果缺少必要信息，不要编造，应继续追问用户。\n")
                .append("4. 涉及预约、下单等真实业务动作，必须调用工具，不允许直接声称操作成功。\n");

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