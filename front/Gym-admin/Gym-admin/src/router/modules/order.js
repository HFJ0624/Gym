const Layout = () => import('@/layout/index.vue')
const order = () => import('@/views/order/order.vue')

export default [
    {
        path: '/order',
        component: Layout,
        name: 'order',
        meta: {
            title: '订单管理',
        },
        icon: 'Bell',
        children: [
            {
                path: '/orderInfo',
                name: 'orderInfo',
                component: order,
                meta: {
                    title: '订单详情',
                },
                icon: 'Message',
                hidden: false
            },
        ],
    },
]