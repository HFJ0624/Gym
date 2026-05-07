<template>
  <div class="layout">
    <header class="header">
      <div class="inner">
        <div class="logo" @click="go('/index')">智能体育场馆预约</div>
        <nav class="nav">
          <router-link class="item" to="/index" :class="{ active: isActive('/index') }">首页</router-link>
          <router-link class="item" to="/venues" :class="{ active: isActive('/venues') }">场馆</router-link>
          <router-link class="item" to="/shopping" :class="{ active: isActive('/shopping') }">商城</router-link>
          <router-link class="item" to="/venueComment" :class="{ active: isActive('/venueComment') }">场馆评论</router-link>
          <router-link class="item" to="/notice" :class="{ active: isActive('/notice') }">公告</router-link>
          <router-link class="item" to="/order" :class="{ active: isActive('/order') }">我的预约</router-link>
          <router-link class="item" to="/user/chat" :class="{ active: isActive('/user/chat') }">在线客服</router-link>
          <router-link class="item" to="/rag" :class="{ active: isActive('/rag') }">场馆知识库问答</router-link>
          <router-link class="item" to="/profile" :class="{ active: isActive('/profile') }">个人中心</router-link>
        </nav>
        <div class="right">
          <div class="cart-btn" @click="go('/shopping/cart')">
            <el-icon><ShoppingCart /></el-icon>
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount > 99 ? '99+' : cartCount }}</span>
          </div>
          <div class="notification-btn" @click="go('/notification')">
            <el-icon><Bell /></el-icon>
            <span v-if="unreadCount > 0" class="notification-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
          </div>
          <el-dropdown>
            <span class="user">
              <el-avatar size="small" :src="avatar" />
              <span class="name">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-item @click="go('/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item @click="go('/notification')">我的消息</el-dropdown-item>
              <el-dropdown-item @click="go('/order')">我的预约</el-dropdown-item>
              <el-dropdown-item @click="go('/shopping/order')">我的订单</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <div class="copyright">© 2026 智能体育场馆预约系统 版权所有</div>
      <div class="info-row">
        <span>客服热线:400-123-4567</span>
        <span>联系邮箱:342586916@qq.com</span>
        <span>工作时间:周一至周五 09:00-18:00</span> 
        <span>场馆地址:泉州市惠安县体育中心综合楼</span>
        <span>ICP备案:粤ICP备12345678号</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '@/stores/auth'
import { useCart } from '@/stores/cart'
import { getUnreadNotificationCount } from '@/api/notification'

const route = useRoute()
const router = useRouter()
const auth = useAuth()
const cartStore = useCart()
const username = computed(() => auth.user?.username || '用户')
const avatar = computed(() => auth.user?.avatar)
const cartCount = computed(() => cartStore.totalQuantity)
const unreadCount = ref(0)

const go = path => router.push(path)

const isActive = path => {
  if (path === '/venues') return route.path.startsWith('/venues')
  if (path === '/shopping') return route.path.startsWith('/shopping')
  return route.path === path
}

const logout = () => {
  auth.logout()
  router.replace({ name: 'login', query: { redirect: route.fullPath } })
}

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadNotificationCount()
    unreadCount.value = res.data || 0
  } catch (e) {
    console.error('加载未读数量失败', e)
  }
}

onMounted(() => {
  cartStore.loadCart()
  loadUnreadCount()
})
</script>

<style scoped lang="scss">
.layout {
  min-height: 100%;
  display: flex;
  flex-direction: column;
  background: #fafafa;
}
.header {
  position: sticky;
  top: 0;
  z-index: 10;
  background: #fff;
  color: #1a1a1a;
  border-bottom: 1px solid #e5e5e5;
}
.inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 18px;
  height: 64px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 16px;
  align-items: center;
}
.logo {
  font-weight: 700;
  font-size: 20px;
  letter-spacing: -0.5px;
  cursor: pointer;
}
.nav {
  display: flex;
  gap: 8px;
}
.item {
  color: #666;
  text-decoration: none;
  padding: 10px 16px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}
.item.active,
.item:hover {
  color: #1a1a1a;
  background: #f5f5f5;
}
.right {
  display: flex;
  align-items: center;
  gap: 15px;

  .cart-btn,
  .notification-btn {
    position: relative;
    padding: 8px;
    cursor: pointer;
    color: #666;
    transition: color 0.2s;

    &:hover {
      color: #1a1a1a;
    }

    .cart-badge,
    .notification-badge {
      position: absolute;
      top: 0;
      right: 0;
      min-width: 18px;
      height: 18px;
      line-height: 18px;
      padding: 0 5px;
      background: #f56c6c;
      color: #fff;
      border-radius: 9px;
      font-size: 12px;
      text-align: center;
    }

    .cart-badge {
      background: #f7ba2a;
    }
  }

  .user {
    display: flex;
    gap: 10px;
    align-items: center;
    cursor: pointer;
  }
}
.name {
  font-size: 14px;
  color: #1a1a1a;
  font-weight: 500;
}
.main {
  flex: 1;
  padding: 24px 0 40px;
}
.footer {
  background: #1a1a1a;
  color: #888;
  text-align: center;
  padding: 24px 0;
  font-size: 13px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.copyright {
  margin-bottom: 4px;
}

.info-row {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}
</style>

