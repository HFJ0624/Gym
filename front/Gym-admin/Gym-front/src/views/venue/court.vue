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
  padding: 20px;
}

.hero {
  display: flex;
  margin-bottom: 30px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.hero .cover {
  width: 400px;
  height: 250px;
  object-fit: cover;
}

.hero .info {
  flex: 1;
  padding: 20px;
}

.hero .top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.hero .name {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.hero .price {
  color: #f56c6c;
  font-weight: bold;
}

.hero .meta {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #666;
}

.hero .meta .tag {
  background: #e6f7ff;
  color: #1890ff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.hero .addr {
  color: #666;
  font-size: 14px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.card .row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card .name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.card .status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
}

.card .status.available {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.card .status.occupied {
  background-color: #ffebee;
  color: #c62828;
}

.card .status.maintenance {
  background-color: #fff3e0;
  color: #ef6c00;
}

.card .meta {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.card .meta span {
  background: #f0f2f5;
  color: #666;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
}
</style>