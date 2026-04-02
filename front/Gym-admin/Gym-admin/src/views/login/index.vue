<!--登录页面-->
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
        <!-- 用户名输入框 -->
        <el-form-item prop="userName" class="form-item">
          <el-input
            class="input-item"
            v-model="model.userName"
            prefix-icon="User"
            clearable
            :placeholder="$t('login.username')"
            :disabled="loading"
          />
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item prop="password" class="form-item">
          <el-input
            class="input-item"
            v-model="model.password"
            prefix-icon="Lock"
            show-password
            clearable
            :placeholder="$t('login.password')"
            :disabled="loading"
          />
        </el-form-item>

        <!-- 验证码输入框（核心修复：验证码区域） -->
        <el-form-item prop="captcha" class="form-item">
          <div class="captcha-wrap">
            <el-input
              class="captcha-input"
              v-model="model.captcha"
              prefix-icon="Picture"
              placeholder="请输入验证码"
              :disabled="loading"
            />
            <!-- 修复：增大验证码容器尺寸，调整内边距 -->
            <div 
              class="captcha-img" 
              @click="refreshCaptcha"
              :class="{ loading: loading }"
            >
              <img :src="captchaSrc" alt="验证码" v-if="captchaSrc" />
              <div v-else class="captcha-skeleton"></div>
            </div>
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
            {{ btnText }}
          </el-button>
        </el-form-item>

        <!-- 注册跳转按钮 -->
        <el-form-item class="form-item register-item">
          <el-button
            type="text"
            class="register-btn"
            @click="goToRegister"
            :disabled="loading"
          >
            <i class="el-icon-user-filled" style="font-size: 16px; color: #409eff; margin-right: 4px;"></i>
            {{ $t('login.goToRegister') }}
          </el-button>
        </el-form-item>
        
        <!-- 手机号登录跳转 -->
        <el-form-item class="form-item register-item">
          <el-button
            type="text"
            class="register-btn"
            @click="goToPhoneLogin"
            :disabled="loading"
          >
            <i class="el-icon-mobile-phone" style="font-size: 16px; color: #409eff; margin-right: 4px;"></i>
            手机号验证码登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 邮箱登录跳转 -->
      <el-form-item class="form-item register-item">
        <el-button
          type="text"
          class="register-btn"
          @click="goToEmailLogin"
          :disabled="loading"
        >
          <i class="el-icon-email" style="font-size: 16px; color: #409eff; margin-right: 4px;"></i>
          邮箱验证码登录
        </el-button>
      </el-form-item>

      
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
import { Login, GetValidateCode } from '@/api/login'
import { useRouter, useRoute } from 'vue-router'
import ChangeLang from '@/layout/components/Topbar/ChangeLang.vue'
import useLang from '@/i18n/useLang'
import { useApp } from '@/pinia/modules/app'

export default defineComponent({
  components: { 
    ChangeLang
  },
  name: 'login',
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
      userName: [
        {
          required: true,
          message: ctx?.$t('login.rules-username') || '请输入用户名',
          trigger: 'blur',
        },
      ],
      password: [
        {
          required: true,
          message: ctx?.$t('login.rules-password') || '请输入密码',
          trigger: 'blur',
        },
        {
          min: 6,
          max: 12,
          message: ctx?.$t('login.rules-regpassword') || '密码长度必须在6-12位之间',
          trigger: 'blur',
        },
      ],
      captcha: [
        {
          required: true,
          message: ctx?.$t('login.rules-validate-code') || '请输入验证码',
          trigger: 'blur',
        },
      ],
    })

    onMounted(() => {
      state.refreshCaptcha()
    })

    const state = reactive({
      model: {
        userName: 'admin',
        password: '123456',
        captcha: '',
        codeKey: ''
      },
      rules: getRules(),
      loading: false,
      captchaSrc: "",
      refreshCaptcha: async () => {
        try {
          const { data } = await GetValidateCode();
          state.model.codeKey = data?.codeKey || ''
          state.captchaSrc = data?.codeValue || ''
        } catch (err) {
          // 兼容：避免ctx未定义导致的报错
          if (ctx?.$message) {
            ctx.$message.error('验证码加载失败，请重试')
          } else {
            console.error('验证码加载失败：', err)
          }
        }
      },
      btnText: computed(() =>
        state.loading ? (ctx?.$t('login.logining') || '登录中...') : (ctx?.$t('login.login') || '登录')
      ),
      loginForm: ref(null),
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
                    message: ctx.$t('login.loginsuccess') || '登录成功',
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
                  ctx.$message.error(message || '登录失败')
                }
                state.refreshCaptcha()
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
      goToRegister: () => {
        router.push('/register')
      },
      goToPhoneLogin: () => {
        router.push('/phone')
      },
      goToEmailLogin: () => {
        router.push('/email')
      },
    })

    return {
      ...toRefs(state),
    }
  },
})
</script>

