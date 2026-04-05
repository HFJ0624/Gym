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
        <div class="cover-container">
          <img class="cover" :src="v.avatar" :alt="v.name" />
          <div class="star-btn" @click.stop="toggleCollect(v)">
            <el-icon v-if="isCollected(v.id)" class="star-icon collected" :size="24">
              <StarFilled />
            </el-icon>
            <el-icon v-else class="star-icon" :size="24">
              <Star />
            </el-icon>
          </div>
        </div>
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
import { ElMessage } from 'element-plus'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { GetAllVenue, RecordVenueVisit, GetUserCollectedVenues, CollectVenue, UncollectVenue } from '@/api/venues'

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
const collectedVenueIds = ref(new Set())

const typeOptions = computed(() => Array.from(new Set(venues.value.map(v => v.venueType))))

const isCollected = (venueId) => {
  return collectedVenueIds.value.has(venueId)
}

const toggleCollect = async (venue) => {
  try {
    if (isCollected(venue.id)) {
      await UncollectVenue(venue.id)
      collectedVenueIds.value.delete(venue.id)
      ElMessage.success('取消收藏成功')
    } else {
      await CollectVenue(venue.id)
      collectedVenueIds.value.add(venue.id)
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const loadCollectedVenues = async () => {
  try {
    const res = await GetUserCollectedVenues()
    if (res.data && res.data.venueCollectList) {
      collectedVenueIds.value = new Set(res.data.venueCollectList.map(item => item.venueId))
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  }
}

const load = async () => {
  loading.value = true
  try {
    const res = await GetAllVenue(queryDto.value)
    venues.value = res.data.allVenue
    await loadCollectedVenues()
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
  margin-bottom: 32px;
  align-items: center;
}
.kw {
  flex: 1;
}
.type {
  width: 180px;
}
:deep(.el-input__wrapper) {
  border-radius: 0;
  box-shadow: 0 0 0 1px #1a1a1a inset;
}
:deep(.el-button--default) {
  border-color: #1a1a1a;
  color: #1a1a1a;
  border-radius: 0;
}
:deep(.el-select .el-input__wrapper) {
  border-radius: 0;
}
.grid {
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
  position: relative;
}
.card:hover {
  transform: translateY(-4px);
  border-color: #1a1a1a;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
}
.cover-container {
  position: relative;
}
.cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  filter: grayscale(20%);
}
.star-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #fff;
  border: 1px solid #e5e5e5;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  z-index: 10;
  &:hover {
    border-color: #1a1a1a;
  }
}
.star-icon {
  color: #ccc;
  &.collected {
    color: #f7ba2a;
  }
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
}
.meta {
  margin-top: 12px;
  display: flex;
  gap: 12px;
  color: #666;
  font-size: 13px;
}
.tag {
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
.tags {
  margin-top: 10px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
</style>

