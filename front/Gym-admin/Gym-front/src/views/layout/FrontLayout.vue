<template>
  <div class="layout">
    <header class="header">
      <div class="inner">
        <div class="logo" @click="go('/index')">体育场馆预约</div>
        <nav class="nav">
          <router-link class="item" to="/index" :class="{ active: isActive('/index') }">首页</router-link>
          <router-link class="item" to="/venues" :class="{ active: isActive('/venues') }">场馆</router-link>
          <router-link class="item" to="/shopping" :class="{ active: isActive('/shopping') }">商城</router-link>
          <router-link class="item" to="/venueComment" :class="{ active: isActive('/venueComment') }">场馆评论</router-link>
          <router-link class="item" to="/notice" :class="{ active: isActive('/notice') }">公告</router-link>
          <router-link class="item" to="/order" :class="{ active: isActive('/order') }">我的预约</router-link>
          <router-link class="item" to="/profile" :class="{ active: isActive('/profile') }">个人中心</router-link>
        </nav>
        <div class="right">
          <div class="cart-btn" @click="go('/shopping/cart')">
            <el-icon><ShoppingCart /></el-icon>
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount > 99 ? '99+' : cartCount }}</span>
          </div>
          <el-dropdown>
            <span class="user">
              <el-avatar size="small" :src="avatar" />
              <span class="name">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-item @click="go('/profile')">个人中心</el-dropdown-item>
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

    <footer class="footer">© 2026 体育场馆预约系统</footer>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '@/stores/auth'
import { useCart } from '@/stores/cart'
import { ShoppingCart } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const auth = useAuth()
const cartStore = useCart()

// 先用死数据（你后续可在登录后调用 /front/me 并 auth.setUser）
const username = computed(() => auth.user?.username || '用户')
const avatar = computed(() => auth.user?.avatar)
const cartCount = computed(() => cartStore.totalQuantity)

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

onMounted(() => {
  cartStore.loadCart()
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

  .cart-btn {
    position: relative;
    padding: 8px;
    cursor: pointer;
    color: #666;
    transition: color 0.2s;

    &:hover {
      color: #1a1a1a;
    }

    .cart-badge {
      position: absolute;
      top: 0;
      right: 0;
      min-width: 18px;
      height: 18px;
      line-height: 18px;
      padding: 0 5px;
      background: #f7ba2a;
      color: #fff;
      border-radius: 9px;
      font-size: 12px;
      text-align: center;
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
}
</style>

