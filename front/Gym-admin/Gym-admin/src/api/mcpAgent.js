import request from '@/utils/request'

/**
 * 后台 MCP Agent 聊天
 */
export function chatWithMcpAgent(data) {
    return request({
        url: '/admin/agent/mcp/chat',
        method: 'post',
        data,
        timeout: 60000
    })
}