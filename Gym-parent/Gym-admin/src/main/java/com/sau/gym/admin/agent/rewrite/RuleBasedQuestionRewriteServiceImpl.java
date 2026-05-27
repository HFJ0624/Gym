package com.sau.gym.admin.agent.rewrite;

import com.sau.gym.admin.agent.intent.IntentRouteResult;
import com.sau.gym.admin.agent.memory.AgentBusinessContext;
import com.sau.gym.admin.agent.rewrite.service.QuestionRewriteService;
import com.sau.gym.admin.enums.AgentIntent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 作者:hfj
 * 功能:基于规则的问题重写服务
 * 作用:
 * 在不调用大模型的情况下，根据意图识别结果和 Redis 业务上下文，
 * 把用户的模糊表达重写成更完整、更适合工具调用的问题。
 * 日期: 2026/5/26 13:57
 */
@Service
public class RuleBasedQuestionRewriteServiceImpl implements QuestionRewriteService {

    /**
     * 指代词列表。
     *
     * 如果用户问题中出现这些词，通常说明它依赖上下文。
     */
    private static final List<String> DEICTIC_WORDS = Arrays.asList(
            "这个", "那个", "这里", "那里", "刚才", "上面", "前面",
            "它", "他", "她", "该场馆", "该场地", "当前场馆", "当前场地"
    );

    /**
     * 继承上一次信息的表达。
     * 示例:
     * “和刚才一样”
     * “还是那个时间”
     * “照旧”
     */
    private static final List<String> SAME_AS_BEFORE_WORDS = Arrays.asList(
            "一样", "同样", "还是", "照旧", "按刚才", "按上次", "和刚才一样", "和上次一样"
    );

    @Override
    public QuestionRewriteResult rewrite(QuestionRewriteRequest request) {
        /*
         * 1. 基础空值保护。
         *
         * 如果请求对象为空，或者原始问题为空，没法重写，直接返回空问题。
         */
        if (request == null || !StringUtils.hasText(request.getOriginalQuestion())) {
            return QuestionRewriteResult.noChange("");
        }

        String originalQuestion = request.getOriginalQuestion().trim();

        /*
         * 2. 获取意图。
         *
         * 如果意图识别结果为空，就把意图当作 UNSURE。
         * 这样可以避免空指针，也方便后续做兜底重写。
         */
        AgentIntent intent = AgentIntent.UNSURE;
        IntentRouteResult intentRouteResult = request.getIntentRouteResult();
        if (intentRouteResult != null && intentRouteResult.getIntent() != null) {
            intent = intentRouteResult.getIntent();
        }

        /*
         * 3. 根据意图选择不同重写策略。
         *
         * 不同意图需要补充的槽位不一样:
         * 预约类: 重点补充场馆、场地、日期、开始时间、结束时间。
         * RAG 类: 重点补充咨询对象，例如当前场馆/场地。
         * 订单类: 不要乱补场地，只保留用户意图。
         */
        QuestionRewriteResult result;

        switch (intent) {
            case BOOKING_DRAFT:
                result = rewriteBookingDraft(request);
                break;

            case SLOT_QUERY:
                result = rewriteSlotQuery(request);
                break;

            case BOOKING_CANCEL:
                result = rewriteCancelBooking(request);
                break;

            case ORDER_QUERY:
                result = rewriteOrderQuery(request);
                break;

            case RAG_KNOWLEDGE:
                result = rewriteRagKnowledge(request);
                break;

            case VENUE_QUERY:
            case COURT_QUERY:
                result = rewriteVenueOrCourtQuery(request);
                break;

            case SHOPPING_QUERY:
                result = rewriteShoppingQuery(request);
                break;

            case SMALL_TALK:
                result = QuestionRewriteResult.noChange(originalQuestion);
                break;

            case BOOKING_CONFIRM:
                result = QuestionRewriteResult.noChange(originalQuestion);
                break;

            case UNSURE:
            default:
                result = rewriteUnsure(request);
                break;
        }

        /*
         * 4. 补充调试信息。
         *
         * 这些信息会进入 Trace，方便你后续排查:
         * 是意图识别问题，还是问题重写问题，还是工具调用问题。
         */
        result.getDebugInfo().put("intent", intent.name());
        result.getDebugInfo().put("effectiveVenueId", request.getEffectiveVenueId());
        result.getDebugInfo().put("effectiveCourtId", request.getEffectiveCourtId());

        AgentBusinessContext context = request.getBusinessContext();
        if (context != null) {
            result.getDebugInfo().put("lastVenueId", context.getLastVenueId());
            result.getDebugInfo().put("lastVenueName", context.getLastVenueName());
            result.getDebugInfo().put("lastCourtId", context.getLastCourtId());
            result.getDebugInfo().put("lastCourtName", context.getLastCourtName());
            result.getDebugInfo().put("lastCourtType", context.getLastCourtType());
            result.getDebugInfo().put("lastBookingDate", context.getLastBookingDate());
            result.getDebugInfo().put("lastStartTime", context.getLastStartTime());
            result.getDebugInfo().put("lastEndTime", context.getLastEndTime());
            result.getDebugInfo().put("lastIntent", context.getLastIntent());
        }

        return result;
    }

