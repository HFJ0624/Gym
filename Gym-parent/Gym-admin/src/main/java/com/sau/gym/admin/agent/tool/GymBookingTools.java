package com.sau.gym.admin.agent.tool;

import com.sau.gym.admin.agent.service.AgentToolGuardService;
import com.sau.gym.admin.agent.store.AgentDraftStore;
import com.sau.gym.admin.agent.store.PendingDraft;
import com.sau.gym.admin.enums.PendingDraftType;
import com.sau.gym.admin.agent.util.AgentToolLogHelper;
import com.sau.gym.admin.enums.AgentRiskLevel;
import com.sau.gym.admin.mapper.CourtBookingMapper;
import com.sau.gym.admin.mapper.CourtMapper;
import com.sau.gym.admin.mapper.VenueMapper;
import com.sau.gym.admin.service.CourtBookingService;
import com.sau.gym.model.dto.venue.BookingDto;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.vo.court.CourtBookVO;
import com.sau.gym.model.vo.court.CourtVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.sau.gym.admin.agent.util.AgentConfirmTokenUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:预约类工具
 * 负责两件事：
 * 1. 生成预约草稿（给大模型调用）
 * 2. 确认预约（给 Java 主流程调用，不给模型直接调用）
 * 日期: 2026/4/23 14:46
 */
@Component
public class GymBookingTools {

    private final VenueMapper venueMapper;
    private final CourtMapper courtMapper;
    private final CourtBookingMapper courtBookingMapper;
    private final CourtBookingService courtBookingService;
    private final AgentDraftStore draftStore;
    private final AgentToolLogHelper agentToolLogHelper;

    private final AgentToolGuardService agentToolGuardService;

    public GymBookingTools(VenueMapper venueMapper,
                           CourtMapper courtMapper,
                           CourtBookingMapper courtBookingMapper,
                           CourtBookingService courtBookingService,
                           AgentDraftStore draftStore,
                           AgentToolLogHelper agentToolLogHelper,
                           AgentToolGuardService agentToolGuardService
                           ) {
        this.venueMapper = venueMapper;
        this.courtMapper = courtMapper;
        this.courtBookingMapper = courtBookingMapper;
        this.courtBookingService = courtBookingService;
        this.draftStore = draftStore;
        this.agentToolLogHelper = agentToolLogHelper;
        this.agentToolGuardService = agentToolGuardService;
    }

    //根据当前页面的场馆ID和场地ID生成预约草稿。
    @Tool("根据当前页面的场馆ID和场地ID生成预约草稿。当用户说“预约这个场地”“帮我预约当前场地”“这个场地明晚7点到8点”时优先使用这个工具。不会真正预约，也不会扣费。")
    public String createBookingDraftByContext(
            @P("当前页面场馆ID，来自页面上下文") Long venueId,
            @P("当前页面场地ID，来自页面上下文") Long courtId,
            @P("预约日期，格式必须是 yyyy-MM-dd，例如 2026-05-08") String date,
            @P("开始时间，格式必须是 HH:mm:ss，例如 19:00:00") String startTime,
            @P("结束时间，格式必须是 HH:mm:ss，例如 20:00:00") String endTime,
            @ToolMemoryId Long userId
    ) {
        //工具调用前风控检查。
        String blocked = agentToolGuardService.checkBeforeToolCall(
                userId,
                "create_booking_draft",
                "生成预约草稿",
                AgentRiskLevel.MEDIUM,
                true,
                false,
                3
        );

        if (blocked != null) {
            return blocked;
        }

        long begin = System.currentTimeMillis();

        //记录工具入参
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("venueId", venueId);
        args.put("courtId", courtId);
        args.put("date", date);
        args.put("startTime", startTime);
        args.put("endTime", endTime);
        args.put("userId", userId);

        String toolName = "createBookingDraftByContext";
        String toolDesc = "根据当前页面的场馆ID和场地ID生成预约草稿";
        String toolClass = this.getClass().getName();
        String methodName = "createBookingDraftByContext";

        try {

            //调用真正的业务逻辑
            String result = doCreateBookingDraftByContext(
                    venueId,
                    courtId,
                    date,
                    startTime,
                    endTime,
                    userId
            );

            //记录工具调用成功日志
            agentToolLogHelper.success(
                    toolName,
                    toolDesc,
                    toolClass,
                    methodName,
                    args,
                    result,
                    System.currentTimeMillis() - begin
            );

            return result;

        } catch (Exception e) {
            //记录工具调用失败日志
            agentToolLogHelper.fail(
                    toolName,
                    toolDesc,
                    toolClass,
                    methodName,
                    args,
                    e,
                    System.currentTimeMillis() - begin
            );
            throw e;
        }
    }


