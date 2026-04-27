<template>
  <div class="sign-generate">
    <div class="head">
      <h2>签到二维码生成</h2>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-card class="info-card" v-if="orderInfo">
      <div class="info-row">
        <span class="label">场地名称：</span>
        <span class="value">{{ orderInfo.courtName }}</span>
      </div>
      <div class="info-row">
        <span class="label">预约日期：</span>
        <span class="value">{{ orderInfo.bookingDate }}</span>
      </div>
      <div class="info-row">
        <span class="label">预约时间：</span>
        <span class="value">{{ orderInfo.startTime }} - {{ orderInfo.endTime }}</span>
      </div>
    </el-card>

    <el-card class="form-card">
      <el-form :model="form" label-width="100px">
        <el-form-item label="签到人姓名">
          <el-input v-model="form.name" placeholder="请输入签到人姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="手机号（选填）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleGenerate" :loading="loading">
            {{ loading ? '生成中...' : '生成签到二维码' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="qrcode-card" v-if="qrInfo">
      <h3>签到二维码</h3>
      <div class="qr-img-box">
        <img :src="qrInfo.qrBase64" alt="签到二维码" class="qr-img" />
      </div>
      <p class="tip">用户扫码后即可完成签到</p>
      <p class="token">签到码：{{ qrInfo.token }}</p>
      <el-button type="primary" @click="downloadQR">下载二维码</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { generateSignQR } from '@/api/sign'
import { useAuth } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuth()

const form = ref({ name: '', phone: '' })
const loading = ref(false)
const qrInfo = ref(null)
const orderInfo = ref(null)

onMounted(() => {
  orderInfo.value = route.query
  // 自动填充用户信息
  if (auth.user) {
    form.value.name = auth.user.username
    form.value.phone = auth.user.phone
  }
})

// 生成二维码
const handleGenerate = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入签到人姓名')
    return
  }
  
  loading.value = true
  try {
    const data = {
      ...form.value,
      orderId: orderInfo.value?.orderId
    }
    const res = await generateSignQR(data)
    qrInfo.value = res.data
    ElMessage.success('生成成功')
  } catch (err) {
    ElMessage.error('生成失败：' + (err.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 下载二维码
const downloadQR = () => {
  const link = document.createElement('a')
  link.href = qrInfo.value.qrBase64
  link.download = `签到码_${form.value.name}.png`
  link.click()
}

const goBack = () => {
  router.back()
}
</script>

<style scoped lang="scss">
.sign-generate {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.head h2 {
  margin: 0;
  font-size: 24px;
  color: #1a1a1a;
}

.info-card,
.form-card,
.qrcode-card {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  
  &:last-child {
    border-bottom: none;
  }
}

.label {
  color: #666;
  width: 100px;
}

.value {
  color: #1a1a1a;
  font-weight: 500;
}

.qrcode-card {
  text-align: center;
}

.qr-img-box {
  padding: 20px 0;
}

.qr-img {
  width: 300px;
  height: 300px;
}

.tip {
  color: #666;
  margin: 10px 0;
  font-size: 14px;
}

.token {
  color: #999;
  font-size: 12px;
  margin: 10px 0;
}
</style>
