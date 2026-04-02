<template>
  <div class="page">
    <div class="hero">
      <div class="left">
        <h1 class="h1">在线预约体育场馆</h1>
        <div class="p">选择场馆与时间段，几步即可完成预约。</div>
        <div class="actions">
          <el-button type="primary" size="large" @click="go('/venues')">立即预约</el-button>
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
  border-radius: 14px;
  padding: 18px;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 18px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}
.h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 900;
}
.p {
  margin-top: 10px;
  color: #606266;
}
.actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}
.img {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  object-fit: cover;
}
.section {
  margin-top: 16px;
}
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2px;
}
.more {
  text-decoration: none;
  color: #409eff;
  font-size: 13px;
}
.grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}
.card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}
.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.12);
}
.cover {
  width: 100%;
  height: 170px;
  object-fit: cover;
}
.body {
  padding: 12px 12px 14px;
}
.row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: baseline;
}
.name {
  font-size: 16px;
  font-weight: 800;
  color: #1f2d3d;
}
.price {
  color: #f56c6c;
  font-weight: 900;
}
.meta {
  margin-top: 8px;
  display: flex;
  gap: 10px;
  color: #909399;
  font-size: 12px;
}
.type {
  background: #ecf5ff;
  color: #409eff;
  padding: 1px 8px;
  border-radius: 999px;
}
.addr {
  margin-top: 8px;
  color: #606266;
  font-size: 12px;
}
</style>

