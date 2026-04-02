const Register = () => import('@/views/login/Register.vue')

export default [
    {
        path: '/register',
        name: 'Register',
        component: Register,
        meta: {
            title: '注册',
            hidden: true // 不显示在侧边栏
        }
    }
]