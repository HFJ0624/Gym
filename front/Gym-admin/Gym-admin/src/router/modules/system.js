// 导入组件
const Layout = () => import('@/layout/index.vue')
const role = () => import('@/views/system/sysRole.vue')
const user = () => import('@/views/system/sysUser.vue')
const menu = () => import('@/views/system/sysMenu.vue')
const operaLog = () => import('@/views/system/OperaLog.vue')

// 导出该组件
export default ([
    {
        path: "/system",
        component: Layout,
        name: 'system',
        meta: {
            title: '系统管理',
        },
        icon: 'Setting',
        children: [
            {
                path: '/role',
                name: 'role',
                component: role,
                meta: {
                    title: '角色管理',
                },
                icon: 'Male',
                hidden: false
            },
            {
                path: '/user',
                name: 'user',
                component: user,
                meta: {
                    title: '用户管理',
                },
                icon: 'User',
                hidden: false
            },
            {
                path: '/menu',
                name: 'menu',
                component: menu,
                meta: {
                    title: '菜单管理',
                },
                icon: 'Menu',
                hidden: false
            },
            {
                path: '/operaLog',
                name: 'operaLog',
                component: operaLog,
                meta: {
                    title: '查看日志',
                },
                icon: 'List',
                hidden: false
            }
        ]
    }
])