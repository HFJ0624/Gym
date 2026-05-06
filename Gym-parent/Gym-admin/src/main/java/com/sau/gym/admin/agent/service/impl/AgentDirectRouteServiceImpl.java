package com.sau.gym.admin.agent.service.impl;

import com.sau.gym.admin.agent.model.BookingTimeInfo;
import com.sau.gym.admin.agent.parser.BookingTimeParser;
import com.sau.gym.admin.agent.service.AgentDirectRouteService;
import com.sau.gym.admin.agent.tool.GymBookingTools;
import org.springframework.stereotype.Service;

/**
 * 作者:hfj
 * 功能:Agent 直达路由服务实现
 * 识别非常明确的业务请求，直接调用工具，减少大模型调用
 * 当前支持：
 * 当前场地预约直达。
 * 日期: 2026/5/6 8:55
 */
@Service
public class AgentDirectRouteServiceImpl implements AgentDirectRouteService {

    private final BookingTimeParser bookingTimeParser;
    private final GymBookingTools gymBookingTools;

    public AgentDirectRouteServiceImpl(BookingTimeParser bookingTimeParser,
                                       GymBookingTools gymBookingTools) {
        this.bookingTimeParser = bookingTimeParser;
        this.gymBookingTools = gymBookingTools;
    }

    /***
     *
     * @param userId 用户ID
     * @param message 用户原始消息
     * @param venueId 当前页面场馆ID
     * @param courtId 当前页面场地ID
     * @return 尝试处理直达路由
     */
    @Override
    public String tryHandle(Long userId, String message, Long venueId, Long courtId) {

        //判断是否有预约意图 没有预约意图，直接返回 null，让请求继续走大模型 Agent。
        if (!hasBookingIntent(message)) {
            return null;
        }

        //判断是否有当前页面上下文,当前直达路由只处理“当前场地预约,必须有venueId和courtId
        if (venueId == null || courtId == null) {
            return null;
        }

        //从用户输入中解析日期和时间段,解析失败就返回 null，让请求继续交给大模型 Agent。
        BookingTimeInfo timeInfo = bookingTimeParser.parse(message);
        if (timeInfo == null) {
            return null;
        }

        //命中当前场地预约直达路由
        return gymBookingTools.createBookingDraftByContext(
                venueId,
                courtId,
                timeInfo.getDate(),
                timeInfo.getStartTime(),
                timeInfo.getEndTime(),
                userId
        );
    }

    /***
     *
     * @param message 用户输入信息
     * @return 判断用户输入是否有预约意图
     */
    private boolean hasBookingIntent(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        String text = message.trim();

        return text.contains("预约")
                || text.contains("预定")
                || text.contains("预订")
                || text.contains("帮我约")
                || text.contains("帮我订")
                || text.contains("我要约")
                || text.contains("我要订");
    }
}
