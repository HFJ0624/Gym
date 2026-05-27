package com.sau.gym.admin.agent.tool.executor;

import com.sau.gym.admin.agent.service.AgentToolGuardService;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 作者:hfj
 * 功能:Gym Agent 工具执行器抽象父类
 * 作用:
 * 把所有工具公共逻辑收敛到这里。
 * 公共逻辑包括:
 * 1. 登录校验
 * 2. 工具风控
 * 3. 工具限流
 * 4. 必填参数校验
 * 5. 异常捕获
 * 6. 执行耗时统计
 * 子类只需要实现 doExecute()，写自己的业务逻辑即可。
 * 日期: 2026/5/27 14:52
 */
public abstract class AbstractGymAgentToolExecutor implements GymAgentToolExecutor{

    //Agent 工具风控服务。
    private final AgentToolGuardService agentToolGuardService;

    public AbstractGymAgentToolExecutor(AgentToolGuardService agentToolGuardService) {
        this.agentToolGuardService = agentToolGuardService;
    }

    //统一执行入口。子类不要重写这个方法。子类应该重写 doExecute()。
    @Override
    public final AgentToolExecuteResult execute(AgentToolExecuteContext context) {
        long start = System.currentTimeMillis();

        try {

            //1. 上下文为空时，直接返回错误。
            if (context == null) {
                return withCostTime(
                        AgentToolExecuteResult.paramError(
                                "工具执行上下文不能为空。",
                                "AgentToolExecuteContext is null"
                        ),
                        start
                );
            }

            //2. 工具调用前风控检查。
            String blockedMessage = agentToolGuardService.checkBeforeToolCall(
                    context.getUserId(),
                    toolCode(),
                    toolName(),
                    riskLevel(),
                    needLogin(),
                    needConfirm(),
                    rateLimitSeconds()
            );

            if (StringUtils.hasText(blockedMessage)) {
                //如果工具需要登录，但 userId 为空，返回权限不足。
                if (needLogin() && context.getUserId() == null) {
                    return withCostTime(
                            AgentToolExecuteResult.permissionDenied(blockedMessage),
                            start
                    );
                }

                //其他情况一般是限流或风控拦截。
                return withCostTime(
                        AgentToolExecuteResult.rateLimited(blockedMessage),
                        start
                );
            }

            //3. 统一校验必填参数。
            AgentToolExecuteResult paramCheckResult = checkRequiredParams(context);
            if (paramCheckResult != null) {
                return withCostTime(paramCheckResult, start);
            }

            //4. 调用子类具体业务逻辑。
            AgentToolExecuteResult result = doExecute(context);

            //5. 子类返回 null 时兜底。
            if (result == null) {
                result = AgentToolExecuteResult.failed(
                        "工具执行失败，未返回有效结果。",
                        "doExecute returned null"
                );
            }

            //6. 补充耗时。
            return withCostTime(result, start);

        } catch (Exception e) {

            //7. 捕获异常。
            return withCostTime(
                    AgentToolExecuteResult.error(
                            "工具执行过程中出现异常，请稍后再试。",
                            e.getMessage()
                    ),
                    start
            );
        }
    }

    /**
     * 子类真正实现业务逻辑的方法。
     */
    protected abstract AgentToolExecuteResult doExecute(AgentToolExecuteContext context);

    /**
     * 参数定义默认返回空列表。
     * 子类如果有参数要求，可以重写这个方法。
     */
    @Override
    public List<AgentToolParamDefinition> paramDefinitions() {
        return Collections.emptyList();
    }

    /**
     * 默认限流 0 秒，表示不限流。
     * 子类可以根据工具类型重写。
     */
    @Override
    public int rateLimitSeconds() {
        return 0;
    }

    /**
     * 统一检查必填参数。
     */
    private AgentToolExecuteResult checkRequiredParams(AgentToolExecuteContext context) {
        List<AgentToolParamDefinition> definitions = paramDefinitions();

        if (definitions == null || definitions.isEmpty()) {
            return null;
        }

        for (AgentToolParamDefinition definition : definitions) {
            if (definition == null || !definition.isRequired()) {
                continue;
            }

            String paramName = definition.getName();

            if (!StringUtils.hasText(paramName)) {
                continue;
            }

            Object value = context.getParams().get(paramName);

            //参数不存在，或者字符串为空，都认为缺少必填参数。
            if (value == null || !StringUtils.hasText(String.valueOf(value))) {
                return AgentToolExecuteResult.paramError(
                        "缺少必要参数：" + definition.getDescription(),
                        "Missing required param: " + paramName
                );
            }
        }

        return null;
    }

    /**
     * 给结果补充耗时。
     */
    private AgentToolExecuteResult withCostTime(
            AgentToolExecuteResult result,
            long start
    ) {
        if (result != null) {
            result.setCostTimeMs(System.currentTimeMillis() - start);
        }

        return result;
    }
}
