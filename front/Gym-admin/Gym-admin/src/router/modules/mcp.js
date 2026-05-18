const Layout = () => import('@/layout/index.vue')
const mcpChat = () => import('@/views/mcp/index.vue')

export default [
    {
        path: '/mcp',
        component: Layout,
        name: 'mcp',
        meta: {
            title: 'MCP Agent聊天',
        },
        icon: 'Location',
        children: [
            {
                path: '/mcpChat',
                name: 'mcpChat',
                component: mcpChat,
                meta: {
                    title: 'MCP Agent问答',
                },
                icon: 'Message',
                hidden: false
            },
        ],
    },
]