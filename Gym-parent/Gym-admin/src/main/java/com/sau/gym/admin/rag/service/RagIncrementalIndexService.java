package com.sau.gym.admin.rag.service;

import com.sau.gym.model.vo.rag.RagIndexResultVO;

public interface RagIncrementalIndexService {

    /**
     * 单条知识重新索引。
     *
     * @param docId 知识文档ID
     * @return 索引结果
     */
    RagIndexResultVO reindexOne(Long docId);
}
