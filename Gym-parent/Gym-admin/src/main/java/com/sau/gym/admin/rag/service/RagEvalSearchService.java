package com.sau.gym.admin.rag.service;

import com.sau.gym.model.entity.rag.RagEvalSearchResult;

public interface RagEvalSearchService {

    /**
     * 执行一次 RAG 问答。
     *
     * @param question 问题
     * @param topK TopK
     * @param minScore 最低相似度
     * @return RAG 评估搜索结果
     */
    RagEvalSearchResult search(String question, Integer topK, Double minScore);
}
