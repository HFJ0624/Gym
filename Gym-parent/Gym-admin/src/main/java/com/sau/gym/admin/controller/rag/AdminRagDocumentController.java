package com.sau.gym.admin.controller.rag;

import com.sau.gym.admin.rag.service.RagIncrementalIndexService;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.vo.rag.RagIndexResultVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者:hfj
 * 功能:管理端RAG知识文档接口。
 * 日期: 2026/5/6 14:54
 */
@RestController
@RequestMapping("/admin/ai/rag/document")
public class AdminRagDocumentController {

    private final RagIncrementalIndexService ragIncrementalIndexService;

    public AdminRagDocumentController(RagIncrementalIndexService ragIncrementalIndexService) {
        this.ragIncrementalIndexService = ragIncrementalIndexService;
    }

    /**
     * 单条知识重新索引。
     */
    @PostMapping("/{id}/reindex")
    public Result reindexOne(@PathVariable Long id) {
        RagIndexResultVO resultVO = ragIncrementalIndexService.reindexOne(id);
        return Result.build(resultVO, ResultCodeEnum.SUCCESS);
    }
}
