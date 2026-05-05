package com.sau.gym.admin.agent.tool;

import com.sau.gym.admin.agent.store.AgentDraftStore;
import com.sau.gym.admin.agent.store.PendingDraft;
import com.sau.gym.admin.agent.store.PendingDraftType;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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

    public GymBookingTools(VenueMapper venueMapper,
                           CourtMapper courtMapper,
                           CourtBookingMapper courtBookingMapper,
                           CourtBookingService courtBookingService,
                           AgentDraftStore draftStore) {
        this.venueMapper = venueMapper;
        this.courtMapper = courtMapper;
        this.courtBookingMapper = courtBookingMapper;
        this.courtBookingService = courtBookingService;
        this.draftStore = draftStore;
    }

    /**
     * 创建预约草稿
     *
     * 现在升级为按时段预约：
     * 必须收集 场馆、场地、日期、开始时间、结束时间
     */
    @Tool("根据场馆名、场地名、预约日期、开始时间、结束时间生成预约草稿。不会真正预约，也不会扣费。")
    public String createBookingDraft(
            @P("场馆名称") String venueName,
            @P("场地名称") String courtName,
            @P("预约日期，格式必须是 yyyy-MM-dd") String date,
            @P("开始时间，格式必须是 HH:mm:ss，例如 19:00:00") String startTime,
            @P("结束时间，格式必须是 HH:mm:ss，例如 20:00:00") String endTime,
            @ToolMemoryId Long userId
    ) {

        // 1. 参数判空：现在必须把开始时间和结束时间也纳入校验
        if (!StringUtils.hasText(venueName)
                || !StringUtils.hasText(courtName)
                || !StringUtils.hasText(date)
                || !StringUtils.hasText(startTime)
                || !StringUtils.hasText(endTime)) {
            return "预约参数不完整，需要 venueName、courtName、date、startTime、endTime。";
        }

        // 2. 时间格式与先后关系校验
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

        // 3. 查询场馆
        List<Venue> venueList = venueMapper.findAllVenue();
        Venue targetVenue = venueList.stream()
                .filter(v -> venueName.equals(v.getVenueName()) || v.getVenueName().contains(venueName))
                .findFirst()
                .orElse(null);

        if (targetVenue == null) {
            return "未找到场馆：" + venueName;
        }

        // 4. 查该场馆下的场地
        List<CourtVO> courtList = courtMapper.getAllCourt(targetVenue.getId());
        if (courtList == null || courtList.isEmpty()) {
            return "该场馆下暂无可预约场地。";
        }

        // 5. 选中目标场地
        CourtVO targetCourt = courtList.stream()
                .filter(c -> c.getName() != null && (c.getName().equals(courtName) || c.getName().contains(courtName)))
                .findFirst()
                .orElse(null);

        if (targetCourt == null) {
            return "场馆【" + targetVenue.getVenueName() + "】下未找到场地【" + courtName + "】。";
        }

        // 6. 计算时长和总价
        // 你当前 court price 如果就是“每小时价格”，那这里应该按小时数计算总价
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 60) {
            return "预约时长必须大于1小时";
        }

        LocalDate localDate = LocalDate.parse(date);
        List<CourtBookVO> list = courtBookingMapper.selectBookTime(localDate, targetCourt.getId());
        // 遍历所有已预约时间段，判断是否重叠
        for (CourtBookVO courtBookVO : list) {
            // 【核心】判断时间段是否重叠
            boolean isOverlap = LocalTime.parse(startTime).isBefore(courtBookVO.getEndTime())
                    && LocalTime.parse(endTime).isAfter(courtBookVO.getStartTime());

            // 重叠=时间冲突
            if (isOverlap) {
                return "预约时间段" + date + "开始:" + startTime +",结束:" + endTime + "与他人冲突";
            }
        }

        BigDecimal totalPrice = targetCourt.getPrice()
                .multiply(BigDecimal.valueOf(minutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        // 7. 构造草稿数据
        Map<String, Object> data = new HashMap<>();
        data.put("venueName", targetVenue.getVenueName());
        data.put("venueId", targetVenue.getId());
        data.put("courtName", targetCourt.getName());
        data.put("courtId", targetCourt.getId());
        data.put("courtType", targetCourt.getType());
        data.put("date", date);
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        data.put("hoursPrice", targetCourt.getPrice()); // 单小时价格
        data.put("totalPrice", totalPrice);             // 本次预约总价

        // 8. 保存草稿到缓存
        draftStore.save(userId, new PendingDraft(
                PendingDraftType.BOOKING,
                data,
                LocalDateTime.now()
        ));

        // 9. 返回给用户确认
        return "我已生成预约草稿：\n"
                + "场馆：" + targetVenue.getVenueName() + "\n"
                + "场地：" + targetCourt.getName() + "\n"
                + "类型：" + targetCourt.getType() + "\n"
                + "日期：" + date + "\n"
                + "开始时间：" + startTime + "\n"
                + "结束时间：" + endTime + "\n"
                + "单小时价格：" + targetCourt.getPrice() + "\n"
                + "总价：" + totalPrice + "\n"
                + "如果确认，请回复：确认预约\n"
                + "如果放弃，请回复：取消";
    }

    /**
     * 真正确认预约
     *
     * 这里要把草稿里的开始时间、结束时间回填到 BookingDto
     */
    public String confirmPendingBooking(Long userId) {
        PendingDraft draft = draftStore.get(userId);
        if (draft == null || draft.type() != PendingDraftType.BOOKING) {
            return "当前没有待确认的预约草稿。";
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