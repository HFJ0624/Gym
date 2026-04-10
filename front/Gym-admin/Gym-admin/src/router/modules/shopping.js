const Layout = () => import('@/layout/index.vue')
const Beverage = () => import('@/views/shopping/Beverage.vue')
const Outfit = () => import('@/views/shopping/Outfit.vue')

export default [
    {
        path: '/shopping',
        component: Layout,
        name: 'shopping',
        meta: {
            title: '购物管理',
        },
        icon: 'Calendar',
        children: [
            {
                path: '/beverage',
                name: 'beverage',
                component: Beverage,
                meta: {
                    title: '饮品管理',
                },
                icon: 'Document',
                hidden: false
            },
            {
                path: '/outfit',
                name: 'outfit',
                component: Outfit,
                meta: {
                    title: '装备管理',
                },
                icon: 'Document',
                hidden: false
            }, 
        ],
    },
]