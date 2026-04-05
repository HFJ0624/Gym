<template>
  <div class="page">
    <div class="head">
      <h2 class="title">我的预约</h2>
      <el-button @click="load" :loading="loading">刷新</el-button>
    </div>

    <el-card v-for="o in orders" :key="o.id" class="card" shadow="never">
      <div class="row">
        <div class="left">
          <div class="name">{{ o.courtName }}</div>
          <div class="sub">
            <span class="badge" :class="badgeClass(o.status)">{{ statusText(o.status) }}</span>
            <span>{{ o.bookingDate }}</span>
            <span class="price">¥{{ o.totalPrice }}</span>
          </div>
          <div class="addr">场地类型：{{ o.type }}</div>
          <div class="contact">订单编号：{{ o.orderNo }}</div>
          <div v-if="o.remark" class="remark">备注：{{ o.remark }}</div>
          <div class="time">下单时间：{{ formatTime(o.createTime) }}</div>
        </div>

        <div class="right">
          <el-button
            v-if="o.status === 1 && canCancel(o.bookingDate)"
            type="danger"
            plain
            :loading="cancelingId === o.id"
            @click="onCancel(o)"
          >
            取消预约
          </el-button>
          <el-button
            v-else-if="o.status ===1"
            type="info"
            plain
            disabled
          >
            已过取消时间
          </el-button>
        </div>
      </div>
    </el-card>

    <el-empty v-if="!loading && orders.length === 0" description="你还没有预约记录，去逛逛场馆吧">
      <el-button type="primary" @click="goVenue">去预约</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getCourtOrder } from '@/api/orders'
// 从Pinia store中获取用户信息
import { useAuth } from '@/stores/auth'

const router = useRouter()

const loading = ref(false)
const orders = ref([])
const cancelingId = ref('')
const auth = useAuth()
const userId = auth.user?.id
console.log(userId)

const formatTime = iso => {
  if (!iso) return '--'
  const d = new Date(iso)
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const hh = String(d.getHours()).padStart(2, '0')
  const mi = String(d.getMinutes()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}`
}

const statusText = status => {
  if (status === 0) return '待支付'
  if (status === 1) return '已支付'
  if (status === 2) return '已取消'
  if (status === 3) return '已完成'
  return '未知状态'
}

const badgeClass = status => {
  if (status === 0) return 'warning'
  if (status === 1) return 'ok'
  if (status === 2) return 'cancel'
  if (status === 3) return 'success'
  return 'normal'
}

// 检查是否可以取消预约（预约日期未过期）
const canCancel = bookingDate => {
  const today = new Date().toISOString().split('T')[0]
  return bookingDate >= today
}

const load = async () => {
  loading.value = true
  try {
    const res = await getCourtOrder(userId)
    orders.value = res.data.orders
  } finally {
    loading.value = false
  }
}

const onCancel = async o => {
  await ElMessageBox.confirm('确认取消该预约吗？取消后不可恢复。', '提示', { type: 'warning' })
  cancelingId.value = o.id
  try {
    await cancelOrder(o.id)
    ElMessage.success('已取消')
    await load()
  } finally {
    cancelingId.value = ''
  }
}

const goVenue = () => router.push('/venues')

onMounted(load)
</script>

<style scoped lang="scss">
.page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 18px;
}
:deep(.el-card) {
  border: 1px solid #e5e5e5;
  border-radius: 0;
  margin-bottom: 16px;
}
:deep(.el-button--danger) {
  background: #1a1a1a;
  border-color: #1a1a1a;
  border-radius: 0;
  color: #fff;
}
:deep(.el-button--info) {
  border-radius: 0;
}
:deep(.el-button--default) {
  border-color: #1a1a1a;
  color: #1a1a1a;
  border-radius: 0;
}
:deep(.el-button--primary) {
  background: #1a1a1a;
  border-color: #1a1a1a;
  border-radius: 0;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
}
.card {
  margin-bottom: 16px;
}
.row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 20px;
  align-items: start;
}
.name {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}
.sub {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #666;
  font-size: 14px;
}
.price {
  color: #1a1a1a;
  font-weight: 600;
}
.addr {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}
.contact,
.remark,
.time {
  margin-top: 8px;
  color: #888;
  font-size: 13px;
}
.badge {
  padding: 4px 12px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid;
}
.badge.warning {
  background: #fef0f0;
  color: #f56c6c;
  border-color: #fde2e2;
}
.badge.ok {
  background: #f0f9eb;
  color: #67c23a;
  border-color: #e1f3d8;
}
.badge.success {
  background: #f0f9eb;
  color: #67c23a;
  border-color: #e1f3d8;
}
.badge.cancel {
  background: #fff;
  color: #1a1a1a;
  border-color: #1a1a1a;
}
</style>

