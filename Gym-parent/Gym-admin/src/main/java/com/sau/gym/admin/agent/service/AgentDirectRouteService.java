package com.sau.gym.admin.agent.service;

public interface AgentDirectRouteService {

    /**
     * 尝试处理当前场地预约直达请求。
     *
     * @param userId 用户ID
     * @param message 用户原始消息
     * @param venueId 当前页面场馆ID
     * @param courtId 当前页面场地ID
     * @return 命中直达路由则返回回复内容；未命中返回 null
     */
    String tryHandle(Long userId, String message, Long venueId, Long courtId);
}
