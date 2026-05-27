package com.sau.gym.admin.agent.tool.executor.impl;

import com.sau.gym.admin.agent.service.AgentToolGuardService;
import com.sau.gym.admin.agent.tool.AgentToolCodes;
import com.sau.gym.admin.agent.tool.executor.AbstractGymAgentToolExecutor;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteContext;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteResult;
import com.sau.gym.admin.enums.AgentRiskLevel;
import com.sau.gym.admin.mapper.NoticeMapper;
import com.sau.gym.model.entity.notice.Notice;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 作者:hfj
 * 功能:查询最新公告工具执行器
 * 作用:
 * 1. 查询系统最新公告。
 * 2. 返回公告标题和内容。
 * 3. 这是低风险只读工具。
 * 日期: 2026/5/27 20:19
 */
@Component
public class QueryNoticeListToolExecutor extends AbstractGymAgentToolExecutor {

    private final NoticeMapper noticeMapper;

    public QueryNoticeListToolExecutor(
            AgentToolGuardService agentToolGuardService,
            NoticeMapper noticeMapper
    ) {
        super(agentToolGuardService);
        this.noticeMapper = noticeMapper;
    }

    @Override
    public String toolCode() {
        return AgentToolCodes.QUERY_NOTICE_LIST;
    }

    @Override
    public String toolName() {
        return "查询最新公告";
    }

    @Override
    public String description() {
        return "查询平台最新公告、通知和活动说明。适合用户问最新通知、公告、平台活动等问题。";
    }

    @Override
    public AgentRiskLevel riskLevel() {
        return AgentRiskLevel.LOW;
    }

    @Override
    public boolean needLogin() {
        return false;
    }

    @Override
    public boolean needConfirm() {
        return false;
    }

    @Override
    public int rateLimitSeconds() {
        return 3;
    }

    @Override
    protected AgentToolExecuteResult doExecute(AgentToolExecuteContext context) {
        List<Notice> noticeList = noticeMapper.findAllNotice();

        if (noticeList == null || noticeList.isEmpty()) {
            return AgentToolExecuteResult.success(
                    "当前没有公告。",
                    Collections.emptyList()
            );
        }

        int limit = Math.min(noticeList.size(), 5);

        List<Map<String, Object>> dataList = new ArrayList<>();
        StringBuilder message = new StringBuilder("最新公告如下：\n");

        for (int i = 0; i < limit; i++) {
            Notice notice = noticeList.get(i);

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("noticeId", notice.getId());
            item.put("title", notice.getTitle());
            item.put("content", notice.getContent());
            dataList.add(item);

            message.append(i + 1)
                    .append(". ")
                    .append(notice.getTitle())
                    .append("：")
                    .append(notice.getContent())
                    .append("\n");
        }

        return AgentToolExecuteResult.success(message.toString(), dataList);
    }
}
