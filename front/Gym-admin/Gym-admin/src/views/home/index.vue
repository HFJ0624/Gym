<template>
  <div class="home-container">
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.userCount }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <el-icon :size="24"><OfficeBuilding /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.venueCount }}</div>
          <div class="stat-label">场馆总数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <el-icon :size="24"><Calendar /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.bookingCount }}</div>
          <div class="stat-label">预约总数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
          <el-icon :size="24"><Bell /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.noticeCount }}</div>
          <div class="stat-label">公告总数</div>
        </div>
      </div>
    </div>

    <div class="content-row">
      <div class="chart-card">
        <div class="card-header">
          <span class="card-title">用户性别分布</span>
        </div>
        <div ref="chart" class="chart-container"></div>
      </div>

      <div class="list-card">
        <div class="card-header">
          <span class="card-title">最新体育公告</span>
          <el-button type="primary" text @click="handleViewAllNotices">查看全部</el-button>
        </div>
        <div class="list-content">
          <div v-for="notice in recentNotices" :key="notice.id" class="list-item">
            <div class="item-icon" style="background: #ecf5ff; color: #409eff;">
              <el-icon><Document /></el-icon>
            </div>
            <div class="item-content">
              <div class="item-title">{{ notice.title }}</div>
              <div class="item-time">{{ notice.createTime }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="content-row">
      <div class="list-card full-width">
        <div class="card-header">
          <span class="card-title">最新公告评论</span>
          <el-button type="primary" text @click="handleViewAllComments">查看全部</el-button>
        </div>
        <div class="list-content">
          <div v-for="comment in recentComments" :key="comment.id" class="comment-item">
            <el-avatar :size="40" :src="comment.avatar" />
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.username }}</span>
                <span class="comment-time">{{ comment.createTime }}</span>
              </div>
              <div class="comment-text">{{ comment.content }}</div>
              <div class="comment-rating">
                <el-rate :model-value="comment.esteem" disabled show-score />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useApp } from '@/pinia/modules/app'
import { useRouter } from 'vue-router'  
import { findGender } from '@/api/User'
import * as echarts from 'echarts'
import { GetAllVenue } from '@/api/Venue'
import { CountAllBook } from '@/api/CourtBook'
import { FindAllNotice } from '@/api/Notice'
import { GetRecentComment } from '@/api/NoticeComment'
import { 
  User, 
  OfficeBuilding, 
  Calendar, 
  Bell, 
  Document 
} from '@element-plus/icons-vue'

const appStore = useApp()
const chart = ref(null)
let myChart = null
const router = useRouter()  

const stats = ref({
  userCount: 0,
  venueCount: 0,
  bookingCount: 0,
  noticeCount: 45
})

//加载最近公告
const recentNotices = ref([])

const recentComments = ref([])

// =============== 【新加】加载场馆总数 ===============
const loadVenueCount = async () => {
  try {
    const res = await GetAllVenue()
    if (res.data && res.data.allVenue) {
      stats.value.venueCount = res.data.allVenue.length
    }
  } catch (e) {
    console.error('获取场馆数量失败', e)
  }
}

// =============== 【新加】加载预约总数 ===============
const loadBookingCount = async () => {
  try {
    const res = await CountAllBook()
    if (res.data) {
      stats.value.bookingCount = res.data.length
    }
  } catch (e) {
    console.error('获取预约数量失败', e)
  }
}

// =============== 【新加】加载公告总数 ===============
const loadNoticeCount = async () => {
  try {
    const res = await FindAllNotice()
    if (res.data && res.data.allNotice) {
      stats.value.noticeCount = res.data.allNotice.length 
    }
  } catch (e) {
    console.error('获取公告数量失败', e)
  }
}

onMounted(() => {
  myChart = echarts.init(chart.value)
  loadData()
  loadVenueCount()
  loadNoticeCount()
  loadBookingCount()
  loadRecentNotices()
  loadRecentComments()
})

const loadData = async () => {
  const res = await findGender()
  // 使用 findGender 返回的集合长度来显示总用户数
  if (res.data && res.data.length > 0) {
    stats.value.userCount = res.data.reduce((total, item) => total + item.number, 0)
  }
  renderChart(res.data)
}

//加载最近公告
const loadRecentNotices = async () => {
  try {
    const res = await FindAllNotice()
    if (res.data && res.data.allNotice) {
      // 直接赋值后端返回的真实公告列表，取前5条
      recentNotices.value = res.data.allNotice.slice(0, 5)
    }
  } catch (error) {
    console.error('获取最新公告失败:', error)
  }
}

const loadRecentComments = async () => {
  try {
    const res = await GetRecentComment()
    if (res.data && res.data.recentComment.length > 0) {
      recentComments.value = res.data.recentComment
    }
  } catch (error) {
    console.error('获取最新评论失败:', error)
  }
}

const renderChart = (dataList) => {
  const option = {
    title: {
      text: '用户性别分布',
      subtext: '统计数据',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '性别占比',
        type: 'pie',
        radius: '50%',
        data: dataList.map(item => ({
          name: item.sex,
          value: item.number
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  myChart.setOption(option)
}

onUnmounted(() => {
  myChart?.dispose()
})

const getHomeStats = async () => {
  return { data: stats.value }
}

const getRecentNotices = async () => {
  return { data: recentNotices.value }
}

const getRecentComments = async () => {
  return { data: recentComments.value }
}

// ===================== 查看全部评论跳转 =====================
const handleViewAllComments = () => {
  router.push('/noticeComment')
}

// ===================== 查看全部公告跳转 =====================
const handleViewAllNotices = () => {
  router.push('/notice')
}
</script>

<style scoped lang="scss">
.home-container {
  padding: 20px;
  min-height: 100vh;
  background: var(--el-bg-color-page);
  transition: background-color 0.3s;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.content-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;

  &.full-width {
    grid-template-columns: 1fr;
  }
}

.chart-card,
.list-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.chart-container {
  height: 300px;
}

.list-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: var(--el-bg-color-page);
  transition: all 0.3s;

  &:hover {
    background: var(--el-fill-color-light);
  }
}

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  background: var(--el-bg-color-page);
  transition: all 0.3s;

  &:hover {
    background: var(--el-fill-color-light);
  }
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.comment-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.comment-text {
  font-size: 14px;
  color: var(--el-text-color-regular);
  margin-bottom: 8px;
  line-height: 1.6;
}

.comment-rating {
  display: flex;
  align-items: center;
}

@media (max-width: 1200px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .content-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
