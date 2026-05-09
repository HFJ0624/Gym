package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.rag.RagEvalCase;
import com.sau.gym.model.entity.rag.RagEvalResult;
import com.sau.gym.model.entity.rag.RagEvalRun;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RagEvalMapper {

    /**
     * 新增评估用例。
     */
    int insertCase(RagEvalCase evalCase);

    /**
     * 修改评估用例。
     */
    int updateCase(RagEvalCase evalCase);

    /**
     * 删除评估用例。
     */
    int deleteCase(@Param("id") Long id);

    /**
     * 分页查询评估用例。
     */
    List<RagEvalCase> selectCasePage(@Param("category") String category,
                                     @Param("enabled") Integer enabled,
                                     @Param("keyword") String keyword);

    /**
     * 查询启用的评估用例。
     */
    List<RagEvalCase> selectEnabledCases(@Param("category") String category);

    /**
     * 插入评估批次。
     */
    int insertRun(RagEvalRun run);

    /**
     * 更新评估批次结果。
     */
    int updateRunResult(RagEvalRun run);

    /**
     * 插入评估明细结果。
     */
    int insertResult(RagEvalResult result);

    /**
     * 分页查询评估批次。
     */
    List<RagEvalRun> selectRunPage();

    /**
     * 根据 runId 查询评估批次。
     */
    RagEvalRun selectRunById(@Param("id") Long id);

    /**
     * 查询某次评估的明细。
     */
    List<RagEvalResult> selectResultsByRunId(@Param("runId") Long runId);
}