    /***
     *
     * @param venueId 查询当前场馆
     * @param courtId 查询当前场地
     * @param date 校验日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户id
     * @return 根据当前页面上下文生成预约草稿的真实业务逻辑
     */
    private String doCreateBookingDraftByContext(Long venueId,
                                                 Long courtId,
                                                 String date,
                                                 String startTime,
                                                 String endTime,
                                                 Long userId) {

        //参数判空
        if (venueId == null || courtId == null) {
            return "当前页面缺少场馆ID或场地ID，暂时无法根据当前页面生成预约草稿。";
        }

        if (userId == null) {
            return "当前用户信息缺失，请先登录后再预约。";
        }

        if (!StringUtils.hasText(date)
                || !StringUtils.hasText(startTime)
                || !StringUtils.hasText(endTime)) {
            return "预约信息不完整，请提供预约日期、开始时间和结束时间。例如：2026-05-08 19:00:00 到 20:00:00。";
        }

        //校验日期格式
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
        } catch (Exception e) {
            return "预约日期格式错误，请使用 yyyy-MM-dd，例如 2026-05-08。";
        }

        //校验时间格式与先后关系
        LocalTime start;
        LocalTime end;

        try {
            start = LocalTime.parse(startTime);
            end = LocalTime.parse(endTime);
        } catch (Exception e) {
            return "时间格式错误，请使用 HH:mm:ss，例如 19:00:00。";
        }

        if (!start.isBefore(end)) {
            return "预约开始时间必须早于结束时间。";
        }

        //计算预约时长
        long minutes = Duration.between(start, end).toMinutes();

        if (minutes < 60) {
            return "预约时长不能少于1小时。";
        }

        //查询当前场馆
        List<Venue> venueList = venueMapper.findAllVenue();

        if (venueList == null || venueList.isEmpty()) {
            return "系统中暂无可预约场馆。";
        }

        Venue targetVenue = venueList.stream()
                .filter(v -> v != null && venueId.equals(v.getId()))
                .findFirst()
                .orElse(null);

        if (targetVenue == null) {
            return "未找到当前页面对应的场馆，venueId=" + venueId + "。";
        }

        //查询当前场馆下的场地列表
        List<CourtVO> courtList = courtMapper.getAllCourt(targetVenue.getId());

        if (courtList == null || courtList.isEmpty()) {
            return "场馆【" + targetVenue.getVenueName() + "】下暂无可预约场地。";
        }

        //根据courtId精确匹配当前场地
        CourtVO targetCourt = courtList.stream()
                .filter(c -> c != null && courtId.equals(c.getId()))
                .findFirst()
                .orElse(null);

        if (targetCourt == null) {
            return "场馆【" + targetVenue.getVenueName() + "】下未找到当前页面对应的场地，courtId=" + courtId + "。";
        }

        //校验价格
        if (targetCourt.getPrice() == null) {
            return "当前场地暂未配置预约价格，无法生成预约草稿。";
        }

        //查询当前场地在该日期下的已预约时间段，并检查是否冲突。
        List<CourtBookVO> bookedList = courtBookingMapper.selectBookTime(localDate, targetCourt.getId());

        if (bookedList != null && !bookedList.isEmpty()) {
            for (CourtBookVO courtBookVO : bookedList) {
                if (courtBookVO == null
                        || courtBookVO.getStartTime() == null
                        || courtBookVO.getEndTime() == null) {
                    continue;
                }

                //判断两个时间段是否重叠
                boolean isOverlap = start.isBefore(courtBookVO.getEndTime())
                        && end.isAfter(courtBookVO.getStartTime());

                if (isOverlap) {
                    return "预约时间段与已有预约冲突，日期：" + date
                            + "，开始时间：" + startTime
                            + "，结束时间：" + endTime
                            + "。请更换时间段。";
                }
            }
        }

