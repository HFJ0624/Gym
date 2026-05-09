const Layout = () => import('@/layout/index.vue')
const venueBook = () => import('@/views/booking/venueBook.vue')
const courtBook = () => import('@/views/booking/courtBook.vue')
const signIn = () => import('@/views/booking/signIn.vue')
const refund = () => import('@/views/booking/refund.vue')

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
            {
                path: '/siginIn',
                name: 'signIn',
                component: signIn,
                meta: {
                    title: '签到查看',
                },
                icon: 'List',
                hidden: false
            }, 
            {
                path: '/refund',
                name: 'refund',
                component: refund,
                meta: {
                    title: '退款申请',
                },
                icon: 'List',
                hidden: false
            }, 
        ],
    },
]