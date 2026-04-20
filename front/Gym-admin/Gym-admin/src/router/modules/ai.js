const Layout = () => import('@/layout/index.vue')
const agent = () => import('@/views/ai/ai.vue')

export default [
    {
        path: '/ai',
        component: Layout,
        name: 'ai',
        meta: {
            title: 'AI智能体',
        },
        icon: 'Location',
        children: [
            {
                path: '/agent',
                name: 'agent',   
                component: agent,
                meta: {
                    title: 'agent',
                },
                icon: 'Message',
                hidden: false
            },
        ],
    },
]