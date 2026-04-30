import request from '@/utils/request'

/**
 * 重建 RAG 知识库索引。
 * 作用：
 * 1. 从 MySQL 的 knowledge_document 表读取启用知识
 * 2. 调用 embedding 模型生成向量
 * 3. 写入 PostgreSQL + pgvector
 * 4. 更新 indexed_status = 1
 */
export function rebuildRagKnowledge() {
    return request({
        url: '/admin/rag/rebuild',
        method: 'post'
    })
}

/**
 * RAG 测试问答。
 * 管理员可以用它测试知识库是否能正常召回。
 */
export function testRagAsk(data) {
    return request({
        url: '/front/rag/ask',
        method: 'post',
        data
    })
}