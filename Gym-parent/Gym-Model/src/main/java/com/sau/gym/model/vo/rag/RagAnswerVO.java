package com.sau.gym.model.vo.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 15:50
 */
@Data
@Schema(description = "RAG回答引用来源")
public class RagAnswerVO {

    /**
     * 模型回答
     */
    private String answer;

    /**
     * 引用来源
     */
    private List<RagSourceVO> sources;
}
