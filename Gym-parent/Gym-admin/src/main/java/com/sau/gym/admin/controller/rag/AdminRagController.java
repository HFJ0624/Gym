package com.sau.gym.admin.controller.rag;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.rag.service.RagBusinessSyncService;
import com.sau.gym.admin.rag.service.RagKnowledgeIngestService;
import com.sau.gym.admin.service.KnowledgeDocumentManageService;
import com.sau.gym.model.dto.rag.KnowledgeDocumentQueryDto;
import com.sau.gym.model.dto.rag.KnowledgeDocumentSaveDto;
import com.sau.gym.model.dto.rag.KnowledgeDocumentUpdateDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.rag.KnowledgeDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作者:hfj
 * 功能:管理端RAG知识库接口。
 * 日期: 2026/4/28 15:56
 */
@RestController
@RequestMapping("/admin/ai/rag")
public class AdminRagController {

    @Autowired
    private RagKnowledgeIngestService ragKnowledgeIngestService;

    @Autowired
    private KnowledgeDocumentManageService knowledgeDocumentManageService;

    @Autowired
    private RagBusinessSyncService ragBusinessSyncService;

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

    /**
     * 分页查询知识文档。
     */
    @PostMapping("/document/page")
    public Result page(@RequestBody KnowledgeDocumentQueryDto queryDto) {
        PageInfo<KnowledgeDocument> pageInfo = knowledgeDocumentManageService.page(queryDto);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    /**
     * 查询知识详情。
     */
    @GetMapping("/document/{id}")
    public Result detail(@PathVariable Long id) {
        KnowledgeDocument document = knowledgeDocumentManageService.detail(id);
        return Result.build(document, ResultCodeEnum.SUCCESS);
    }

    /**
     * 更新知识文档。
     */
    @PutMapping("/document")
    public Result updateDocument(@RequestBody KnowledgeDocumentUpdateDto dto) {
        knowledgeDocumentManageService.update(dto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 启用/禁用知识。
     */
    @PutMapping("/document/{id}/enabled/{enabled}")
    public Result updateEnabled(@PathVariable Long id,
                                @PathVariable Integer enabled) {
        knowledgeDocumentManageService.updateEnabled(id, enabled);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 删除知识文档。
     */
    @DeleteMapping("/document/{id}")
    public Result deleteDocument(@PathVariable Long id) {
        knowledgeDocumentManageService.delete(id);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 同步场馆业务数据到 RAG 知识库。
     */
    @PostMapping("/sync/venue")
    public Result syncVenueKnowledge() {
        ragBusinessSyncService.syncVenueKnowledge();
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 同步场地业务数据到 RAG 知识库。
     */
    @PostMapping("/sync/court")
    public Result syncCourtKnowledge() {
        ragBusinessSyncService.syncCourtKnowledge();
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

}
