<template>
  <div class="login-page">
    <!-- 左侧：大号灵动偷看玩偶 -->
    <div class="left-side">
      <div class="logo-text">体育场馆预约系统</div>

      <div class="character-group"
        :class="{
          peek: focusUsername,
          hide: focusPassword
        }"
      >
        <div class="char char1">
          <div class="eye left"></div>
          <div class="eye right"></div>
        </div>
        <div class="char char2">
          <div class="eye left"></div>
          <div class="eye right"></div>
        </div>
        <div class="char char3">
          <div class="eye left"></div>
          <div class="eye right"></div>
        </div>
        <div class="char char4">
          <div class="eye left"></div>
          <div class="eye right"></div>
        </div>
      </div>
    </div>

    <!-- 右侧：登录表单（完全不变） -->
    <div class="right-side">
      <div class="card">
        <div class="brand">
          <div class="title">体育场馆预约</div>
          <div class="sub">用户登录</div>
        </div>

        <el-form :model="form" label-position="top" @keyup.enter="onSubmit">
          <el-form-item label="账号/手机号">
            <el-input
              v-model="form.username"
              placeholder="请输入账号或手机号"
              @focus="focusUsername = true"
              @blur="focusUsername = focusPassword = false"
            />
          </el-form-item>

          <el-form-item label="密码">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              @focus="focusPassword = true"
              @blur="focusUsername = focusPassword = false"
            />
          </el-form-item>

          <el-form-item label="验证码">
            <div class="captcha-row">
              <el-input v-model="form.captcha" placeholder="请输入验证码" />
              <!-- 验证码区域 -->
              <div class="captcha" @click="loadCaptcha">
                <img v-if="captchaImg" :src="captchaImg" alt="captcha" />
                <div v-else class="captcha-placeholder">点击获取</div>
              </div>
            </div>
          </el-form-item>

          <el-button type="primary" size="large" class="btn" :loading="loading" @click="onSubmit">
            登录
          </el-button>

          <div class="reg-link">
            <span>没有注册？</span>
            <el-link type="primary" :underline="false" @click="router.push('/register')">去注册</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
// ==============================
// 你的原有逻辑 100% 完全不动
// ==============================
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuth } from '@/stores/auth'
import { FrontLogin, GetFrontValidateCode, GetFrontUserInfo } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuth()

const loading = ref(false)
const form = reactive({ username: '', password: '', captcha: '', codeKey: '' })
const captchaImg = ref('')

const loadCaptcha = async () => {
  try {
    const res = await GetFrontValidateCode()
    captchaImg.value = res?.data?.codeValue || ''
    form.codeKey = res?.data?.codeKey || ''
  } catch (e) {}
}

const onSubmit = async () => {
  if (!form.username || !form.password) { ElMessage.warning('请输入账号和密码'); return }
  loading.value = true
  try {
    const res = await FrontLogin({ userName: form.username, password: form.password, captcha: form.captcha, codeKey: form.codeKey })
    const token = res?.data?.token
    if (!token) { ElMessage.error('登录失败：未获取到 token'); await loadCaptcha(); return }
    auth.setToken(token)
    const me = await GetFrontUserInfo()
    auth.setUser(me.data)
    ElMessage.success('登录成功')
    router.replace(route.query.redirect || '/index')
  } catch (e) {
    ElMessage.error('登录失败，请检查账号密码或验证码')
    await loadCaptcha()
  } finally { loading.value = false }
}

onMounted(loadCaptcha)

// ==============================
// 灵动交互控制
// ==============================
const focusUsername = ref(false)
const focusPassword = ref(false)
</script>

<style scoped lang="scss">
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  background: #f6f7f9;
  overflow: hidden;
}

/* ========= 左侧放大版 ========= */
.left-side {
  flex: 1;
  background: #f6f7f9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

/* 标题放大 */
.logo-text {
  font-size: 38px;    /* 放大 */
  font-weight: 800;
  color: #222;
  margin-bottom: 70px;
}

/* 玩偶组合放大 */
.character-group {
  display: flex;
  gap: 22px;        /* 放大间距 */
  align-items: flex-end;
  transition: all 0.5s cubic-bezier(0.3, 0.9, 0.5, 1);

  /* 账号：偷看动作更大更明显 */
  &.peek {
    transform: translateX(160px) scale(0.95) rotate(-2.5deg);
    .eye {
      width: 18px;
      height: 18px;
      background: #111;
    }
  }

  /* 密码：闭眼 */
  &.hide {
    transform: translateX(60px);
    .eye {
      width: 20px;
      height: 4px;
      background: #111;
    }
  }
}

/* 大号玩偶 */
.char {
  position: relative;
  border-radius: 14px;
  display: flex;
  justify-content: center;

  .eye {
    position: absolute;
    top: 24%;
    width: 16px;
    height: 16px;
    background: #fff;
    border-radius: 50%;
    transition: all 0.28s ease;
    &.left { left: 24%; }
    &.right { right: 24%; }
  }
}

/* 全部放大 40%～60% */
.char1 { width: 140px; height: 140px; background: #7c3aed; }
.char2 { width: 115px; height: 200px; background: #ff9a6c; }
.char3 { width: 105px; height: 190px; background: #111; }
.char4 { width: 95px; height: 140px; background: #facc15; border-radius: 48px 48px 14px 14px; }

/* ========= 右侧 ========= */
.right-side {
  flex: 1;
  background: #fff;
  display: grid;
  place-items: center;
  padding: 30px;
}

.card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 10px 35px rgba(0,0,0,0.07);
}

.brand {
  text-align: center;
  margin-bottom: 24px;
}
.title { font-size: 26px; font-weight: 800; color: #1f2d3d; }
.sub { margin-top: 6px; color: #999; font-size: 15px; }

.btn { width: 100%; height: 46px; border-radius: 8px; }

/* ======================
   🔥 修复：验证码放大、完全显示
   ====================== */
.captcha-row {
  display: grid;
  grid-template-columns: 1fr 140px; /* 宽度放大 */
  gap: 10px;
  align-items: center;
}
.captcha {
  height: 46px;        /* 高度变大 */
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  display: grid;
  place-items: center;
  background: #fafafa;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover; /* 保证验证码图片完全显示 */
    display: block;
  }
}
.captcha-placeholder {
  font-size: 13px;
  color: #999;
}

.reg-link {
  text-align: center;
  margin-top: 16px;
  font-size: 13px;
  color: #999;
}

@media (max-width: 768px) {
  .left-side { display: none; }
}
</style>