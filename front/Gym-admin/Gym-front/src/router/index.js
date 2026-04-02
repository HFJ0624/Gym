import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from '@/stores/auth'

const FrontLayout = () => import('@/views/layout/FrontLayout.vue')

const router = createRouter({
  // 前台部署在 /front/ 子路径下
  history: createWebHistory('/front'),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/index.vue'),
      meta: { public: true, title: '前台登录' },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/register/index.vue'),
      meta: { public: true, title: '前台注册' },
    },
    {
      path: '/',
      component: FrontLayout,
      redirect: '/index',
      children: [
        {
          path: 'index',
          name: 'index',
          component: () => import('@/views/home/index.vue'),
          meta: { title: '首页' },
        },
        {
          path: 'venues',
          name: 'venues',
          component: () => import('@/views/venue/list.vue'),
          meta: { title: '场馆列表' },
        },
        {
          path: 'venues/court/:id',
          name: 'court',
          component: () => import('@/views/venue/court.vue'),
          meta: { title: '场地详情' },
        },
        {
          path: 'venues/details/:id',
          name: 'details',
          component: () => import('@/views/venue/details.vue'),
          meta: { title: '预约场地' },
        },
        {
          path: 'order',
          name: 'order',
          component: () => import('@/views/order/list.vue'),
          meta: { title: '我的预约' },
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/profile/index.vue'),
          meta: { title: '个人中心' },
        },
        {
          path: 'notice',
          name: 'notice',
          component: () => import('@/views/notice/index.vue'),
          meta: { title: '公告' },
        },
        {
          path: 'notice/:id',
          name: 'noticeDetail',
          component: () => import('@/views/notice/detail.vue'),
          meta: { title: '公告详情' },
        },
        {
          path: 'venueComment',
          name: 'venueComment',
          component: () => import('@/views/venue/comment.vue'),
          meta: { title: '场馆评论' },
        },
      ],
    },
  ],
  scrollBehavior() {
    return { left: 0, top: 0 }
  },
})

router.beforeEach(to => {
  if (to.meta && to.meta.title) document.title = `${to.meta.title} - 体育场馆预约`

  // public 页面不需要登录（只有 /login）
  if (to.meta && to.meta.public) return true

  // 其余页面需要前台 token
  const auth = useAuth()
  if (auth.token) return true

  return {
    name: 'login',
    query: { redirect: to.fullPath },
    replace: true,
  }
})

export default router

