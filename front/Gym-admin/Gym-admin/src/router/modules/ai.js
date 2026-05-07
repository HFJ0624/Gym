const Layout = () => import('@/layout/index.vue')
const rag = () => import('@/views/ai/rag.vue')
const agentToolLog = () => import('@/views/ai/agentToolLog.vue')

export default [
    {
        path: '/ai',
        component: Layout,
        name: 'ai',
        meta: {
            title: 'AI应用',
        },
        icon: 'Location',
        children: [
            {
                path: '/rag',
                name: 'rag',
                component: rag,
                meta: {
                    title: '场馆知识库问答',
                },
                icon: 'Message',
                hidden: false
            },
            {
                path: '/toolLog',
                name: 'toolLog',
                component: agentToolLog,
                meta: {
                    title: 'agent工具日志',
                },
                icon: 'Message',
                hidden: false
            },
        ],
    },
]