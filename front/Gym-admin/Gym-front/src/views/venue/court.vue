<template>
  <div class="page" v-loading="loading">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item to="/index">首页</el-breadcrumb-item>
      <el-breadcrumb-item to="/venues">场馆列表</el-breadcrumb-item>
      <el-breadcrumb-item>{{ venue?.venueName || '场地列表' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 场馆信息头部 -->
    <div class="hero" v-if="venue">
      <img class="cover" :src="venue.avatar" :alt="venue.venueName" />
      <div class="info">
        <div class="top">
          <div class="name">{{ venue.venueName }}</div>
        </div>
        <div class="meta">
          <span>地址：{{ venue.location }}</span>
        </div>
        <div class="addr">电话：{{ venue.phone }}</div>
      </div>
    </div>

    <!-- 场地列表 -->
    <div class="grid" v-loading="loading">
      <div 
        v-for="court in courtList" 
        :key="court.id"
        class="card"
        @click="goToBooking(court.id)"
      >
        <div class="body">
          <div class="row">
            <div class="name">{{ court.name }}</div>
            <div class="status" :class="court.status">
              {{ court.statusText }}
            </div>
          </div>
          <div class="meta">
            <span class="tag">{{ court.type }}</span>
            <span>容量：{{ court.capacity }}人</span>
            <span>价格：¥{{ court.price }}/小时</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {GetAllCourt} from '@/api/court'

const router = useRouter()
const route = useRoute()
const loading = ref(false)

// 场馆信息
const venue = ref({
  id: null,
  venueName: '',
  location: '',
  phone: '',
  avatar: ''
})

// 场地列表数据
const courtList = ref([])

// 跳转到场地详情页面
const goToBooking = (courtId) => {
  router.push({ path: `/venues/details/${courtId}`, query: { venueId: venue.value.id } })
}

// 从后端获取场馆详情和场地列表
const loadData = async () => {
  loading.value = true
  try {
    const venueId = route.params.id
    
    // 调用后端接口获取该场馆下的所有场地
    const response = await GetAllCourt(venueId)
    if (response.data && response.data.allCourt) {
      courtList.value = response.data.allCourt.map(court => ({
        ...court,
        status: 'available', // 后续可根据实际业务逻辑判断场地状态
        statusText: '可预约'
      }))
      
      // 如果有场地，取第一个场地的场馆信息
      if (courtList.value.length > 0) {
        const firstCourt = courtList.value[0]
        venue.value = {
          id: venueId,
          venueName: firstCourt.venueName,
          location: firstCourt.location,
          phone: firstCourt.phone,
          avatar: firstCourt.avatar
        }
      }
    }
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
  max-width: 1200px;
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

.hero {
  display: flex;
  margin: 32px 0;
  background: #fff;
  border: 1px solid #e5e5e5;
  overflow: hidden;
}

.hero .cover {
  width: 400px;
  height: 280px;
  object-fit: cover;
  filter: grayscale(20%);
}

.hero .info {
  flex: 1;
  padding: 32px;
}

.hero .top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.hero .name {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: -0.5px;
}

.hero .price {
  color: #1a1a1a;
  font-weight: 700;
}

.hero .meta {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  color: #666;
  font-size: 14px;
}

.hero .meta .tag {
  background: #f5f5f5;
  color: #1a1a1a;
  padding: 6px 14px;
  font-weight: 500;
}

.hero .addr {
  color: #666;
  font-size: 14px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.card {
  background: #fff;
  border: 1px solid #e5e5e5;
  padding: 24px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.card:hover {
  border-color: #1a1a1a;
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
}

.card .row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.card .name {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

.card .status {
  padding: 6px 16px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid;
}

.card .status.available {
  background-color: #1a1a1a;
  color: #fff;
  border-color: #1a1a1a;
}

.card .status.occupied {
  background-color: #fff;
  color: #1a1a1a;
  border-color: #1a1a1a;
}

.card .status.maintenance {
  background-color: #f5f5f5;
  color: #666;
  border-color: #e5e5e5;
}

.card .meta {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.card .meta span {
  background: #f5f5f5;
  color: #666;
  padding: 6px 14px;
  font-size: 14px;
}
</style>