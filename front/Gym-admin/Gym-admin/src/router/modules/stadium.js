const Layout = () => import('@/layout/index.vue')
const venue = () => import('@/views/stadium/venue.vue')
const court = () => import('@/views/stadium/court.vue')
const equipment = () => import('@/views/stadium/equipment.vue')


export default [
    {
        path: '/stadium',
        component: Layout,
        name: 'stadium',
        meta: {
            title: '场馆管理',
        },
        icon: 'OfficeBuilding',
        children: [
            {
                path: '/venue',
                name: 'venue',
                component: venue,
                meta: {
                    title: '场馆管理',
                },
                icon: 'Document',
                hidden: false
            },
            {
                path: '/court',
                name: 'court',
                component: court,
                meta: {
                    title: '场地管理',
                },
                icon: 'Memo',
                hidden: false
            },
            {
                path: '/equipment',
                name: 'equipment',
                component: equipment,
                meta: {
                    title: '器械管理',
                },
                icon: 'Lollipop',
                hidden: false
            }, 
        ],
    },
]