        //计算总价
        BigDecimal totalPrice = targetCourt.getPrice()
                .multiply(BigDecimal.valueOf(minutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        //构造草稿数据
        Map<String, Object> data = new HashMap<>();

        data.put("venueName", targetVenue.getVenueName());
        data.put("venueId", targetVenue.getId());

        data.put("courtName", targetCourt.getName());
        data.put("courtId", targetCourt.getId());
        data.put("courtType", targetCourt.getType());

        data.put("date", date);
        data.put("startTime", startTime);
        data.put("endTime", endTime);

        data.put("hoursPrice", targetCourt.getPrice());
        data.put("totalPrice", totalPrice);

        //生成确认码。
        String confirmToken = AgentConfirmTokenUtil.generateToken();

        //保存预约草稿到Redis
        draftStore.save(userId, new PendingDraft(
                PendingDraftType.BOOKING,
                data,
                LocalDateTime.now(),
                confirmToken
        ));

        //返回草稿信息，让用户确认。
        return "已为您生成预约草稿：\n" +
                "场馆：" + targetVenue.getVenueName() + "\n" +
                "场地：" + targetCourt.getName() + "\n" +
                "类型：" + targetCourt.getType() + "\n" +
                "日期：" + date + "\n" +
                "开始时间：" + startTime + "\n" +
                "结束时间：" + endTime + "\n" +
                "单小时价格：" + targetCourt.getPrice() + "\n" +
                "总价：" + totalPrice + "\n" +
                "确认码：" + confirmToken + "\n" +
                "如果确认，请回复：确认预约 " + confirmToken + "\n" +
                "如果放弃，请回复：取消\n" +
                "注意：该草稿将在15分钟后自动过期。";
    }

    /***
     *
     * @param userId 用户id
     * @param confirmToken 确认码
     * @return 真正确认预约
     */
    public String confirmPendingBooking(Long userId,String confirmToken) {
        PendingDraft draft = draftStore.get(userId);
        if (draft == null || draft.type() != PendingDraftType.BOOKING) {
            return "当前没有待确认的预约草稿。";
        }

        //新增校验确认码
        if (confirmToken == null || confirmToken.trim().isEmpty()) {
            return "请带上确认码，例如：确认预约 " + draft.confirmToken();
        }

        if (!draft.confirmToken().equals(confirmToken.trim())) {
            return "确认码错误，请核对后重新输入。正确格式为：确认预约 " + draft.confirmToken();
        }

        try {
            Map<String, Object> data = draft.data();

            Long courtId = ((Number) data.get("courtId")).longValue();
            String venueName = String.valueOf(data.get("venueName"));
            String courtName = String.valueOf(data.get("courtName"));
            String date = String.valueOf(data.get("date"));
            String startTime = String.valueOf(data.get("startTime"));
            String endTime = String.valueOf(data.get("endTime"));

            // 构建预约类
            BookingDto bookingDto = new BookingDto();
            bookingDto.setCourtId(courtId);
            bookingDto.setUserId(userId);
            bookingDto.setBookingDate(LocalDate.parse(date));
            bookingDto.setStartTime(LocalTime.parse(startTime));
            bookingDto.setEndTime(LocalTime.parse(endTime));
            bookingDto.setHoursPrice((BigDecimal) data.get("hoursPrice"));

            bookingDto.setRemark("LangChain4j预约-" + venueName + "-" + courtName);

            // 调用预约业务
            courtBookingService.saveCourtBook(bookingDto);

            // 成功后清除草稿
            draftStore.clear(userId);

            return "预约成功：\n"
                    + "场馆：" + venueName + "\n"
                    + "场地：" + courtName + "\n"
                    + "日期：" + date + "\n"
                    + "开始时间：" + startTime + "\n"
                    + "结束时间：" + endTime + "\n"
                    + "已调用系统原有预约逻辑完成下单。";
        } catch (Exception e) {
            return "预约失败：" + e.getMessage();
        }
    }
}