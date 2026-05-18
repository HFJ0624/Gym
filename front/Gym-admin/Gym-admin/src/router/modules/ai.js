const Layout = () => import('@/layout/index.vue')
const agentToolLog = () => import('@/views/agent/agentToolLog.vue')
const agentTrace = () => import('@/views/agent/agentTrace.vue')

export default [
    {
        path: '/agentLog',
        component: Layout,
        name: 'agentLog',
        meta: {
            title: 'agent调用日志',
        },
        icon: 'Location',
        children: [
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
            {
                path: '/trace',
                name: 'trace',
                component: agentTrace,
                meta: {
                    title: 'agent调用链',
                },
                icon: 'Message',
                hidden: false
            },
        ],
    },
]