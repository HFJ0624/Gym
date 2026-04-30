package com.sau.gym.admin.rag.service;

import com.sau.gym.model.dto.rag.RagAskDto;
import com.sau.gym.model.vo.rag.RagAnswerVO;

public interface RagQaService {

    /**
     * 根据知识库回答用户问题。
     *
     * @param dto 用户问题
     * @return RAG 回答结果
     */
    RagAnswerVO ask(RagAskDto dto);
}
