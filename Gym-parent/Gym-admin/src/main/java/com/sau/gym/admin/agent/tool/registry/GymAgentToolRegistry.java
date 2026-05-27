package com.sau.gym.admin.agent.tool.registry;

import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteContext;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteResult;
import com.sau.gym.admin.agent.tool.executor.GymAgentToolExecutor;
import com.sau.gym.admin.enums.AgentIntent;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 作者:hfj
 * 功能:Gym Agent 工具注册器
 * 作用:
 * 系统启动时自动收集所有实现 GymAgentToolExecutor 的工具执行器，
 * 并按照 toolCode 注册到 Map 中。
 * 日期: 2026/5/27 15:31
 */
@Component
public class GymAgentToolRegistry {

    /**
     * Spring 自动注入所有 GymAgentToolExecutor 实现类。
     */
    private final List<GymAgentToolExecutor> executors;

    /**
     * 工具执行器 Map。
     * key:
     * toolCode，例如 ask_gym_knowledge
     * value:
     * 具体工具执行器
     */
    private final Map<String, GymAgentToolExecutor> executorMap = new LinkedHashMap<>();

    /**
     * 工具元信息 Map。
     * key:
     * toolCode
     * value:
     * 工具元信息
     */
    private final Map<String, AgentToolInfo> toolInfoMap = new LinkedHashMap<>();

    public GymAgentToolRegistry(List<GymAgentToolExecutor> executors) {
        this.executors = executors == null ? Collections.emptyList() : executors;
    }

    /**
     * Spring Bean 初始化完成后自动执行。
     * 作用:
     * 把所有 Executor 注册到 executorMap 和 toolInfoMap。
     */
    @PostConstruct
    public void init() {
        for (GymAgentToolExecutor executor : executors) {
            register(executor);
        }
    }

    /**
     * 注册单个工具执行器。
     *
     * @param executor 工具执行器
     */
    private void register(GymAgentToolExecutor executor) {

        //1. 空对象直接忽略。
        if (executor == null) {
            return;
        }

        // 2. toolCode 不能为空。
        String toolCode = executor.toolCode();

        if (!StringUtils.hasText(toolCode)) {
            throw new IllegalStateException(
                    "Agent 工具注册失败：toolCode 不能为空，工具类="
                            + executor.getClass().getName()
            );
        }

        //3. toolCode 不能重复。
        if (executorMap.containsKey(toolCode)) {
            GymAgentToolExecutor exists = executorMap.get(toolCode);

            throw new IllegalStateException(
                    "Agent 工具注册失败：toolCode 重复，toolCode="
                            + toolCode
                            + "，已存在工具="
                            + exists.getClass().getName()
                            + "，重复工具="
                            + executor.getClass().getName()
            );
        }

        //4. 注册工具执行器。
        executorMap.put(toolCode, executor);

        //5. 构造并注册工具元信息。
        toolInfoMap.put(toolCode, buildToolInfo(executor));
    }

    /**
     * 根据 Executor 构造工具元信息。
     */
    private AgentToolInfo buildToolInfo(GymAgentToolExecutor executor) {
        AgentToolInfo info = new AgentToolInfo();

        info.setToolCode(executor.toolCode());
        info.setToolName(executor.toolName());
        info.setDescription(executor.description());
        info.setRiskLevel(
                executor.riskLevel() == null ? null : executor.riskLevel().name()
        );
        info.setNeedLogin(executor.needLogin());
        info.setNeedConfirm(executor.needConfirm());
        info.setRateLimitSeconds(executor.rateLimitSeconds());
        info.setEnabled(true);

        if (executor.paramDefinitions() != null) {
            info.setParamDefinitions(executor.paramDefinitions());
        }

        return info;
    }

    /**
     * 根据工具编码获取工具执行器。
     *
     * @param toolCode 工具编码
     * @return 工具执行器，不存在则返回 null
     */
    public GymAgentToolExecutor getExecutor(String toolCode) {
        if (!StringUtils.hasText(toolCode)) {
            return null;
        }

        return executorMap.get(toolCode);
    }

    /**
     * 根据工具编码获取工具元信息。
     *
     * @param toolCode 工具编码
     * @return 工具元信息，不存在则返回 null
     */
    public AgentToolInfo getToolInfo(String toolCode) {
        if (!StringUtils.hasText(toolCode)) {
            return null;
        }

        return toolInfoMap.get(toolCode);
    }

    /**
     * 判断某个工具是否存在。
     */
    public boolean containsTool(String toolCode) {
        return StringUtils.hasText(toolCode) && executorMap.containsKey(toolCode);
    }