    /**
     * 重写预约草稿类问题。
     *
     * 示例:
     * 原始问题: “那就预约这个”
     *
     * 重写后:
     * “用户想生成预约草稿；当前场馆ID=1；当前场地ID=2；
     * 预约日期=2026-05-27；开始时间=19:00:00；结束时间=20:00:00；
     * 原始问题=那就预约这个。”
     */
    private QuestionRewriteResult rewriteBookingDraft(QuestionRewriteRequest request) {
        String original = request.getOriginalQuestion();
        AgentBusinessContext context = request.getBusinessContext();

        List<String> filledSlots = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        builder.append("用户想生成预约草稿");

        appendVenueAndCourt(builder, filledSlots, request, context);
        appendBookingTime(builder, filledSlots, context);

        builder.append("；原始问题=").append(original).append("。");

        return QuestionRewriteResult.changed(
                original,
                builder.toString(),
                "预约草稿类问题需要补充场馆、场地、日期和时间段，避免大模型编造预约信息",
                filledSlots
        );
    }

    /**
     * 重写查询可预约时段类问题。
     *
     * 示例:
     * 原始问题: “明晚七点还有吗”
     *
     * 重写后:
     * “用户想查询可预约时段；当前场馆ID=1；当前场地ID=2；
     * 预约日期=2026-05-27；开始时间=19:00:00；
     * 原始问题=明晚七点还有吗。”
     */
    private QuestionRewriteResult rewriteSlotQuery(QuestionRewriteRequest request) {
        String original = request.getOriginalQuestion();
        AgentBusinessContext context = request.getBusinessContext();

        List<String> filledSlots = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        builder.append("用户想查询可预约时段");

        appendVenueAndCourt(builder, filledSlots, request, context);
        appendBookingTime(builder, filledSlots, context);

        builder.append("；原始问题=").append(original).append("。");

        return QuestionRewriteResult.changed(
                original,
                builder.toString(),
                "查询可预约时段需要结合当前场馆、场地和时间上下文",
                filledSlots
        );
    }

    /**
     * 重写取消预约类问题。
     *
     * 注意:
     * 取消预约是敏感操作。
     * 这里只做语义补全，不直接执行取消。
     * 真正取消仍然必须走取消预约工具，并且需要用户确认。
     */
    private QuestionRewriteResult rewriteCancelBooking(QuestionRewriteRequest request) {
        String original = request.getOriginalQuestion();
        AgentBusinessContext context = request.getBusinessContext();

        List<String> filledSlots = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        builder.append("用户想取消预约");

        appendVenueAndCourt(builder, filledSlots, request, context);
        appendBookingTime(builder, filledSlots, context);

        builder.append("；原始问题=").append(original).append("。");
        builder.append("注意: 取消预约属于敏感操作，必须先生成取消草稿或让用户确认，不能直接取消。");

        return QuestionRewriteResult.changed(
                original,
                builder.toString(),
                "取消预约类问题需要补充上下文，但不能直接执行取消操作",
                filledSlots
        );
    }

