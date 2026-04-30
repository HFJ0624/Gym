import request from '@/utils/frontRequest'

/**
 * RAG 知识库问答接口
 * 使用场景：
 * 1. 用户在普通问答页提问：只传 question
 * 2. 用户在场馆详情页提问：传 question + venueId
 * 3. 用户在场地详情页提问：传 question + venueId + courtId
 */
export function askRag(data) {
    return request({
        url: '/front/rag/ask',
        method: 'post',
        data
    })
}