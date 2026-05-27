package com.sau.gym.admin.agent.service.impl;

import com.alibaba.fastjson.JSON;
import com.sau.gym.admin.agent.assistant.GymAgentAssistant;

import com.sau.gym.admin.agent.intent.GymIntentRouter;
import com.sau.gym.admin.agent.intent.IntentRouteRequest;
import com.sau.gym.admin.agent.intent.IntentRouteResult;
import com.sau.gym.admin.agent.memory.model.AgentSessionMemory;
import com.sau.gym.admin.agent.memory.service.AgentSessionMemoryService;
import com.sau.gym.admin.agent.rewrite.QuestionRewriteRequest;
import com.sau.gym.admin.agent.rewrite.QuestionRewriteResult;
import com.sau.gym.admin.agent.rewrite.service.QuestionRewriteService;
import com.sau.gym.admin.agent.security.AgentSafetyCheckResult;
import com.sau.gym.admin.agent.tool.registry.GymAgentToolRegistry;
import com.sau.gym.admin.agent.trace.AgentTraceInfo;
import com.sau.gym.admin.agent.memory.model.AgentBusinessContext;
import com.sau.gym.admin.agent.service.*;
import com.sau.gym.admin.agent.store.AgentDraftStore;
import com.sau.gym.admin.agent.tool.GymBookingTools;
import com.sau.gym.admin.agent.tool.GymShoppingTools;
import com.sau.gym.admin.agent.trace.AgentTraceContext;
import com.sau.gym.admin.agent.util.AgentUserContext;
import com.sau.gym.admin.mapper.ChatRecordMapper;
import com.sau.gym.admin.mapper.UserMapper;
import com.sau.gym.model.dto.agent.AgentChatDto;
import com.sau.gym.model.entity.chat.ChatRecord;
import com.sau.gym.model.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AgentToolLogService agentToolLogService;

    private final AgentDirectRouteService agentDirectRouteService;

    private final AgentContextEnhanceService contextEnhanceService;

    private final AgentCancelBookingService agentCancelBookingService;

    private final AgentTraceService agentTraceService;

    private final AgentSafetyService agentSafetyService;

    private final GymIntentRouter gymIntentRouter;

    private final QuestionRewriteService questionRewriteService;

    private final AgentSessionMemoryService agentSessionMemoryService;

    private final GymAgentToolRegistry gymAgentToolRegistry;


    public AgentServiceImpl(GymAgentAssistant gymAgentAssistant,
                            AgentDraftStore agentDraftStore,
                            GymBookingTools gymBookingTools,
                            GymShoppingTools gymShoppingTools,
                            ChatRecordMapper chatRecordMapper,
                            UserMapper userMapper,
                            AgentDirectRouteService agentDirectRouteService,
                            AgentContextEnhanceService contextEnhanceService,
                            AgentCancelBookingService agentCancelBookingService,
                            AgentTraceService agentTraceService,
                            AgentSafetyService agentSafetyService,
                            GymIntentRouter gymIntentRouter,
                            QuestionRewriteService questionRewriteService,
                            AgentSessionMemoryService agentSessionMemoryService,
                            GymAgentToolRegistry gymAgentToolRegistry
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
        this.agentTraceService = agentTraceService;
        this.agentSafetyService = agentSafetyService;
        this.gymIntentRouter = gymIntentRouter;
        this.questionRewriteService = questionRewriteService;
        this.agentSessionMemoryService = agentSessionMemoryService;
        this.gymAgentToolRegistry = gymAgentToolRegistry;
    }

    @Override
    public String chat(Long userId, AgentChatDto agentChatDto) {
        String message = agentChatDto.getMessage();

        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("消息不能为空");
        }

        //去掉首尾空白
        message = message.trim();

        //为本次Agent对话生成 traceId。
        String traceId = UUID.randomUUID().toString().replace("-", "");

        String reply = null;

        long traceStart = System.currentTimeMillis();

        try {

            //1.设置当前线程Trace上下文。
            AgentTraceContext.set(new AgentTraceInfo(traceId, userId, message));

            //设置当前线程用户上下文。
            AgentUserContext.setUserId(userId);

            //2. 创建agent_trace主记录。
            agentTraceService.startTrace(traceId, userId, null, message);

            agentTraceService.addStep(
                    "TRACE_START",
                    "收到用户消息",
                    null,
                    message,
                    "SUCCESS",
                    null,
                    0L
            );

            //3. 用户输入安全检查。主要防：提示词注入 越权请求 删除类危险请求 绕过确认流程
            long safetyStart = System.currentTimeMillis();

            AgentSafetyCheckResult safetyResult = agentSafetyService.checkUserMessage(userId, message);

            if (!safetyResult.isAllowed()) {
                reply = safetyResult.getReason();

                agentTraceService.addStep(
                        "SAFETY_CHECK",
                        "用户输入安全检查未通过",
                        message,
                        reply,
                        "FAILED",
                        safetyResult.getReason(),
                        System.currentTimeMillis() - safetyStart
                );

                rememberRound(userId, message, reply);
                saveChatRecord(userId, message, reply);

                agentTraceService.finishFailed(
                        traceId,
                        reply,
                        safetyResult.getReason(),
                        System.currentTimeMillis() - traceStart
                );

                return reply;
            }

            agentTraceService.addStep(
                    "SAFETY_CHECK",
                    "用户输入安全检查通过",
                    message,
                    "allowed",
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - safetyStart
            );

            //4. 在处理本轮消息前，读取并更新业务上下文。
            long contextStart = System.currentTimeMillis();

            AgentBusinessContext agentBusinessContext =
                    contextEnhanceService.prepareBeforeAgent(userId, message, agentChatDto);

            agentTraceService.addStep(
                    "CONTEXT_PREPARE",
                    "业务上下文增强完成",
                    message,
                    agentBusinessContext == null ? null : agentBusinessContext.toString(),
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - contextStart
            );

            //4.1读取最近会话记忆。
            long memoryLoadStart = System.currentTimeMillis();

            AgentSessionMemory sessionMemory = agentSessionMemoryService.getMemory(userId, null);

            agentTraceService.addStep(
                    "SESSION_MEMORY_LOAD",
                    "读取最近会话记忆完成",
                    message,
                    JSON.toJSONString(sessionMemory),
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - memoryLoadStart
            );

            //5.优先处理确认/取消类指令。(用户回复确认预约和取消等不用走大模型)
            long pendingStart = System.currentTimeMillis();

            reply = handlePendingAction(userId, message);

            if (reply != null) {
                agentTraceService.addStep(
                        "PENDING_ACTION",
                        "命中确认/取消类指令",
                        message,
                        reply,
                        "SUCCESS",
                        null,
                        System.currentTimeMillis() - pendingStart
                );

                rememberRound(userId, message, reply);
                saveChatRecord(userId, message, reply);

                agentTraceService.addStep(
                        "FINAL_REPLY",
                        "返回确认/取消类指令结果",
                        null,
                        reply,
                        "SUCCESS",
                        null,
                        0L
                );

                agentTraceService.finishSuccess(
                        traceId,
                        reply,
                        System.currentTimeMillis() - traceStart
                );

                return reply;
            }

            // 6. 获取有效场馆ID和场地ID。先查看前端是否传入,没有在解析用户本次输入结果,没有再Redis历史上下文
            Long effectiveVenueId = contextEnhanceService.getEffectiveVenueId(agentChatDto, agentBusinessContext);

            Long effectiveCourtId = contextEnhanceService.getEffectiveCourtId(agentChatDto, agentBusinessContext);

            //7.意图识别
            //在真正调用大模型之前，先判断用户这一轮是预约、取消、查询场馆、查规则还是闲聊。这样后续可以减少大模型误调用工具的问题。
            long intentStart = System.currentTimeMillis();

            IntentRouteRequest intentRouteRequest = new IntentRouteRequest();
            intentRouteRequest.setUserId(userId);
            intentRouteRequest.setMessage(message);
            intentRouteRequest.setEffectiveVenueId(effectiveVenueId);
            intentRouteRequest.setEffectiveCourtId(effectiveCourtId);
            intentRouteRequest.setBusinessContext(agentBusinessContext);

            IntentRouteResult intentRouteResult = gymIntentRouter.route(intentRouteRequest);

            // 把意图识别结果写入 Trace，方便后台排查。
            agentTraceService.addStep(
                    "INTENT_ROUTE",
                    "用户意图识别完成",
                    message,
                    JSON.toJSONString(intentRouteResult),
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - intentStart
            );

            // 如果意图识别认为必须先追问，则直接返回，不调用大模型。
            // 示例: 用户只说“帮我预约一下”，但没有场馆、场地、时间等信息。
            if (intentRouteResult.isNeedClarify()) {
                reply = intentRouteResult.getClarifyQuestion();

                rememberRound(userId,message,reply);
                saveChatRecord(userId, message, reply);

                agentTraceService.addStep(
                        "INTENT_CLARIFY",
                        "意图识别发现信息不足，直接追问用户",
                        message,
                        reply,
                        "SUCCESS",
                        null,
                        0L
                );

                agentTraceService.finishSuccess(
                        traceId,
                        reply,
                        System.currentTimeMillis() - traceStart
                );

                return reply;
            }

            //8.问题重写
            long rewriteStart = System.currentTimeMillis();

            QuestionRewriteRequest rewriteRequest = new QuestionRewriteRequest();
            rewriteRequest.setUserId(userId);
            rewriteRequest.setOriginalQuestion(message);
            rewriteRequest.setIntentRouteResult(intentRouteResult);
            rewriteRequest.setEffectiveVenueId(effectiveVenueId);
            rewriteRequest.setEffectiveCourtId(effectiveCourtId);
            rewriteRequest.setBusinessContext(agentBusinessContext);
            rewriteRequest.setAgentChatDto(agentChatDto);

            QuestionRewriteResult rewriteResult = questionRewriteService.rewrite(rewriteRequest);

            // 防御性兜底。
            // 如果重写服务异常返回 null，就使用原始问题，避免影响主流程。
            if (rewriteResult == null) {
                rewriteResult = QuestionRewriteResult.noChange(message);
            }

            // 写入 Trace。
            // 这样后续可以排查“模型为什么调用了某个工具”，
            // 看它拿到的是原始问题，还是已经重写后的问题。
            agentTraceService.addStep(
                    "QUESTION_REWRITE",
                    "问题重写完成",
                    message,
                    JSON.toJSONString(rewriteResult),
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - rewriteStart
            );

            // 后续内部路由和大模型调用优先使用重写后的问题。
            // 注意:保存聊天记录时仍然保存用户原始问题 message。
            String rewrittenMessage = rewriteResult.getRewrittenQuestion();

            //8.尝试走直达路由。命中后不请求大模型，减少模型调用成本和响应时间。
            long directStart = System.currentTimeMillis();

            reply = agentDirectRouteService.tryHandle(
                    userId,
                    rewrittenMessage,
                    effectiveVenueId,
                    effectiveCourtId
            );

            if (reply != null) {
                agentTraceService.addStep(
                        "DIRECT_ROUTE",
                        "命中直达路由",
                        "originalMessage=" + message
                                + ", rewrittenMessage=" + rewrittenMessage
                                + ", effectiveVenueId=" + effectiveVenueId
                                + ", effectiveCourtId=" + effectiveCourtId,
                        reply,
                        "SUCCESS",
                        null,
                        System.currentTimeMillis() - directStart
                );

                rememberRound(userId, message, reply);
                saveChatRecord(userId, message, reply);

                agentTraceService.addStep(
                        "FINAL_REPLY",
                        "返回直达路由结果",
                        null,
                        reply,
                        "SUCCESS",
                        null,
                        0L
                );

                agentTraceService.finishSuccess(
                        traceId,
                        reply,
                        System.currentTimeMillis() - traceStart
                );

                return reply;
            }

            //9. 构造带业务上下文的Agent输入。(这里会把Redis里的业务上下文拼到用户输入前面)
            String agentInput = buildAgentInput(message, agentChatDto, agentBusinessContext,intentRouteResult,rewriteResult,sessionMemory);

            //10.调用LangChain4j Agent。
            long llmStart = System.currentTimeMillis();

            reply = gymAgentAssistant.chat(userId, agentInput);

            agentTraceService.addStep(
                    "LLM_CALL",
                    "调用 LangChain4j Agent 完成",
                    agentInput,
                    reply,
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - llmStart
            );

            //11.Agent调用完成后刷新业务上下文。
            long refreshStart = System.currentTimeMillis();

            contextEnhanceService.refreshAfterAgent(userId);

            agentTraceService.addStep(
                    "CONTEXT_REFRESH",
                    "Agent 调用后刷新业务上下文",
                    null,
                    "refreshAfterAgent done",
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - refreshStart
            );

            rememberRound(userId, message, reply);
            //12.保存聊天记录。
            saveChatRecord(userId, message, reply);

            //13.记录最终回复并结束Trace。
            agentTraceService.addStep(
                    "FINAL_REPLY",
                    "返回最终回复",
                    null,
                    reply,
                    "SUCCESS",
                    null,
                    0L
            );

            agentTraceService.finishSuccess(
                    traceId,
                    reply,
                    System.currentTimeMillis() - traceStart
            );

            return reply;

        } catch (Exception e) {
            e.printStackTrace();

            if (e.getMessage() != null && e.getMessage().contains("timed out")) {
                reply = "AI模型响应超时，请稍后重试，或把问题描述得更简短一些。";
            } else {
                reply = "AI服务异常，请稍后再试。";
            }

            rememberRound(userId, message, reply);
            //异常也要保存聊天记录。
            saveChatRecord(userId, message, reply);

            //异常也要写入 Trace。
            try {
                agentTraceService.addStep(
                        "TRACE_FAILED",
                        "Agent 调用异常",
                        message,
                        null,
                        "FAILED",
                        e.getMessage(),
                        System.currentTimeMillis() - traceStart
                );

                agentTraceService.finishFailed(
                        traceId,
                        reply,
                        e.getMessage(),
                        System.currentTimeMillis() - traceStart
                );
            } catch (Exception traceException) {
                //Trace 记录失败不能影响用户正常返回。
                traceException.printStackTrace();
            }

            return reply;

        } finally {
            //必须清理ThreadLocal。
            AgentTraceContext.clear();
            AgentUserContext.clear();
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
     * 当前输入由几部分组成:
     * 1. 业务上下文
     * 2. 会话记忆
     * 3. 当前页面上下文
     * 4. 意图识别结果
     * 5. 问题重写结果
     * 6. 可用工具列表
     * 7. 用户原始问题
     * 8. 系统重写后的问题
     */
    private String buildAgentInput(String userMessage,
                                   AgentChatDto agentChatDto,
                                   AgentBusinessContext businessContext,
                                   IntentRouteResult intentRouteResult,
                                   QuestionRewriteResult rewriteResult,
                                   AgentSessionMemory sessionMemory) {
        StringBuilder builder = new StringBuilder();

        // 1. 业务上下文。
        String contextPrompt = contextEnhanceService.buildContextPrompt(businessContext);
        if (contextPrompt != null && !contextPrompt.trim().isEmpty()) {
            builder.append(contextPrompt).append("\n");
        }

        // 2. 会话记忆。
        String memoryPrompt = agentSessionMemoryService.buildMemoryPrompt(sessionMemory);
        if (memoryPrompt != null && !memoryPrompt.trim().isEmpty()) {
            builder.append(memoryPrompt).append("\n");
        }

        // 3. 当前页面上下文。
        builder.append("〖当前页面上下文〗\n");

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

        // 4. 意图识别结果。
        if (intentRouteResult != null && intentRouteResult.getRoutePrompt() != null) {
            builder.append("\n")
                    .append(intentRouteResult.getRoutePrompt())
                    .append("\n");
        }

        // 5. 问题重写结果。
        if (rewriteResult != null && rewriteResult.getRewritePrompt() != null) {
            builder.append("\n")
                    .append(rewriteResult.getRewritePrompt())
                    .append("\n");
        }

        // 6. 可用工具列表。
        // 作用:
        // 让大模型知道当前系统有哪些工具、各自适合什么场景、需要哪些参数。
        String toolPrompt = gymAgentToolRegistry.buildToolPrompt();
        if (toolPrompt != null && !toolPrompt.trim().isEmpty()) {
            builder.append("\n")
                    .append(toolPrompt)
                    .append("\n");
        }

        // 7. 用户原始问题。
        builder.append("\n〖用户原始问题〗\n")
                .append(userMessage)
                .append("\n");

        // 8. 系统重写后的问题。
        if (rewriteResult != null
                && rewriteResult.getRewrittenQuestion() != null
                && !rewriteResult.getRewrittenQuestion().trim().isEmpty()) {
            builder.append("\n〖系统重写后的问题〗\n")
                    .append(rewriteResult.getRewrittenQuestion())
                    .append("\n");
        }

        // 9. 强约束。
        builder.append("\n〖处理要求〗\n")
                .append("1. 用户原始问题必须保留语义，系统重写问题只是帮助理解上下文。\n")
                .append("2. 最近会话记忆只用于理解多轮对话，不代表最终业务事实。\n")
                .append("3. 如果用户说“这个场馆”“这个场地”“这里”“刚才那个”，优先结合业务上下文、会话记忆和问题重写结果。\n")
                .append("4. 如果系统重写问题中包含场馆ID、场地ID、预约日期、开始时间、结束时间，调用工具时应优先使用这些结构化信息。\n")
                .append("5. 如果缺少必要信息，不要编造，应继续追问用户。\n")
                .append("6. 如果意图是 BOOKING_DRAFT，只能生成预约草稿，不能直接声称预约成功。\n")
                .append("7. 如果意图是 BOOKING_CANCEL，必须走取消预约确认流程，不能直接取消。\n")
                .append("8. 如果意图是 RAG_KNOWLEDGE，应优先调用知识库工具回答规则、公告、停车、退款、开放时间等问题。\n")
                .append("9. 工具调用结果才代表真实业务结果，不能仅凭模型推测声称预约成功、取消成功或退款成功。\n");

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

    /**
     * 保存一轮会话记忆。
     * 一轮会话包含:
     * 1. 用户原始输入
     * 2. AI 最终回复
     * 注意:
     * 这里保存的是原始用户消息和最终回复，
     * 不保存 agentInput、工具 JSON、Trace 信息。
     *
     * @param userId 用户ID
     * @param userMessage 用户原始消息
     * @param aiReply AI 最终回复
     */
    private void rememberRound(Long userId, String userMessage, String aiReply) {
        long start = System.currentTimeMillis();

        try {
            agentSessionMemoryService.appendRound(
                    userId,
                    null,
                    userMessage,
                    aiReply
            );

            agentTraceService.addStep(
                    "SESSION_MEMORY_SAVE",
                    "保存本轮会话记忆完成",
                    userMessage,
                    aiReply,
                    "SUCCESS",
                    null,
                    System.currentTimeMillis() - start
            );
        } catch (Exception e) {
            //会话记忆失败不能影响主聊天流程。
            agentTraceService.addStep(
                    "SESSION_MEMORY_SAVE",
                    "保存本轮会话记忆失败",
                    userMessage,
                    aiReply,
                    "FAILED",
                    e.getMessage(),
                    System.currentTimeMillis() - start
            );
        }
    }
}