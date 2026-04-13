const Layout = () => import('@/layout/index.vue')
const chatAdmin = () => import('@/views/chat/AdminChat.vue')

export default [
    {
        path: '/chat',
        component: Layout,
        name: 'chat',
        meta: {
            title: '场馆客服',
        },
        icon: 'Message',
        children: [
            {
                path: '/chatAdmin',
                name: 'chatAdmin',   
                component: chatAdmin,
                meta: {
                    title: '场馆客服',
                },
                icon: 'Message',
                hidden: false
            },
        ],
    },
]