package com.sau.gym.admin.rag.service;

//把系统中的业务表数据同步到 knowledge_document。
public interface RagBusinessSyncService {

    /**
     * 同步场馆数据到 RAG 知识库。
     * 逻辑：
     * 1. 查询启用且未删除的场馆
     * 2. 为每个场馆生成一条 knowledge_document
     * 3. 如果该场馆知识已存在，则更新
     * 4. 如果不存在，则新增
     */
    void syncVenueKnowledge();
}