    /**
     * 重写查询订单/预约记录类问题。
     *
     * 示例:
     * “我的预约呢”
     *
     * 这类问题主要依赖 userId，不应该强行补场馆/场地，
     * 否则可能把“查询所有预约”误变成“查询某个场地的预约”。
     */
    private QuestionRewriteResult rewriteOrderQuery(QuestionRewriteRequest request) {
        String original = request.getOriginalQuestion();

        String rewritten = "用户想查询自己的预约记录或订单信息；原始问题=" + original + "。";

        return QuestionRewriteResult.changed(
                original,
                rewritten,
                "订单查询类问题主要依赖用户身份，不强制补充场馆或场地上下文",
                new ArrayList<>()
        );
    }

    /**
     * 重写 RAG 知识库问答类问题。
     *
     * 示例:
     * 原始问题: “这个场地可以退款吗”
     *
     * 重写后:
     * “用户想咨询体育场馆预约平台知识库；
     * 当前场馆ID=1；当前场地ID=2；
     * 原始问题=这个场地可以退款吗。”
     *
     * 这样 RAG 工具可以带着 venueId/courtId 做范围过滤。
     */
    private QuestionRewriteResult rewriteRagKnowledge(QuestionRewriteRequest request) {
        String original = request.getOriginalQuestion();
        AgentBusinessContext context = request.getBusinessContext();

        List<String> filledSlots = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        builder.append("用户想咨询体育场馆预约平台知识库");

        appendVenueAndCourt(builder, filledSlots, request, context);

        builder.append("；原始问题=").append(original).append("。");

        return QuestionRewriteResult.changed(
                original,
                builder.toString(),
                "知识库问答类问题需要保留原始问题，并在涉及当前场馆/场地时补充上下文",
                filledSlots
        );
    }

    /**
     * 重写场馆或场地查询类问题。
     * 示例:
     * “这个场馆有哪些场地”
     * 如果问题里出现“这个场馆”，就补充当前 venueId。
     */
    private QuestionRewriteResult rewriteVenueOrCourtQuery(QuestionRewriteRequest request) {
        String original = request.getOriginalQuestion();
        AgentBusinessContext context = request.getBusinessContext();

        List<String> filledSlots = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        builder.append("用户想查询场馆或场地信息");

        appendVenueAndCourt(builder, filledSlots, request, context);

        builder.append("；原始问题=").append(original).append("。");

        if (filledSlots.isEmpty() && !containsDeicticWord(original)) {
            return QuestionRewriteResult.noChange(original);
        }

        return QuestionRewriteResult.changed(
                original,
                builder.toString(),
                "场馆/场地查询中包含上下文指代，需要补充当前场馆或场地信息",
                filledSlots
        );
    }

    /**
     * 重写商城类问题。
     *
     * 当前只做轻量重写。
     * 后续如果商城工具需要商品类型、数量、价格区间，可以再扩展槽位。
     */
    private QuestionRewriteResult rewriteShoppingQuery(QuestionRewriteRequest request) {
        String original = request.getOriginalQuestion();

        String rewritten = "用户想咨询或操作商城相关功能；原始问题=" + original + "。";

        return QuestionRewriteResult.changed(
                original,
                rewritten,
                "商城问题统一标记为购物相关，后续由商城工具继续解析商品和数量",
                new ArrayList<>()
        );
    }

