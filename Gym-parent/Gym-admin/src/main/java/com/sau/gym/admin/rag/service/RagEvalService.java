package com.sau.gym.admin.rag.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.rag.RagEvalCaseDto;
import com.sau.gym.model.dto.rag.RagEvalRunDto;
import com.sau.gym.model.entity.rag.RagEvalCase;
import com.sau.gym.model.entity.rag.RagEvalResult;
import com.sau.gym.model.entity.rag.RagEvalRun;

import java.util.List;

public interface RagEvalService {

    PageInfo<RagEvalCase> pageCases(Integer current, Integer limit, String category, Integer enabled, String keyword);

    void saveCase(RagEvalCaseDto dto);

    void deleteCase(Long id);

    RagEvalRun runEval(RagEvalRunDto dto);

    PageInfo<RagEvalRun> pageRuns(Integer current, Integer limit);

    RagEvalRun getRun(Long runId);

    List<RagEvalResult> getResults(Long runId);
}
