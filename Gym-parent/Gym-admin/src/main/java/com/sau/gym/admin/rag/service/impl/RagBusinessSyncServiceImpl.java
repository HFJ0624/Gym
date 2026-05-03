package com.sau.gym.admin.rag.service.impl;

import com.sau.gym.admin.enums.KnowledgeScopeEnum;
import com.sau.gym.admin.enums.KnowledgeSourceTypeEnum;
import com.sau.gym.admin.mapper.KnowledgeDocumentMapper;
import com.sau.gym.admin.mapper.RagBusinessSyncMapper;
import com.sau.gym.admin.rag.service.RagBusinessSyncService;
import com.sau.gym.model.entity.rag.KnowledgeDocument;
import com.sau.gym.model.entity.venue.Venue;
import com.sau.gym.model.vo.rag.RagCourtSyncVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/1 9:02
 */
@Service
public class RagBusinessSyncServiceImpl implements RagBusinessSyncService {

    @Autowired
    private RagBusinessSyncMapper ragBusinessSyncMapper;

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    /**
     * 同步场馆数据到 RAG 知识库。
     *
     * 设计原则：
     * 1. 不直接写 pgvector
     * 2. 只写 MySQL 的 knowledge_document
     * 3. 写完后 indexed_status = 0
     * 4. 管理员再点击“重建知识库索引”生成向量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncVenueKnowledge() {
        // 1. 查询所有启用且未删除的场馆
        List<Venue> venueList = ragBusinessSyncMapper.selectEnabledVenues();

        if (venueList == null || venueList.isEmpty()) {
            return;
        }

        // 2. 遍历场馆，逐个生成知识文档
        for (Venue venue : venueList) {
            syncSingleVenue(venue);
        }
    }

    /**
     * 同步单个场馆。
     *
     * @param venue 场馆实体
     */
    private void syncSingleVenue(Venue venue) {
        if (venue == null || venue.getId() == null) {
            return;
        }

        // 1. 生成知识标题
        String title = venue.getVenueName() + "场馆介绍";

        // 2. 生成知识正文
        String content = buildVenueKnowledgeContent(venue);

        // 3. 生成知识文档对象
        KnowledgeDocument document = new KnowledgeDocument();

        document.setTitle(title);
        document.setContent(content);

        // 场馆级知识
        document.setKnowledgeScope(KnowledgeScopeEnum.VENUE.getCode());

        // 来源类型：场馆介绍
        document.setSourceType(KnowledgeSourceTypeEnum.VENUE_INTRO.getCode());

        document.setVenueId(venue.getId());
        document.setVenueName(venue.getVenueName());

        document.setCourtId(null);
        document.setCourtName(null);
        document.setCourtType(null);
        document.setNoticeId(null);

        document.setTopic("场馆介绍");

        // tags 用于增强召回，尽量放用户可能问到的关键词
        document.setTags(buildVenueTags(venue));

        // 自动同步的场馆知识优先级给 8
        document.setPriority(8);

        // 默认启用
        document.setEnabled(1);

        // 4. 判断该场馆介绍知识是否已经存在
        KnowledgeDocument old = knowledgeDocumentMapper.selectByVenueIdAndSourceType(
                venue.getId(),
                KnowledgeSourceTypeEnum.VENUE_INTRO.getCode()
        );

        // 5. 不存在则新增，存在则更新
        if (old == null) {
            document.setCreateTime(new Date());
            document.setUpdateTime(new Date());
            knowledgeDocumentMapper.insert(document);
        } else {
            document.setUpdateTime(new Date());
            knowledgeDocumentMapper.updateByVenueIdAndSourceType(document);
        }
    }

    /**
     * 构造场馆知识正文。
     *
     * 这里的文本非常关键，因为 RAG 最终就是检索这些文本。
     * 文本要尽量自然，包含用户可能问到的信息。
     */
    private String buildVenueKnowledgeContent(Venue venue) {
        StringBuilder builder = new StringBuilder();

        builder.append(venue.getVenueName())
                .append("是平台中的一个");

        if (notEmpty(venue.getVenueType())) {
            builder.append(venue.getVenueType()).append("类型");
        }

        builder.append("体育场馆。");

        if (notEmpty(venue.getLocation())) {
            builder.append("场馆地址位于")
                    .append(venue.getLocation())
                    .append("。");
        }

        if (notEmpty(venue.getPhone())) {
            builder.append("场馆联系电话为")
                    .append(venue.getPhone())
                    .append("。");
        }

        if (venue.getCapacity() != null) {
            builder.append("场馆容量约为")
                    .append(venue.getCapacity())
                    .append("人。");
        }

        if (venue.getOpenTime() != null && venue.getCloseTime() != null) {
            builder.append("场馆开放时间为")
                    .append(venue.getOpenTime())
                    .append("至")
                    .append(venue.getCloseTime())
                    .append("。");
        }

        if (notEmpty(venue.getDescription())) {
            builder.append("场馆说明：")
                    .append(venue.getDescription())
                    .append("。");
        }

        builder.append("用户可以在平台中查看该场馆下的具体场地，")
                .append("例如篮球场、足球场、羽毛球场等，并选择日期、开始时间和结束时间进行预约。")
                .append("提交预约订单后，需要按照系统提示完成支付，支付成功后预约正式生效。");

        return builder.toString();
    }

    /**
     * 构造标签
     * tags 的作用：
     * 1. 增强语义召回
     * 2. 前端展示来源时更清晰
     */
    private String buildVenueTags(Venue venue) {
        StringBuilder builder = new StringBuilder();

        appendTag(builder, venue.getVenueName());
        appendTag(builder, venue.getVenueType());
        appendTag(builder, "场馆");
        appendTag(builder, "预约");
        appendTag(builder, "开放时间");
        appendTag(builder, "地址");

        return builder.toString();
    }

