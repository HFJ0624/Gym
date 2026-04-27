<template>
  <div class="sign-check">
    <div class="status-box" :class="statusClass">
      <div class="icon" :class="statusClass">
        <div v-if="status === 'loading'" class="loading-spinner"></div>
        <div v-else-if="status === 'success'" class="success-icon">✓</div>
        <div v-else class="fail-icon">✕</div>
      </div>
      <h2>{{ statusText }}</h2>
      <p class="tip">{{ tipText }}</p>
      <el-button v-if="status !== 'loading'" type="primary" @click="goToIndex">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { doSign } from '@/api/sign'

const route = useRoute()
const router = useRouter()
const status = ref('loading')
const statusText = ref('正在签到...')
const tipText = ref('请稍候')
const statusClass = ref('loading')

const goToIndex = () => {
  router.push('/index')
}

onMounted(async () => {
  console.log('Check页面挂载，route.query:', route.query)
  
  const token = route.query.token
  if (!token) {
    status.value = 'fail'
    statusText.value = '无效的签到码'
    tipText.value = '请确认二维码是否正确'
    statusClass.value = 'fail'
    return
  }

  try {
    const res = await doSign(token)
    
    console.log('后端完整返回：', res)
    
    // 兼容不同的返回结构
    let resultText = res.data
    if (!resultText && res.message) {
      resultText = res.message
    }
    
    console.log('提取到的签到结果：', resultText)

    if (resultText && resultText.includes('已到场')) {
      status.value = 'success'
      statusText.value = resultText
      tipText.value = '签到成功，3秒后跳转...'
      statusClass.value = 'success'

      console.log('准备跳转，3秒后执行...')
      setTimeout(() => {
        console.log('执行跳转 router.push(/index)')
        router.push('/index').catch(err => {
          console.error('跳转失败：', err)
        })
      }, 3000)

    } else {
      status.value = 'fail'
      statusText.value = resultText || '签到失败'
      tipText.value = '请联系管理员'
      statusClass.value = 'fail'
    }
  } catch (err) {
    console.error('签到报错：', err)
    status.value = 'fail'
    statusText.value = '网络异常/接口未放行'
    tipText.value = '请检查网络后重试'
    statusClass.value = 'fail'
  }
})
</script>

<style scoped lang="scss">
.sign-check {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
}

.status-box {
  text-align: center;
  padding: 60px 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
}

.icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 40px;
  line-height: 1;
  
  &.loading {
    background: #f0f0f0;
  }
  
  &.success {
    background: #f0f9eb;
    color: #67c23a;
  }
  
  &.fail {
    background: #fef0f0;
    color: #f56c6c;
  }
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e0e0e0;
  border-top-color: #67c23a;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

h2 {
  margin: 0 0 12px;
  font-size: 24px;
  color: #1a1a1a;
}

.tip {
  color: #666;
  font-size: 14px;
  margin: 0 0 24px;
}
</style>
