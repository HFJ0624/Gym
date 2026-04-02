const Layout = () => import('@/layout/index.vue')
const collectVenue = () => import('@/views/visit/CollectVenue.vue')
const visitVenue = () => import('@/views/visit/VisitVenue.vue')

export default [
    {
        path: '/visit',
        component: Layout,
        name: 'visit',
        meta: {
            title: '收藏和访问',
        },
        icon: 'OfficeBuilding',
        children: [
            {
                path: '/collectVenue',
                name: 'collectVenue',
                component: collectVenue,
                meta: {
                    title: '场馆收藏',
                },
                icon: 'Document',
                hidden: false
            },
            {
                path: '/visitVenue',
                name: 'visitVenue',
                component: visitVenue,
                meta: {
                    title: '场馆访问量',
                },
                icon: 'Document',
                hidden: false
            },
        ],
    },
]