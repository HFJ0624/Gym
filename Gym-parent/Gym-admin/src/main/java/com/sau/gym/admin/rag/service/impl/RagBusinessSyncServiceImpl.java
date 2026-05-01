package com.sau.gym.admin.rag.service.impl;

import com.sau.gym.admin.enums.KnowledgeScopeEnum;
import com.sau.gym.admin.enums.KnowledgeSourceTypeEnum;
import com.sau.gym.admin.mapper.KnowledgeDocumentMapper;
import com.sau.gym.admin.mapper.RagBusinessSyncMapper;
import com.sau.gym.admin.rag.service.RagBusinessSyncService;
import com.sau.gym.model.entity.rag.KnowledgeDocument;
import com.sau.gym.model.entity.venue.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
