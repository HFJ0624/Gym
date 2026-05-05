import request from '@/utils/request'

/**
 * 分页查询 Agent 工具调用日志。
 */
export function pageAgentToolLog(data) {
    return request({
        url: '/admin/ai/toolLog/page',
        method: 'post',
        data
    })
}

/**
 * 查询 Agent 工具调用日志详情。
 */
export function getAgentToolLogDetail(id) {
    return request({
        url: `/admin/ai/toolLog/${id}`,
        method: 'get'
    })
}