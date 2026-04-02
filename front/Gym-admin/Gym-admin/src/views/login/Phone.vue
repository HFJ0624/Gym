<!--手机号验证码登录页面-->
<template>
  <div class="login-container">
    <!-- 背景装饰层 -->
    <div class="login-bg"></div>
    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 顶部logo/标题区 -->
      <div class="login-header">
        <div class="login-logo">
          <i class="el-icon-stadium" style="font-size: 32px; color: #409eff;"></i>
        </div>
        <h1 class="login-title">体育场馆预约系统</h1>
        <p class="login-subtitle">高效管理 · 便捷预约</p>
      </div>

      <!-- 表单区域 -->
      <el-form 
        class="login-form" 
        :model="model" 
        :rules="rules" 
        ref="loginForm"
        size="large"
      >
        <!-- 手机号输入框 -->
        <el-form-item prop="phone" class="form-item">
          <el-input
            class="input-item"
            v-model="model.phone"
            prefix-icon="Phone"
            clearable
            placeholder="请输入手机号"
            :disabled="loading"
          />
        </el-form-item>

        <!-- 验证码输入框 -->
        <el-form-item prop="code" class="form-item">
          <div class="captcha-wrap">
            <el-input
              class="captcha-input"
              v-model="model.phoneCode"
              prefix-icon="Message"
              placeholder="请输入验证码"
              :disabled="loading"
            />
            <el-button 
              type="primary"
              class="get-code-btn"
              :disabled="!canGetCode || loading"
              @click="getCode"
            >
              {{ codeBtnText }}
            </el-button>
          </div>
        </el-form-item>

        <!-- 登录按钮 -->
        <el-form-item class="form-item submit-item">
          <el-button
            :loading="loading"
            type="primary"
            class="login-btn"
            size="large"
            @click="submit"
            icon="CircleCheck"
          >
            登录
          </el-button>
        </el-form-item>

        <!-- 账号密码登录跳转 -->
        <el-form-item class="form-item register-item">
          <el-button
            type="text"
            class="register-btn"
            @click="goToPasswordLogin"
            :disabled="loading"
          >
            <i class="el-icon-user-filled" style="font-size: 16px; color: #409eff; margin-right: 4px;"></i>
            账号密码登录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部版权信息 -->
      <div class="login-footer">
        <span>© 2026 体育场馆预约系统 版权所有</span>
      </div>
    </div>
  </div>

  <!-- 语言切换 -->
  <div class="change-lang">
    <change-lang />
  </div>
</template>

<script>
import {
  defineComponent,
  getCurrentInstance,
  reactive,
  toRefs,
  ref,
  computed,
  onMounted,
  watch,
} from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { GetSmsValidateCode,Login } from '@/api/login'
import ChangeLang from '@/layout/components/Topbar/ChangeLang.vue'
import useLang from '@/i18n/useLang'
import { useApp } from '@/pinia/modules/app'

export default defineComponent({
  components: { 
    ChangeLang
  },
  name: 'phone-login',
  setup() {
    const { proxy: ctx } = getCurrentInstance()
    const router = useRouter()
    const route = useRoute()
    const { lang } = useLang()

    // 兼容：避免lang未定义导致的报错
    watch(lang || ref(''), () => {
      state.rules = getRules()
    })

    const getRules = () => ({
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
      ],
      phoneCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
        { min: 6, max: 6, message: '验证码长度为6位', trigger: 'blur' }
      ]
    })

    onMounted(() => {
      // 页面加载完成后的初始化操作
    })

    const state = reactive({
      model: {
        phone: '',
        phoneCode: ''
      },
      rules: getRules(),
      loading: false,
      canGetCode: true,
      codeBtnText: '获取验证码',
      loginForm: ref(null),
      getCode: async () => {
        if (!state.model.phone) {
          ElMessage.warning('请先输入手机号')
          return
        }
        try {
          const { code, message } = await GetSmsValidateCode(state.model.phone)
          if (code === 200) {
            ElMessage.success('验证码已发送，请注意查收')
            state.canGetCode = false
            state.codeBtnText = '60秒后重新获取'
            let countdown = 60
            const timer = setInterval(() => {
              countdown--
              state.codeBtnText = `${countdown}秒后重新获取`
              if (countdown <= 0) {
                clearInterval(timer)
                state.canGetCode = true
                state.codeBtnText = '获取验证码'
              }
            }, 1000)
          } else {
            ElMessage.error(message || '获取验证码失败，请稍后重试')
          }
        } catch (error) {
          ElMessage.error('获取验证码失败，请稍后重试')
        }
      },
      submit: () => {
        if (state.loading) {
          return
        }
        // 兼容：避免loginForm未定义
        if (!state.loginForm) return
        
        state.loginForm.validate(async valid => {
          if (valid) {
            state.loading = true
            try {
              const { code, data, message } = await Login(state.model)
              if (+code === 200) {
                if (ctx?.$message) {
                  ctx.$message.success({
                    message: '登录成功',
                    duration: 1000,
                  })
                }

                const targetPath = decodeURIComponent(route.query.redirect || '')
                if (targetPath.startsWith('http')) {
                  window.location.href = targetPath
                } else if (targetPath.startsWith('/')) {
                  router.push(targetPath)
                } else {
                  router.push('/')
                }
                useApp().initToken(data)
              } else {
                if (ctx?.$message) {
                  ctx.$message.error(message || '登录失败，请检查手机号和验证码')
                }
              }
            } catch (err) {
              if (ctx?.$message) {
                ctx.$message.error('登录请求失败，请检查网络')
              }
              console.error('登录失败：', err)
            } finally {
              state.loading = false
            }
          }
        })
      },
      goToPasswordLogin: () => {
        router.push('/login')
      },
    })

    return {
      ...toRefs(state),
    }
  },
})
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.login-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  background: url('./background.jpg') no-repeat center center;
  background-size: cover;
}

.login-card {
  width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-logo {
  margin-bottom: 20px;
}

.login-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.login-subtitle {
  font-size: 14px;
  color: #999;
}

.login-form {
  margin-bottom: 20px;
}

.form-item {
  margin-bottom: 20px;
}

.input-item {
  width: 100%;
}

.captcha-wrap {
  display: flex;
  gap: 10px;
}

.captcha-input {
  flex: 1;
}

.get-code-btn {
  width: 120px;
}

.submit-item {
  margin-bottom: 15px;
}

.login-btn {
  width: 100%;
}

.register-item {
  text-align: center;
}

.register-btn {
  color: #409eff;
  font-size: 14px;
}

.login-footer {
  text-align: center;
  color: #999;
  font-size: 12px;
  margin-top: 20px;
}
</style>