package com.sau.gym.admin.rag.service;

import com.sau.gym.model.dto.rag.KnowledgeDocumentSaveDto;

public interface RagKnowledgeIngestService {

    /**
     * 保存一条知识文档到 MySQL。
     *
     * @param dto 知识文档参数
     */
    void saveDocument(KnowledgeDocumentSaveDto dto);

    /**
     * 重建所有知识的向量索引。
     *
     * 逻辑：
     * 1. 清空 pgvector 旧向量
     * 2. 查询 MySQL 中所有启用知识
     * 3. 文本切分
     * 4. 生成 embedding
     * 5. 写入 pgvector
     */
    void rebuildAll();
}
