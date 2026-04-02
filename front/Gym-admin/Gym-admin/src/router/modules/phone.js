const Phone = () => import('@/views/login/Phone.vue')

export default [
    {
        path: '/phone',
        name: 'Phone',
        component: Phone,
        meta: {
            title: '手机号登录',
            hidden: true // 不显示在侧边栏
        }
    }
]