    /**
     * 获取所有工具元信息。
     *
     * @return 工具列表
     */
    public List<AgentToolInfo> listTools() {
        List<AgentToolInfo> list = new ArrayList<>(toolInfoMap.values());

        /*
         * 按工具编码排序，保证返回顺序稳定。
         */
        list.sort(Comparator.comparing(AgentToolInfo::getToolCode));

        return list;
    }

    /**
     * 根据意图获取建议工具执行器。
     * 说明:
     * 你前面定义的 AgentIntent 里有 suggestedToolName。
     * 这里直接使用 suggestedToolName 作为 toolCode 查找工具。
     */
    public GymAgentToolExecutor getExecutorByIntent(AgentIntent intent) {
        if (intent == null || !StringUtils.hasText(intent.getSuggestedToolName())) {
            return null;
        }

        return getExecutor(intent.getSuggestedToolName());
    }

    /**
     * 执行指定工具。
     *
     * @param toolCode 工具编码
     * @param context 工具执行上下文
     * @return 工具执行结果
     */
    public AgentToolExecuteResult execute(String toolCode, AgentToolExecuteContext context) {

        //1. 工具编码为空，直接返回参数错误。
        if (!StringUtils.hasText(toolCode)) {
            return AgentToolExecuteResult.paramError(
                    "工具编码不能为空。",
                    "toolCode is blank"
            );
        }

        //2. 根据 toolCode 查找执行器。
        GymAgentToolExecutor executor = getExecutor(toolCode);

        //3. 工具不存在，返回失败。
        if (executor == null) {
            return AgentToolExecuteResult.failed(
                    "未找到可用工具：" + toolCode,
                    "Tool executor not found, toolCode=" + toolCode
            );
        }

        //4. 调用工具执行器。风控、限流、参数校验、异常捕获已经在 AbstractGymAgentToolExecutor 中统一处理。
        return executor.execute(context);
    }

    /**
     * 根据意图执行建议工具。
     *
     * @param intent 当前用户意图
     * @param context 工具执行上下文
     * @return 工具执行结果
     */
    public AgentToolExecuteResult executeByIntent(AgentIntent intent, AgentToolExecuteContext context) {
        if (intent == null) {
            return AgentToolExecuteResult.paramError(
                    "用户意图不能为空。",
                    "intent is null"
            );
        }

        String toolCode = intent.getSuggestedToolName();
        return execute(toolCode, context);
    }

    /**
     * 构造给大模型看的工具说明提示词。
     * 作用:
     * 在 buildAgentInput 里拼接这段提示词后，
     * 大模型可以知道当前系统有哪些工具、每个工具适合做什么、需要什么参数。
     */
    public String buildToolPrompt() {
        List<AgentToolInfo> tools = listTools();

        if (tools.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("〖可用工具列表〗\n");
        builder.append("下面是当前体育场馆智能助手可以使用的工具。选择工具时必须结合用户意图、问题重写结果和业务上下文。\n");

        for (AgentToolInfo tool : tools) {
            if (tool == null || !tool.isEnabled()) {
                continue;
            }

            builder.append("- 工具编码：")
                    .append(tool.getToolCode())
                    .append("\n");

            builder.append("  工具名称：")
                    .append(tool.getToolName())
                    .append("\n");

            builder.append("  工具说明：")
                    .append(tool.getDescription())
                    .append("\n");

            builder.append("  风险等级：")
                    .append(tool.getRiskLevel())
                    .append("\n");

            builder.append("  是否需要登录：")
                    .append(tool.isNeedLogin())
                    .append("\n");

            builder.append("  是否需要确认：")
                    .append(tool.isNeedConfirm())
                    .append("\n");

            if (tool.getParamDefinitions() != null && !tool.getParamDefinitions().isEmpty()) {
                builder.append("  参数：\n");

                tool.getParamDefinitions().forEach(param -> {
                    if (param == null) {
                        return;
                    }

                    builder.append("    - ")
                            .append(param.getName())
                            .append("：")
                            .append(param.getDescription())
                            .append("，类型=")
                            .append(param.getType())
                            .append("，必填=")
                            .append(param.isRequired());

                    if (StringUtils.hasText(param.getExample())) {
                        builder.append("，示例=")
                                .append(param.getExample());
                    }

                    builder.append("\n");
                });
            }

            builder.append("\n");
        }

        builder.append("工具选择规则：\n");
        builder.append("1. 查询规则、退款、停车、开放时间、公告、FAQ 时，优先使用 ask_gym_knowledge。\n");
        builder.append("2. 用户想预约时，优先使用 create_booking_draft 生成预约草稿，不允许直接声称预约成功。\n");
        builder.append("3. 高风险操作必须经过用户确认，不能由模型自行执行。\n");
        builder.append("4. 如果缺少必填参数，必须先追问用户，不能编造参数。\n");

        return builder.toString();
    }
}
