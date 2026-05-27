package com.sau.gym.admin.agent.rewrite.service;

import com.sau.gym.admin.agent.rewrite.QuestionRewriteRequest;
import com.sau.gym.admin.agent.rewrite.QuestionRewriteResult;

public interface QuestionRewriteService {

    /**
     * 根据用户原始问题、意图识别结果和业务上下文，生成重写问题。
     *
     * @param request 问题重写请求
     * @return 问题重写结果
     */
    QuestionRewriteResult rewrite(QuestionRewriteRequest request);
}
