<template>
  <div class="wrap" :style="{ backgroundImage: `url(${bgUrl})` }">
    <div class="card">
      <div class="brand">
        <div class="title">体育场馆预约</div>
        <div class="sub">用户登录</div>
      </div>

      <el-form :model="form" label-position="top" @keyup.enter="onSubmit">
        <el-form-item label="账号/手机号">
          <el-input v-model="form.username" placeholder="请输入账号或手机号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item label="验证码">
          <div class="captcha-row">
            <el-input v-model="form.captcha" placeholder="请输入验证码" />
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
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuth } from '@/stores/auth'
import { FrontLogin, GetFrontValidateCode,GetFrontUserInfo } from '@/api/auth'
import bgUrl from '@/assets/background.jpg'


const route = useRoute()
const router = useRouter()
const auth = useAuth()

const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  captcha: '',
  codeKey: '',
})

const captchaImg = ref('')

const loadCaptcha = async () => {
  try {
    const res = await GetFrontValidateCode()
    captchaImg.value = res?.data?.codeValue || ''
    form.codeKey = res?.data?.codeKey || ''
  } catch (e) {
    captchaImg.value = ''
    form.codeKey = ''
  }
}

const onSubmit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    // 你后端 LoginDto 里有 captcha、codeKey，这里按该字段提交
    const res = await FrontLogin({
      userName: form.username,
      password: form.password,
      captcha: form.captcha,
      codeKey: form.codeKey,
    })

    const token = res?.data?.token
    if (!token) {
      ElMessage.error('登录失败：未获取到 token')
      await loadCaptcha()
      return
    }

    auth.setToken(token)
    // 拉取用户信息
    const me = await GetFrontUserInfo()
    auth.setUser(me.data)

    ElMessage.success('登录成功')
    const redirect = route.query.redirect ? String(route.query.redirect) : '/index'
    router.replace(redirect)
  } catch (e) {
    ElMessage.error('登录失败，请检查账号密码或验证码')
    await loadCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(loadCaptcha)
</script>

<style scoped lang="scss">
.wrap {
  height: 100%;
  min-height: 100vh;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: grid;
  place-items: center;
  padding: 20px;
}
.card {
  width: 420px;
  background: #fff;
  border-radius: 14px;
  padding: 22px 22px 18px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.08);
}
.brand {
  margin-bottom: 10px;
}
.title {
  font-size: 20px;
  font-weight: 900;
  color: #1f2d3d;
}
.sub {
  margin-top: 6px;
  color: #909399;
  font-size: 13px;
}
.btn {
  width: 100%;
}
.captcha-row {
  display: grid;
  grid-template-columns: 1fr 120px;
  gap: 10px;
  align-items: center;
}
.captcha {
  height: 32px;
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
    object-fit: cover;
  }
}
.captcha-placeholder {
  font-size: 12px;
  color: #909399;
}
.tips {
  margin-top: 14px;
  font-size: 12px;
  color: #909399;
  line-height: 1.6;

  code {
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
      monospace;
  }
}
.reg-link {
  margin-top: 12px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}
</style>

