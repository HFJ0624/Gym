package com.sau.gym.admin.controller.rag;

import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.rag.service.RagEvalService;
import com.sau.gym.model.dto.rag.RagEvalCaseDto;
import com.sau.gym.model.dto.rag.RagEvalRunDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.rag.RagEvalCase;
import com.sau.gym.model.entity.rag.RagEvalResult;
import com.sau.gym.model.entity.rag.RagEvalRun;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/9 19:46
 */
@RestController
@RequestMapping("/admin/ai/rag/eval")
public class AdminRagEvalController {

    private final RagEvalService ragEvalService;

    public AdminRagEvalController(RagEvalService ragEvalService) {
        this.ragEvalService = ragEvalService;
    }

    /**
     * 分页查询评估用例。
     */
    @GetMapping("/case/page")
    public Result casePage(@RequestParam(defaultValue = "1") Integer current,
                           @RequestParam(defaultValue = "10") Integer limit,
                           @RequestParam(required = false) String category,
                           @RequestParam(required = false) Integer enabled,
                           @RequestParam(required = false) String keyword) {

        PageInfo<RagEvalCase> pageInfo = ragEvalService.pageCases(current, limit, category, enabled, keyword);

        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    /**
     * 新增或修改评估用例。
     */
    @PostMapping("/case/save")
    public Result saveCase(@RequestBody RagEvalCaseDto dto) {
        ragEvalService.saveCase(dto);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 删除评估用例。
     */
    @DeleteMapping("/case/{id}")
    public Result deleteCase(@PathVariable Long id) {
        ragEvalService.deleteCase(id);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 运行一次 RAG 评估。
     */
    @PostMapping("/run")
    public Result run(@RequestBody RagEvalRunDto dto) {
        RagEvalRun run = ragEvalService.runEval(dto);
        return Result.build(run, ResultCodeEnum.SUCCESS);
    }

    /**
     * 查询评估批次列表。
     */
    @GetMapping("/run/page")
    public Result runPage(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer limit) {

        PageInfo<RagEvalRun> pageInfo = ragEvalService.pageRuns(current, limit);

        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    /**
     * 查询某次评估详情。
     */
    @GetMapping("/run/detail/{runId}")
    public Result runDetail(@PathVariable Long runId) {
        RagEvalRun run = ragEvalService.getRun(runId);
        List<RagEvalResult> results = ragEvalService.getResults(runId);

        Map<String, Object> data = new HashMap<>();
        data.put("run", run);
        data.put("results", results);

        return Result.build(data, ResultCodeEnum.SUCCESS);
    }
}
