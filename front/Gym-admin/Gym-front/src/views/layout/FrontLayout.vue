<template>
  <div class="layout">
    <header class="header">
      <div class="inner">
        <div class="logo" @click="go('/index')">体育场馆预约</div>
        <nav class="nav">
          <router-link class="item" to="/index" :class="{ active: isActive('/index') }">首页</router-link>
          <router-link class="item" to="/venues" :class="{ active: isActive('/venues') }">场馆</router-link>
          <router-link class="item" to="/venueComment" :class="{ active: isActive('/venueComment') }">场馆评论</router-link>
          <router-link class="item" to="/notice" :class="{ active: isActive('/notice') }">公告</router-link>
          <router-link class="item" to="/order" :class="{ active: isActive('/order') }">我的预约</router-link>
          <router-link class="item" to="/profile" :class="{ active: isActive('/profile') }">个人中心</router-link>
        </nav>
        <div class="right">
          <el-dropdown>
            <span class="user">
              <el-avatar size="small" :src="avatar" />
              <span class="name">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-item @click="go('/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item @click="go('/order')">我的预约</el-dropdown-item>
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuth()

// 先用死数据（你后续可在登录后调用 /front/me 并 auth.setUser）
const username = computed(() => auth.user?.username || '用户')
const avatar = computed(() => auth.user?.avatar)

const go = path => router.push(path)

const isActive = path => {
  if (path === '/venues') return route.path.startsWith('/venues')
  return route.path === path
}

const logout = () => {
  auth.logout()
  router.replace({ name: 'login', query: { redirect: route.fullPath } })
}
</script>

<style scoped lang="scss">
.layout {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}
.header {
  position: sticky;
  top: 0;
  z-index: 10;
  background: #409eff;
  color: #fff;
}
.inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 18px;
  height: 56px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 16px;
  align-items: center;
}
.logo {
  font-weight: 900;
  cursor: pointer;
}
.nav {
  display: flex;
  gap: 14px;
}
.item {
  color: #fff;
  text-decoration: none;
  padding: 8px 10px;
  border-radius: 8px;
  font-size: 14px;
}
.item.active,
.item:hover {
  background: rgba(255, 255, 255, 0.18);
}
.right .user {
  display: flex;
  gap: 8px;
  align-items: center;
  cursor: pointer;
}
.name {
  font-size: 13px;
}
.main {
  flex: 1;
  padding: 18px 0 30px;
}
.footer {
  background: #fff;
  border-top: 1px solid #eee;
  color: #909399;
  text-align: center;
  padding: 16px 0;
  font-size: 12px;
}
</style>

