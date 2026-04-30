package com.sau.gym.admin.controller.ai;

import com.sau.gym.admin.rag.service.RagKnowledgeIngestService;
import com.sau.gym.model.dto.rag.KnowledgeDocumentSaveDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:管理端RAG知识库接口。
 * 日期: 2026/4/28 15:56
 */
@RestController
@RequestMapping("/admin/rag")
public class RagController {

    @Autowired
    private RagKnowledgeIngestService ragKnowledgeIngestService;

    /***
     *
     * @param dto 保存知识文档请求参数
     * @return 新增知识文档。
     */
    @PostMapping("/document")
    public Result saveDocument(@RequestBody KnowledgeDocumentSaveDto dto) {
        ragKnowledgeIngestService.saveDocument(dto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 重建全部知识向量索引。
     * 注意：
     * 第一版是全量重建。
     */
    @PostMapping("/rebuild")
    public Result rebuildAll() {
        ragKnowledgeIngestService.rebuildAll();
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }
}
