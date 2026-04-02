const Email = () => import('@/views/login/Email.vue')

export default [
    {
        path: '/email',
        name: 'Email',
        component: Email,
        meta: {
            title: '邮箱登录',
            hidden: true // 不显示在侧边栏
        }
    }
]