    /**
     * 重写不确定意图。
     *
     * 逻辑:
     * 如果原始问题包含“这个、那个、刚才、一样”等上下文指代词，
     * 并且 Redis 里有业务上下文，则把上下文补进去。
     *
     * 如果没有上下文，也没有明确指代，就不重写。
     */
    private QuestionRewriteResult rewriteUnsure(QuestionRewriteRequest request) {
        String original = request.getOriginalQuestion();
        AgentBusinessContext context = request.getBusinessContext();

        boolean hasDeictic = containsDeicticWord(original);
        boolean sameAsBefore = containsSameAsBeforeWord(original);

        if (!hasDeictic && !sameAsBefore) {
            return QuestionRewriteResult.noChange(original);
        }

        List<String> filledSlots = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        builder.append("用户问题存在上下文指代，需要结合最近业务上下文理解");

        appendVenueAndCourt(builder, filledSlots, request, context);
        appendBookingTime(builder, filledSlots, context);

        builder.append("；原始问题=").append(original).append("。");

        if (filledSlots.isEmpty()) {
            return QuestionRewriteResult.noChange(original);
        }

        return QuestionRewriteResult.changed(
                original,
                builder.toString(),
                "检测到上下文指代词，补充最近业务上下文",
                filledSlots
        );
    }

    /**
     * 追加场馆和场地信息。
     *
     * 注意:
     * 这里只补充已经存在的上下文，不做数据库查询，也不编造名称。
     */
    private void appendVenueAndCourt(
            StringBuilder builder,
            List<String> filledSlots,
            QuestionRewriteRequest request,
            AgentBusinessContext context
    ) {
        Long venueId = request.getEffectiveVenueId();
        Long courtId = request.getEffectiveCourtId();

        if (venueId != null) {
            builder.append("；当前场馆ID=").append(venueId);
            filledSlots.add("venueId");
        }

        if (context != null && StringUtils.hasText(context.getLastVenueName())) {
            builder.append("；当前场馆名称=").append(context.getLastVenueName());
            filledSlots.add("venueName");
        }

        if (courtId != null) {
            builder.append("；当前场地ID=").append(courtId);
            filledSlots.add("courtId");
        }

        if (context != null && StringUtils.hasText(context.getLastCourtName())) {
            builder.append("；当前场地名称=").append(context.getLastCourtName());
            filledSlots.add("courtName");
        }

        if (context != null && StringUtils.hasText(context.getLastCourtType())) {
            builder.append("；当前场地类型=").append(context.getLastCourtType());
            filledSlots.add("courtType");
        }
    }

    /**
     * 追加预约日期和时间段。
     *
     * 注意:
     * 这些时间来自 BookingTimeParser 或历史上下文。
     * 如果上下文没有，就不补充，避免编造。
     */
    private void appendBookingTime(
            StringBuilder builder,
            List<String> filledSlots,
            AgentBusinessContext context
    ) {
        if (context == null) {
            return;
        }

        if (StringUtils.hasText(context.getLastBookingDate())) {
            builder.append("；预约日期=").append(context.getLastBookingDate());
            filledSlots.add("bookingDate");
        }

        if (StringUtils.hasText(context.getLastStartTime())) {
            builder.append("；开始时间=").append(context.getLastStartTime());
            filledSlots.add("startTime");
        }

        if (StringUtils.hasText(context.getLastEndTime())) {
            builder.append("；结束时间=").append(context.getLastEndTime());
            filledSlots.add("endTime");
        }
    }

    /**
     * 判断是否包含指代词。
     *
     * 示例:
     * “这个场地”
     * “刚才那个”
     * “这里可以停车吗”
     */
    private boolean containsDeicticWord(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }

        for (String word : DEICTIC_WORDS) {
            if (text.contains(word)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否包含“沿用上一次信息”的表达。
     *
     * 示例:
     * “和刚才一样”
     * “还是那个时间”
     * “照旧”
     */
    private boolean containsSameAsBeforeWord(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }

        for (String word : SAME_AS_BEFORE_WORDS) {
            if (text.contains(word)) {
                return true;
            }
        }

        return false;
    }
}
