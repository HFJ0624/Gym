package com.sau.gym.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.KnowledgeDocumentMapper;
import com.sau.gym.admin.service.KnowledgeDocumentManageService;
import com.sau.gym.model.dto.rag.KnowledgeDocumentQueryDto;
import com.sau.gym.model.dto.rag.KnowledgeDocumentSaveDto;
import com.sau.gym.model.dto.rag.KnowledgeDocumentUpdateDto;
import com.sau.gym.model.entity.rag.KnowledgeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 作者:hfj
 * 功能:RAG知识文档管理服务实现。
 * 日期: 2026/4/30 22:30
 */
@Service
public class KnowledgeDocumentManageServiceImpl implements KnowledgeDocumentManageService {

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    /**
     * 分页查询知识文档。
     */
    @Override
    public PageInfo<KnowledgeDocument> page(KnowledgeDocumentQueryDto queryDto) {
        if (queryDto == null) {
            queryDto = new KnowledgeDocumentQueryDto();
        }

        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());

        List<KnowledgeDocument> list = knowledgeDocumentMapper.selectPage(queryDto);

        return new PageInfo<>(list);
    }

    /**
     * 查询知识详情。
     */
    @Override
    public KnowledgeDocument detail(Long id) {
        if (id == null) {
            throw new RuntimeException("知识ID不能为空");
        }

        KnowledgeDocument document = knowledgeDocumentMapper.selectById(id);

        if (document == null) {
            throw new RuntimeException("知识文档不存在");
        }

        return document;
    }

    /**
     * 新增知识。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(KnowledgeDocumentSaveDto dto) {
        if (dto == null) {
            throw new RuntimeException("知识参数不能为空");
        }

        validateSaveParam(dto.getTitle(), dto.getContent(), dto.getKnowledgeScope(), dto.getSourceType());

        KnowledgeDocument document = new KnowledgeDocument();

        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setKnowledgeScope(dto.getKnowledgeScope());
        document.setSourceType(dto.getSourceType());

        document.setVenueId(dto.getVenueId());
        document.setVenueName(dto.getVenueName());
        document.setCourtId(dto.getCourtId());
        document.setCourtName(dto.getCourtName());
        document.setCourtType(dto.getCourtType());
        document.setNoticeId(dto.getNoticeId());

        document.setTopic(dto.getTopic());
        document.setTags(dto.getTags());

        document.setPriority(dto.getPriority() == null ? 0 : dto.getPriority());

        knowledgeDocumentMapper.insert(document);
    }

    /**
     * 更新知识。
     *
     * 更新后 indexed_status 会被重置为 0。
     * 因为知识内容变了，旧向量就不可信了。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(KnowledgeDocumentUpdateDto dto) {
        if (dto == null || dto.getId() == null) {
            throw new RuntimeException("知识ID不能为空");
        }

        validateSaveParam(dto.getTitle(), dto.getContent(), dto.getKnowledgeScope(), dto.getSourceType());

        KnowledgeDocument old = knowledgeDocumentMapper.selectById(dto.getId());

        if (old == null) {
            throw new RuntimeException("知识文档不存在");
        }

        KnowledgeDocument document = new KnowledgeDocument();

        document.setId(dto.getId());
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setKnowledgeScope(dto.getKnowledgeScope());
        document.setSourceType(dto.getSourceType());

        document.setVenueId(dto.getVenueId());
        document.setVenueName(dto.getVenueName());
        document.setCourtId(dto.getCourtId());
        document.setCourtName(dto.getCourtName());
        document.setCourtType(dto.getCourtType());
        document.setNoticeId(dto.getNoticeId());

        document.setTopic(dto.getTopic());
        document.setTags(dto.getTags());

        document.setPriority(dto.getPriority() == null ? 0 : dto.getPriority());
        document.setEnabled(dto.getEnabled() == null ? old.getEnabled() : dto.getEnabled());

        knowledgeDocumentMapper.updateById(document);
    }

    /**
     * 启用/禁用知识。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnabled(Long id, Integer enabled) {
        if (id == null) {
            throw new RuntimeException("知识ID不能为空");
        }

        if (enabled == null || (enabled != 0 && enabled != 1)) {
            throw new RuntimeException("启用状态不合法");
        }

        Date date = new Date();
        knowledgeDocumentMapper.updateEnabled(id, enabled,date);
    }

    /**
     * 删除知识。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            throw new RuntimeException("知识ID不能为空");
        }

        knowledgeDocumentMapper.deleteById(id);
    }

    /**
     * 校验保存/更新参数。
     */
    private void validateSaveParam(String title,
                                   String content,
                                   Integer knowledgeScope,
                                   Integer sourceType) {
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("知识标题不能为空");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("知识正文不能为空");
        }

        if (knowledgeScope == null) {
            throw new RuntimeException("知识范围不能为空");
        }

        if (sourceType == null) {
            throw new RuntimeException("知识来源类型不能为空");
        }
    }
}