<style lang="scss" scoped>
// 全局容器
.login-container {
  width: 100vw;
  height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  // 背景层（兼容：移除backdrop-filter，改用纯色渐变）
  .login-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url('../login/background.jpg'); // 你的背景图路径
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    // 可选纹理（不想加纹理可删除下面的&::after区块）
    &::after {
      content: '';
      position: absolute;
      width: 100%;
      height: 100%;
      background-image: 
        radial-gradient(rgba(255, 255, 255, 0.1) 2px, transparent 2px);
      background-size: 40px 40px;
      opacity: 0.3;
    }
  }

  // 登录卡片
  .login-card {
    width: 420px;
    background: #ffffff; // 兼容：移除毛玻璃，改用纯白
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
    padding: 40px 30px;
    position: relative;
    z-index: 10;
    border: 1px solid #e5e7eb;

    // 登录头部
    .login-header {
      text-align: center;
      margin-bottom: 30px;

      .login-logo {
        margin-bottom: 12px;
        display: inline-block;
        width: 60px;
        height: 60px;
        line-height: 60px;
        background: #409eff;
        border-radius: 50%;
        color: #fff;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
        text-align: center;
      }

      .login-title {
        font-size: 24px;
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 8px 0;
      }

      .login-subtitle {
        font-size: 14px;
        color: #6b7280;
        margin: 0;
      }
    }

    // 表单区域
    .login-form {
      .form-item {
        margin-bottom: 20px;

        &.submit-item {
          margin-bottom: 12px;
        }

        &.register-item {
          margin-bottom: 0;
        }
      }

      // 输入框样式
      .input-item {
        :deep(.el-input__wrapper) {
          height: 50px;
          border-radius: 8px;
          border: 1px solid #e5e7eb;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
          transition: all 0.3s ease;
          background: #fff;

          &:hover {
            border-color: #93c5fd;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
          }

          &:focus-within {
            border-color: #409eff;
            box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
          }
        }

        :deep(.el-input__inner) {
          font-size: 15px;
          color: #1f2937;
          height: 100%;
          line-height: 50px;

          &::placeholder {
            color: #9ca3af;
          }
        }

        :deep(.el-input__prefix) {
          color: #9ca3af;
        }
      }

      // 验证码容器（核心修复：调整尺寸和适配）
      .captcha-wrap {
        display: flex;
        gap: 12px;
        align-items: center;

        .captcha-input {
          flex: 1;

          :deep(.el-input__wrapper) {
            height: 50px;
          }
        }

        // 核心修复：增大验证码容器，调整内边距和图片适配
        .captcha-img {
          width: 140px; // 加宽：从120px→140px
          height: 50px; // 和输入框高度一致
          border-radius: 8px;
          overflow: visible; // 修复：改为visible，避免裁剪
          cursor: pointer;
          border: 1px solid #e5e7eb;
          transition: all 0.2s ease;
          padding: 2px; // 减小内边距，给图片更多空间
          background: #fff;

          &:hover {
            border-color: #409eff;
            transform: scale(1.02);
          }

          &.loading {
            opacity: 0.7;
            pointer-events: none;
          }

          img {
            width: 100%;
            height: 100%;
            object-fit: contain; // 核心修复：改为contain（完整显示），而非cover（裁剪）
            border-radius: 6px; // 适配内边距后的圆角
          }
        }

        // 验证码骨架屏适配新尺寸
        .captcha-skeleton {
          width: 140px;
          height: 50px;
          background: #f3f4f6;
          border-radius: 8px;
        }
      }

      // 登录按钮
      .login-btn {
        width: 100%;
        height: 50px;
        border-radius: 8px;
        background: #409eff;
        border: none;
        font-size: 16px;
        font-weight: 500;
        transition: all 0.3s ease;

        &:hover {
          background: #3390ff;
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
        }

        &:active {
          transform: translateY(0);
        }
      }

      // 注册按钮
      .register-btn {
        color: #409eff;
        font-size: 14px;
        padding: 0;
        transition: all 0.2s ease;

        &:hover {
          color: #3390ff;
          text-decoration: underline;
        }
      }
    }

    // 底部版权
    .login-footer {
      margin-top: 30px;
      text-align: center;
      font-size: 12px;
      color: #9ca3af;
    }
  }
}

// 语言切换样式优化
.change-lang {
  position: fixed;
  right: 30px;
  top: 30px;
  z-index: 20;

  :deep {
    .change-lang {
      height: 36px;
      line-height: 36px;
      padding: 0 12px;
      background: #ffffff;
      border-radius: 18px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      color: #1f2937;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        background: #fff;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .icon {
        color: #409eff;
        margin-right: 6px;
      }
    }
  }
}

// 响应式适配
@media (max-width: 500px) {
  .login-container .login-card {
    width: 90%;
    padding: 30px 20px;
  }
  
  // 移动端验证码容器适配
  .login-form .captcha-img {
    width: 120px !important;
  }
}

.front-redirect {
  margin-top: 10px;
  text-align: center;
}
</style>