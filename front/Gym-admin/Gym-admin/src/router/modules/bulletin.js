const Layout = () => import('@/layout/index.vue')
const notice = () => import('@/views/bulletin/notice.vue')
const noticeComment = () => import('@/views/bulletin/noticeComment.vue')
const venueComment = () => import('@/views/bulletin/venueComment.vue')

export default [
    {
        path: '/bulletin',
        component: Layout,
        name: 'bulletin',
        meta: {
            title: '系统公告',
        },
        icon: 'Bell',
        children: [
            {
                path: '/notice',
                name: 'notice',
                component: notice,
                meta: {
                    title: '体育场馆公告',
                },
                icon: 'Share',
                hidden: false
            },
            {
                path: '/noticeComment',
                name: 'noticeComment',
                component: noticeComment,
                meta: {
                    title: '体育场馆公告评论',
                },
                icon: 'Comment',
                hidden: false
            },
            {
                path: '/venueComment',
                name: 'venueComment',
                component: venueComment,
                meta: {
                    title: '体育场馆评论',
                },
                icon: 'Comment',
                hidden: false
            },
        ],
    },
]