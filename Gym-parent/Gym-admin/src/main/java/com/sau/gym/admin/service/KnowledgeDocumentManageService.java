package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.rag.KnowledgeDocumentQueryDto;
import com.sau.gym.model.dto.rag.KnowledgeDocumentSaveDto;
import com.sau.gym.model.dto.rag.KnowledgeDocumentUpdateDto;
import com.sau.gym.model.entity.rag.KnowledgeDocument;

public interface KnowledgeDocumentManageService {

    /**
     * 分页查询知识文档。
     */
    PageInfo<KnowledgeDocument> page(KnowledgeDocumentQueryDto queryDto);

    /**
     * 根据ID查询详情。
     */
    KnowledgeDocument detail(Long id);

    /**
     * 新增知识。
     */
    void save(KnowledgeDocumentSaveDto dto);

    /**
     * 更新知识。
     */
    void update(KnowledgeDocumentUpdateDto dto);

    /**
     * 启用/禁用知识。
     */
    void updateEnabled(Long id, Integer enabled);

    /**
     * 删除知识。
     */
    void delete(Long id);
}
