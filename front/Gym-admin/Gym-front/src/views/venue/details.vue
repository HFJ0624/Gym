<template>
  <div class="page" v-loading="loading">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item to="/index">首页</el-breadcrumb-item>
      <el-breadcrumb-item to="/venues">场馆列表</el-breadcrumb-item>
      <el-breadcrumb-item :to="`/venues/court/${venueId}`">场地列表</el-breadcrumb-item>
      <el-breadcrumb-item>{{ courtInfo.name || '场地详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 场地详情信息 -->
    <div class="detail-container" v-if="courtInfo">
      <div class="info-section">
        <h2>{{ courtInfo.name }}</h2>
        <div class="basic-info">
          <div class="info-item">
            <label>场地类型：</label>
            <span>{{ courtInfo.type }}</span>
          </div>
          <div class="info-item">
            <label>容量：</label>
            <span>{{ courtInfo.capacity }}人</span>
          </div>
          <div class="info-item">
            <label>价格：</label>
            <span>¥{{ courtInfo.price }}/小时</span>
          </div>
          <div class="info-item">
            <label>所属场馆：</label>
            <span>{{ courtInfo.venueName }}</span>
          </div>
          <div class="info-item">
            <label>场馆地址：</label>
            <span>{{ courtInfo.location }}</span>
          </div>
          <div class="info-item">
            <label>联系电话：</label>
            <span>{{ courtInfo.phone }}</span>
          </div>
        </div>
      </div>

      <!-- 预约表单 -->
      <div class="booking-form">
        <el-form :model="bookingForm" label-width="80px">
          <el-form-item label="预约日期">
            <el-date-picker
              v-model="bookingForm.bookingDate"
              type="date"
              placeholder="选择日期"
              style="width: 100%;"
            />
          </el-form-item>
          <el-form-item label="备注">
            <el-input
              v-model="bookingForm.remark"
              type="textarea"
              placeholder="请输入备注信息"
              :rows="3"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitBooking">提交预约</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
// 后续需要导入后端API接口
import { GetCourtDetail, BookCourt } from '@/api/court'
// 从Pinia store中获取用户信息
import { useAuth } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const venueId = route.query.venueId
const auth = useAuth()
const userId = auth.user?.id

// 场地详情信息
const courtInfo = ref({
  id: null,
  name: '',
  type: '',
  capacity: 0,
  price: 0,
  venueName: '',
  location: '',
  phone: ''
})

// 预约表单数据
const bookingForm = ref({
  bookingDate: '',
  remark: '',
})

// 提交预约
const submitBooking = async() => {
  if (!bookingForm.value.bookingDate) {
    ElMessage.warning('请选择预约日期')
    return
  }
  // 后续需要调用后端预约接口
  try {
    const bookingData = {
      courtId: courtInfo.value.id,
      totalPrice: courtInfo.value.price,
      bookingDate: bookingForm.value.bookingDate,
      remark: bookingForm.value.remark,
      userId: userId
    }
    
    const {code} = await BookCourt(bookingData);
    if (code == 200) {
      ElMessage.success('预约成功')
      // 预约成功后跳转到我的预约页面
      router.push('/order')
    }else if(code == 224){
      ElMessage.error('余额不足，请充值充值')
    }
  } catch (error) {
    ElMessage.error('预约失败，请稍后重试')
  }
  ElMessage.success('预约提交成功')
}

// 从后端获取场地详情
const loadData = async () => {
  loading.value = true
  try {
    const courtId = route.params.id
    const response = await GetCourtDetail(courtId, venueId)
    courtInfo.value = response.data.court[0]
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 18px;
}

:deep(.el-breadcrumb__inner) {
  color: #666;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #1a1a1a;
  font-weight: 600;
}

:deep(.el-button--primary) {
  background: #1a1a1a;
  border-color: #1a1a1a;
  border-radius: 0;
}

:deep(.el-input__wrapper) {
  border-radius: 0;
}

:deep(.el-date-editor) {
  border-radius: 0;
}

.detail-container {
  background: #fff;
  border: 1px solid #e5e5e5;
  padding: 32px;
  margin-top: 32px;
}

.info-section h2 {
  margin-top: 0;
  margin-bottom: 28px;
  font-size: 32px;
  color: #1a1a1a;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.basic-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 18px;
  background: #fafafa;
  border: 1px solid #e5e5e5;
}

.info-item label {
  font-weight: 600;
  color: #666;
  margin-right: 12px;
  min-width: 100px;
  font-size: 14px;
}

.info-item span {
  color: #1a1a1a;
  font-weight: 500;
}

.booking-form {
  margin-top: 36px;
  padding: 28px;
  background: #fafafa;
  border: 1px solid #e5e5e5;
}

.booking-form .el-form-item {
  margin-bottom: 24px;
}
</style>