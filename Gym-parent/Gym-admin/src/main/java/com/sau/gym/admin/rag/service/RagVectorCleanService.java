package com.sau.gym.admin.rag.service;

public interface RagVectorCleanService {

    /**
     * 根据知识文档ID删除 pgvector 中的旧向量。
     *
     * @param docId 知识文档ID
     * @return 删除行数
     */
    int deleteByDocId(Long docId);
}
