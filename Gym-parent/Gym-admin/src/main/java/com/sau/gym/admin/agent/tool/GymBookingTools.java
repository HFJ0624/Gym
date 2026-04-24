package com.sau.gym.admin.agent.tool;

import com.sau.gym.admin.mapper.CourtMapper;
import com.sau.gym.admin.mapper.VenueMapper;
import com.sau.gym.admin.service.CourtBookingService;
import com.sau.gym.admin.agent.store.AgentDraftStore;
import com.sau.gym.admin.agent.store.PendingDraft;
import com.sau.gym.admin.agent.store.PendingDraftType;
import com.sau.gym.model.dto.venue.BookingDto;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.vo.court.CourtVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
    private final CourtBookingService courtBookingService;
    private final AgentDraftStore draftStore;

    public GymBookingTools(VenueMapper venueMapper,
                           CourtMapper courtMapper,
                           CourtBookingService courtBookingService,
                           AgentDraftStore draftStore) {
        this.venueMapper = venueMapper;
        this.courtMapper = courtMapper;
        this.courtBookingService = courtBookingService;
        this.draftStore = draftStore;
    }

    /***
     *
     * @param venueName 场馆名称
     * @param courtName 场地名称
     * @param date 预约日期
     * @param userId 用户id
     * @return 创建预约草稿
     */
    @Tool("根据场馆名、场地名和日期生成预约草稿。不会真正预约，也不会扣费。")
    public String createBookingDraft(
            @P("场馆名称") String venueName,
            @P("场地名称") String courtName,
            @P("预约日期，格式必须是 yyyy-MM-dd") String date,
            @ToolMemoryId Long userId
    ) {
        // 参数判空
        if (!StringUtils.hasText(venueName) || !StringUtils.hasText(courtName) || !StringUtils.hasText(date)) {
            return "预约参数不完整，需要 venueName、courtName、date。";
        }

        // 查询场馆
        List<Venue> venueList = venueMapper.findAllVenue();
        Venue targetVenue = venueList.stream()
                .filter(v -> venueName.equals(v.getVenueName()) || v.getVenueName().contains(venueName))
                .findFirst()
                .orElse(null);

        if (targetVenue == null) {
            return "未找到场馆：" + venueName;
        }

        // 查该场馆下的场地
        List<CourtVO> courtList = courtMapper.getAllCourt(targetVenue.getId());
        if (courtList == null || courtList.isEmpty()) {
            return "该场馆下暂无可预约场地。";
        }

        // 选中目标场地
        CourtVO targetCourt = courtList.stream()
                .filter(c -> c.getName() != null && (c.getName().equals(courtName) || c.getName().contains(courtName)))
                .findFirst()
                .orElse(null);

        if (targetCourt == null) {
            return "场馆【" + targetVenue.getVenueName() + "】下未找到场地【" + courtName + "】。";
        }

        // 构造草稿数据
        Map<String, Object> data = new HashMap<>();
        data.put("venueName", targetVenue.getVenueName());
        data.put("venueId", targetVenue.getId());
        data.put("courtName", targetCourt.getName());
        data.put("courtId", targetCourt.getId());
        data.put("courtType", targetCourt.getType());
        data.put("date", date);
        data.put("price", targetCourt.getPrice());

        // 保存草稿到缓存
        draftStore.save(userId, new PendingDraft(
                PendingDraftType.BOOKING,
                data,
                LocalDateTime.now()
        ));

        // 返回给用户确认
        return "我已生成预约草稿：\n"
                + "场馆：" + targetVenue.getVenueName() + "\n"
                + "场地：" + targetCourt.getName() + "\n"
                + "类型：" + targetCourt.getType() + "\n"
                + "日期：" + date + "\n"
                + "价格：" + targetCourt.getPrice() + "\n"
                + "如果确认，请回复：确认预约\n"
                + "如果放弃，请回复：取消";
    }

    /***
     * 真正确认预约
     * @param userId 用户id
     * @return 执行下单操作
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

            // 构建预约类
            BookingDto bookingDto = new BookingDto();
            bookingDto.setCourtId(courtId);
            bookingDto.setUserId(userId);
            bookingDto.setBookingDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
            bookingDto.setTotalPrice((java.math.BigDecimal) data.get("price"));
            bookingDto.setRemark("LangChain4j预约-" + venueName + "-" + courtName);

            // 调用预约业务
            courtBookingService.saveCourtBook(bookingDto);

            // 成功后清除草稿
            draftStore.clear(userId);

            return "预约成功：\n"
                    + "场馆：" + venueName + "\n"
                    + "场地：" + courtName + "\n"
                    + "日期：" + date + "\n"
                    + "已调用系统原有预约逻辑完成下单。";
        } catch (Exception e) {
            return "预约失败：" + e.getMessage();
        }
    }
}
