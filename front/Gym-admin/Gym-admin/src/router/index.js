import { createRouter, createWebHashHistory } from 'vue-router'
import redirect from './modules/redirect'
import error from './modules/error'
import login from './modules/login'
import lock from './modules/lock'
import home from './modules/home'
import system from './modules/system'
import register from './modules/register'
import stadium from './modules/stadium'
import booking from './modules/booking'
import bulletin from './modules/bulletin'
import phone from './modules/phone'
import email from './modules/email'
import visit from './modules/visit'

/* 菜单栏的路由 */
// 固定菜单
export const fixedRoutes = [...home]
// 动态菜单
export const asyncRoutes = [...system, ...stadium, ...booking, ...bulletin, ...visit]

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      redirect: '/home',
    },
    ...redirect, // 统一的重定向配置
    ...login,
    ...phone,
    ...email,
    ...register, // 新增展开注册路由模块
    ...lock,
    ...fixedRoutes,
    ...error,
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { left: 0, top: 0 }
    }
  },
})

export default router