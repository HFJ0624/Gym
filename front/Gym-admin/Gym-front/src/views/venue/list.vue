<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="queryDto.venueName" placeholder="搜索场馆名称" clearable class="kw" @keyup.enter="load">
        <template #append>
          <el-button @click="load">搜索</el-button>
        </template>
      </el-input>
      <el-select v-model="queryDto.venueType" clearable placeholder="类型" class="type" @change="load">
        <el-option v-for="t in typeOptions" :key="t" :label="t" :value="t" />
      </el-select>
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
            <span class="tag">{{ v.venueType }}</span>
            <span>营业：{{ v.openTime }}</span>
          </div>
          <div class="addr">{{ v.location }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { GetAllVenue, RecordVenueVisit } from '@/api/venues'

const router = useRouter()
const goDetail = async (id) => {
  try {
    await RecordVenueVisit(id)
  } catch (error) {
    console.error('记录场馆访问次数失败:', error)
  }
  router.push(`/venues/court/${id}`)
}

const loading = ref(false)
const queryDto = ref({
  venueType: "",
  venueName: "",
})
const type = ref('')
const venues = ref([])

const typeOptions = computed(() => Array.from(new Set(venues.value.map(v => v.venueType))))

//展示列表方法
const load = async () => {
  loading.value = true
  try {
    const res = await GetAllVenue(queryDto.value)
    venues.value = res.data.allVenue
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped lang="scss">
.page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 18px;
}
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
  align-items: center;
}
.kw {
  flex: 1;
}
.type {
  width: 180px;
}
.grid {
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
.tag {
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
.tags {
  margin-top: 10px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
</style>

