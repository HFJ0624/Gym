<template>
  <div class="page">
    <div class="hero">
      <div class="left">
        <h1 class="h1">在线预约体育场馆</h1>
        <div class="p">选择场馆与时间段，几步即可完成预约。</div>
        <div class="actions">
          <el-button class="green-btn" type="primary" size="large" @click="go('/venues')">立即预约</el-button>
          <el-button size="large" @click="go('/order')">查看我的预约</el-button>
        </div>
      </div>

      <!-- 轮播图片 -->
      <div class="right">
        <el-carousel v-if="bannerVenues.length" height="280px" indicator-position="outside">
          <el-carousel-item v-for="v in bannerVenues" :key="v.id">
            <img class="img" :src="v.avatar" :alt="v.venueName" />
          </el-carousel-item>
        </el-carousel>
        <div v-else class="img-placeholder">暂无场馆图片</div>
      </div>

    </div>

    <div class="section">
      <div class="section-head">
        <h2>推荐场馆</h2>
        <router-link to="/venues" class="more">查看全部</router-link>
      </div>

      <div class="grid" v-loading="loading">
        <div v-for="v in venues" :key="v.id" class="card" @click="goDetail(v.id)">
          <img class="cover" :src="v.avatar" :alt="v.name" />
          <div class="body">
            <div class="row">
              <div class="name">{{ v.venueName }}</div>
              <div class="price">容量:{{ v.capacity }}/人数</div>
            </div>
            <div class="meta">
              <span class="type">{{ v.venueType }}</span>
              <span class="open">营业：{{ v.openTime }}</span>
            </div>
            <div class="addr">{{ v.location }}</div>
          </div>
        </div>
      </div>
    </div>

    <!--  AI 客服小球  -->
    <AiAssistant />
  </div>
</template>

<script setup>
import { onMounted, ref,computed  } from 'vue'
import { useRouter } from 'vue-router'
import { GetAllVenue, RecordVenueVisit } from '@/api/venues'
import AiAssistant from '@/components/AiAssistant.vue'

const router = useRouter()
const go = path => router.push(path)

//首页无须的对象,放着为空
const queryDto = ref({
  venueType: "",
  venueName: "",
})

const goDetail = async (id) => {
  try {
    await RecordVenueVisit(id)
  } catch (error) {
    console.error('记录场馆访问次数失败:', error)
  }
  router.push(`/venues/court/${id}`)
}

const loading = ref(false)
const venues = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await GetAllVenue(queryDto.value)
    //只展示前6条
    venues.value = res.data.allVenue.slice(0,6)
  } finally {
    loading.value = false
  }
})

//加载轮播图片
const bannerVenues = computed(() => (venues.value || []).slice(0, 5))

</script>

<style scoped lang="scss">
.page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 18px;
}
.hero {
  background: #fff;
  border: 1px solid #e5e5e5;
  padding: 40px;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 40px;
  margin-bottom: 40px;
}
.left {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.h1 {
  margin: 0;
  font-size: 42px;
  font-weight: 700;
  letter-spacing: -1px;
  color: #1a1a1a;
  line-height: 1.2;
}
.p {
  margin-top: 16px;
  color: #666;
  font-size: 16px;
  line-height: 1.6;
}
.actions {
  margin-top: 28px;
  display: flex;
  gap: 12px;
}
:deep(.el-button--primary) {
  background: #1a1a1a;
  border-color: #1a1a1a;

  &:hover {
    background: #333 !important;
    border-color: #333 !important;
  }
}

:deep(.green-btn) {
  background: #52c41a !important;
  border-color: #52c41a !important;

  &:hover {
    background: #73d13d !important;
    border-color: #73d13d !important;
  }
}
:deep(.el-button--default) {
  border-color: #1a1a1a;
  color: #1a1a1a;

  &:hover {
    background: #1a1a1a !important;
    border-color: #1a1a1a !important;
    color: #fff !important;
  }
}
.right {
  border: 1px solid #e5e5e5;
}
.img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: grayscale(20%);
}
.img-placeholder {
  width: 100%;
  height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  background: #f5f5f5;
}
.section {
  margin-bottom: 40px;
}
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  margin-bottom: 24px;
}
.section-head h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: -0.5px;
}
.more {
  text-decoration: none;
  color: #666;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 16px;
  border: 1px solid #e5e5e5;
  transition: all 0.2s ease;

  &:hover {
    color: #1a1a1a;
    border-color: #1a1a1a;
  }
}
.grid {
  margin-top: 0;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.card {
  background: #fff;
  border: 1px solid #e5e5e5;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s ease;
}
.card:hover {
  transform: translateY(-4px);
  border-color: #1a1a1a;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
}
.cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  filter: grayscale(20%);
}
.body {
  padding: 20px;
}
.row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: baseline;
}
.name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}
.price {
  color: #1a1a1a;
  font-weight: 600;
  font-size: 14px;
}
.meta {
  margin-top: 12px;
  display: flex;
  gap: 12px;
  color: #666;
  font-size: 13px;
}
.type {
  background: #f5f5f5;
  color: #1a1a1a;
  padding: 4px 10px;
  font-weight: 500;
}
.addr {
  margin-top: 12px;
  color: #888;
  font-size: 13px;
}
</style>