    /**
     * 追加标签。
     */
    private void appendTag(StringBuilder builder, String tag) {
        if (!notEmpty(tag)) {
            return;
        }

        if (!builder.isEmpty()) {
            builder.append(",");
        }

        builder.append(tag);
    }

    /**
     * 判断字符串是否非空。
     */
    private boolean notEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * 同步场地数据到 RAG 知识库。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncCourtKnowledge() {
        // 1. 查询启用且未删除的场地
        List<RagCourtSyncVO> courtList = ragBusinessSyncMapper.selectEnabledCourts();

        if (courtList == null || courtList.isEmpty()) {
            return;
        }

        // 2. 遍历每个场地，生成 RAG 知识
        for (RagCourtSyncVO court : courtList) {
            syncSingleCourt(court);
        }
    }

    /**
     * 同步单个场地。
     * @param court 场地同步数据
     */
    private void syncSingleCourt(RagCourtSyncVO court) {
        if (court == null || court.getCourtId() == null) {
            return;
        }

        // 1. 生成知识文档
        KnowledgeDocument document = new KnowledgeDocument();

        document.setTitle(buildCourtTitle(court));
        document.setContent(buildCourtKnowledgeContent(court));

        // 场地级知识
        document.setKnowledgeScope(KnowledgeScopeEnum.COURT.getCode());

        // 来源类型：场地介绍
        document.setSourceType(KnowledgeSourceTypeEnum.COURT_INTRO.getCode());

        document.setVenueId(court.getVenueId());
        document.setVenueName(court.getVenueName());

        document.setCourtId(court.getCourtId());
        document.setCourtName(court.getCourtName());
        document.setCourtType(court.getType());

        document.setNoticeId(null);
        document.setTopic("场地介绍");
        document.setTags(buildCourtTags(court));

        // 场地知识优先级比场馆知识稍高
        document.setPriority(9);

        // 默认启用
        document.setEnabled(1);

        // 2. 判断该场地知识是否已存在
        KnowledgeDocument old = knowledgeDocumentMapper.selectByCourtIdAndSourceType(
                court.getCourtId(),
                KnowledgeSourceTypeEnum.COURT_INTRO.getCode()
        );

        Date date = new Date();
        // 3. 不存在则新增，存在则更新
        if (old == null) {
            document.setCreateTime(date);
            document.setUpdateTime(date);
            knowledgeDocumentMapper.insert(document);
        } else {
            document.setUpdateTime(date);
            knowledgeDocumentMapper.updateByCourtIdAndSourceType(document);
        }
    }

    /**
     * 构造场地知识标题。
     */
    private String buildCourtTitle(RagCourtSyncVO court) {
        String venueName = court.getVenueName() == null ? "" : court.getVenueName();
        String courtName = court.getCourtName() == null ? "场地" : court.getCourtName();

        if (!venueName.isEmpty()) {
            return venueName + "-" + courtName + "场地介绍";
        }
        return courtName + "场地介绍";
    }

    /**
     * 构造场地知识正文。
     * 这段文本是 RAG 检索的核心内容。
     * 写得越自然，用户越容易通过自然语言问题命中。
     */
    private String buildCourtKnowledgeContent(RagCourtSyncVO court) {
        StringBuilder builder = new StringBuilder();

        if (notEmpty(court.getCourtName())) {
            builder.append(court.getCourtName());
        } else {
            builder.append("该场地");
        }

        if (notEmpty(court.getVenueName())) {
            builder.append("属于").append(court.getVenueName()).append("。");
        } else {
            builder.append("是平台中的一个可预约场地。");
        }

        if (notEmpty(court.getType())) {
            builder.append("场地类型为")
                    .append(court.getType())
                    .append("。");
        }

        if (court.getCapacity() != null) {
            builder.append("该场地容量约为")
                    .append(court.getCapacity())
                    .append("人。");
        }

        if (court.getPrice() != null) {
            builder.append("该场地预约价格为每小时")
                    .append(formatPrice(court.getPrice()))
                    .append("元，最终价格以系统下单页面显示为准。");
        }

        if (court.getOpenTime() != null && court.getCloseTime() != null) {
            builder.append("该场地开放时间为")
                    .append(court.getOpenTime())
                    .append("至")
                    .append(court.getCloseTime())
                    .append("。");
        }

        if (notEmpty(court.getDescription())) {
            builder.append("场地说明：")
                    .append(court.getDescription())
                    .append("。");
        }

        builder.append("用户可以在平台中选择该场地、预约日期、开始时间和结束时间后提交预约订单。")
                .append("提交订单后需要完成支付，支付成功后预约正式生效。")
                .append("用户应按照预约时段入场使用，避免超时占用场地。");

        return builder.toString();
    }

    /**
     * 构造场地标签。
     * tags 的作用：
     * 1. 增强语义召回
     * 2. 方便前端展示来源
     */
    private String buildCourtTags(RagCourtSyncVO court) {
        StringBuilder builder = new StringBuilder();

        appendTag(builder, court.getVenueName());
        appendTag(builder, court.getCourtName());
        appendTag(builder, court.getType());
        appendTag(builder, "场地");
        appendTag(builder, "预约");
        appendTag(builder, "价格");
        appendTag(builder, "开放时间");

        return builder.toString();
    }

    /**
     * 格式化价格。
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "";
        }

        return price.stripTrailingZeros().toPlainString();
    }
}
