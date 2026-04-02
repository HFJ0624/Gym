const Layout = () => import('@/layout/index.vue')
const venueBook = () => import('@/views/booking/venueBook.vue')
const courtBook = () => import('@/views/booking/courtBook.vue')

export default [
    {
        path: '/booking',
        component: Layout,
        name: 'booking',
        meta: {
            title: '预约管理',
        },
        icon: 'Calendar',
        children: [
            {
                path: '/venueBook',
                name: 'venueBook',
                component: venueBook,
                meta: {
                    title: '场馆预约',
                },
                icon: 'PhoneFilled',
                hidden: false
            },
            {
                path: '/courtBook',
                name: 'courtBook',
                component: courtBook,
                meta: {
                    title: '场地预约',
                },
                icon: 'PhoneFilled',
                hidden: false
            }, 
        ],
    },
]