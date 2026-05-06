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
        <div class="booking-title">
          <h3>场地预约</h3>
          <p>请选择预约日期，然后点击一个可预约时段，系统会自动填充开始时间和结束时间。</p>
        </div>

        <el-form :model="bookingForm" label-width="90px">
          <!-- 预约日期 -->
          <el-form-item label="预约日期" required>
            <el-date-picker
              v-model="bookingForm.bookingDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%;"
              :disabled-date="disabledPastDate"
              @change="handleDateChange"
            />
          </el-form-item>

          <!-- 可预约时段 -->
          <el-form-item label="预约时段" required>
            <div class="slot-panel" v-loading="slotLoading">
              <div
                v-if="slotList.length === 0 && !slotLoading"
                class="empty-slot"
              >
                暂无可预约时段
              </div>

              <div
                v-for="slot in slotList"
                :key="slot.startTime + '-' + slot.endTime"
                class="slot-item"
                :class="{
                  available: slot.available,
                  disabled: !slot.available,
                  selected: isSelectedSlot(slot)
                }"
                @click="selectSlot(slot)"
              >
                <div class="slot-time">
                  {{ slot.label }}
                </div>

                <div class="slot-status">
                  {{ slot.available ? '可预约' : slot.reason }}
                </div>
              </div>
            </div>
          </el-form-item>

          <!-- 已选时间，只读展示 -->
          <el-form-item label="已选时间">
            <el-input
              :model-value="selectedTimeText"
              readonly
            />
          </el-form-item>

          <!-- 预计金额，只读展示 -->
          <el-form-item label="预计金额">
            <el-input
              :model-value="estimatedAmountText"
              readonly
            />
          </el-form-item>

          <!-- 备注 -->
          <el-form-item label="备注">
            <el-input
              v-model="bookingForm.remark"
              type="textarea"
              placeholder="请输入备注信息"
              :rows="3"
            />
          </el-form-item>

          <!-- 提交 -->
          <el-form-item>
            <el-button
              type="primary"
              :loading="submitLoading"
              @click="submitBooking"
            >
              提交预约
            </el-button>

            <el-button @click="resetSelection">
              重置选择
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>

  <!-- 人工智能助手 -->
  <AiAssistant
    :venue-id="currentVenueId"
    :court-id="currentCourtId"
  />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

import { GetCourtDetail, BookCourt,getAvailableSlots } from '@/api/court'
import { useAuth } from '@/stores/auth'
import AiAssistant from '@/components/AiAssistant.vue'

const router = useRouter()
const route = useRoute()
const auth = useAuth()

const loading = ref(false)
const slotLoading = ref(false)
const submitLoading = ref(false)

const venueId = route.query.venueId

/**
 * 当前登录用户ID。
 */
const userId = computed(() => auth.user?.id)

/**
 * 当前页面场馆ID。
 */
const currentVenueId = computed(() => {
  return route.query.venueId ? Number(route.query.venueId) : null
})

/**
 * 当前页面场地ID。
 */
const currentCourtId = computed(() => {
  return route.params.id ? Number(route.params.id) : null
})

/**
 * 场地详情信息。
 */
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

/**
 * 预约表单数据。
 *
 * 现在 startTime / endTime 不再手动选择，
 * 而是用户点击可预约时段后自动填充。
 */
const bookingForm = ref({
  bookingDate: formatDate(new Date()),
  startTime: '',
  endTime: '',
  remark: ''
})

/**
 * 后端返回的可预约时段列表。
 */
const slotList = ref([])

/**
 * 当前选中的时段。
 */
const selectedSlot = ref(null)

/**
 * 已选时间展示文本。
 */
const selectedTimeText = computed(() => {
  if (!bookingForm.value.startTime || !bookingForm.value.endTime) {
    return '请选择可预约时段'
  }

  return `${bookingForm.value.startTime} - ${bookingForm.value.endTime}`
})

/**
 * 预计金额。
 */
const estimatedAmountText = computed(() => {
  if (!selectedSlot.value || !courtInfo.value.price) {
    return '请选择时段后自动计算'
  }

  const hours = calcHours(selectedSlot.value.startTime, selectedSlot.value.endTime)
  const total = Number(courtInfo.value.price) * hours

  return `¥${total.toFixed(2)}`
})

/**
 * 从后端获取场地详情。
 */
const loadData = async () => {
  loading.value = true

  try {
    const courtId = route.params.id
    const response = await GetCourtDetail(courtId, venueId)

    /**
     * 保留你原来的数据结构：
     * response.data.court[0]
     */
    if (response?.data?.court && response.data.court.length > 0) {
      courtInfo.value = response.data.court[0]
    } else {
      ElMessage.warning('未找到场地详情')
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载场地详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载某个日期下的可预约时段。
 */
const loadAvailableSlots = async () => {
  if (!currentVenueId.value || !currentCourtId.value || !bookingForm.value.bookingDate) {
    return
  }

  slotLoading.value = true

  /**
   * 切换日期后，清空旧时段选择。
   */
  selectedSlot.value = null
  bookingForm.value.startTime = ''
  bookingForm.value.endTime = ''

  try {
    const response = await getAvailableSlots({
      venueId: currentVenueId.value,
      courtId: currentCourtId.value,
      date: bookingForm.value.bookingDate
    })

    const data = unwrapData(response)

    slotList.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('加载可预约时段失败:', error)
    ElMessage.error('加载可预约时段失败')
  } finally {
    slotLoading.value = false
  }
}

/**
 * 日期改变后重新加载时段。
 */
const handleDateChange = () => {
  loadAvailableSlots()
}

/**
 * 选择一个可预约时段。
 */
const selectSlot = slot => {
  if (!slot.available) {
    return
  }

  selectedSlot.value = slot

  bookingForm.value.startTime = normalizeTime(slot.startTime)
  bookingForm.value.endTime = normalizeTime(slot.endTime)
}

/**
 * 判断时段是否选中。
 */
const isSelectedSlot = slot => {
  if (!selectedSlot.value) {
    return false
  }

  return selectedSlot.value.startTime === slot.startTime
    && selectedSlot.value.endTime === slot.endTime
}

/**
 * 重置选择。
 */
const resetSelection = () => {
  selectedSlot.value = null
  bookingForm.value.startTime = ''
  bookingForm.value.endTime = ''
}

/**
 * 提交预约。
 */
const submitBooking = async () => {
  if (!userId.value) {
    ElMessage.warning('请先登录后再预约')
    return
  }

  if (!bookingForm.value.bookingDate) {
    ElMessage.warning('请选择预约日期')
    return
  }

  if (!bookingForm.value.startTime || !bookingForm.value.endTime) {
    ElMessage.warning('请先选择可预约时段')
    return
  }

  submitLoading.value = true

  try {
    const formatBookingDate = normalizeDate(bookingForm.value.bookingDate)

    const bookingData = {
      courtId: courtInfo.value.id,
      hoursPrice: courtInfo.value.price,
      startTime: bookingForm.value.startTime,
      endTime: bookingForm.value.endTime,
      bookingDate: formatBookingDate,
      remark: bookingForm.value.remark,
      userId: userId.value
    }

    const result = await BookCourt(bookingData)
    const code = result?.code ?? result?.data?.code

    if (code === 200) {
      ElMessage.success('预约成功')

      /**
       * 预约成功后重新加载时段，
       * 这样刚刚预约的时段会变成“已被预约”。
       */
      await loadAvailableSlots()

      /**
       * 保留你原来的逻辑：
       * 预约成功后跳转到我的预约页面。
       */
      router.push('/order')
    } else {
      ElMessage.error('预约失败，时段冲突或余额不足')
    }
  } catch (error) {
    console.error('预约失败:', error)
    ElMessage.error('预约失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

/**
 * 禁用过去日期。
 */
const disabledPastDate = time => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  return time.getTime() < today.getTime()
}

/**
 * 兼容不同 request 封装返回。
 */
function unwrapData(response) {
  if (!response) {
    return null
  }

  /**
   * 如果 request 拦截器已经返回 response.data：
   * { code: 200, data: [...] }
   */
  if (response.code !== undefined && response.data !== undefined) {
    return response.data
  }

  /**
   * 如果是原始 axios response：
   * { data: { code: 200, data: [...] } }
   */
  if (response.data && response.data.code !== undefined && response.data.data !== undefined) {
    return response.data.data
  }

  if (response.data !== undefined) {
    return response.data
  }

  return response
}

/**
 * 格式化日期为 yyyy-MM-dd。
 */
function formatDate(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}-${month}-${day}`
}

/**
 * 日期标准化。
 *
 * Element Plus 如果使用 value-format="YYYY-MM-DD"，
 * bookingDate 本身就是字符串。
 *
 * 这里保留兼容，防止后续改回 Date 对象。
 */
function normalizeDate(value) {
  if (!value) {
    return ''
  }

  if (typeof value === 'string') {
    return value
  }

  const date = new Date(value)
  return formatDate(date)
}

/**
 * 时间标准化。
 *
 * 后端时段接口有时可能返回 08:00，
 * 提交预约时统一转成 08:00:00。
 */
function normalizeTime(value) {
  if (!value) {
    return ''
  }

  if (value.length === 5) {
    return `${value}:00`
  }

  return value
}

/**
 * 计算两个时间之间的小时数。
 */
function calcHours(startTime, endTime) {
  const start = normalizeTime(startTime)
  const end = normalizeTime(endTime)

  const [sh, sm, ss] = start.split(':').map(Number)
  const [eh, em, es] = end.split(':').map(Number)

  const startSeconds = sh * 3600 + sm * 60 + (ss || 0)
  const endSeconds = eh * 3600 + em * 60 + (es || 0)

  return Math.max((endSeconds - startSeconds) / 3600, 0)
}

onMounted(async () => {
  await loadData()
  await loadAvailableSlots()
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

.booking-title {
  margin-bottom: 24px;
}

.booking-title h3 {
  margin: 0 0 8px;
  font-size: 22px;
  color: #1a1a1a;
}

.booking-title p {
  margin: 0;
  font-size: 14px;
  color: #777;
}

.booking-form .el-form-item {
  margin-bottom: 24px;
}

/**
 * 时段面板。
 */
.slot-panel {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  width: 100%;
  min-height: 90px;
}

/**
 * 单个时段。
 */
.slot-item {
  padding: 12px 8px;
  border-radius: 0;
  border: 1px solid #dcdfe6;
  text-align: center;
  cursor: pointer;
  background: #fff;
  transition: all 0.2s;
  user-select: none;
}

.slot-item.available:hover {
  border-color: #1a1a1a;
  color: #1a1a1a;
  transform: translateY(-1px);
}

.slot-item.selected {
  background: #1a1a1a;
  border-color: #1a1a1a;
  color: #fff;
}

.slot-item.disabled {
  background: #f5f5f5;
  color: #bbb;
  cursor: not-allowed;
}

.slot-item.disabled:hover {
  transform: none;
}

.slot-time {
  font-weight: 600;
  font-size: 14px;
}

.slot-status {
  margin-top: 4px;
  font-size: 12px;
}

.empty-slot {
  grid-column: 1 / -1;
  color: #999;
  text-align: center;
  padding: 24px 0;
  background: #fff;
  border: 1px dashed #ddd;
}

@media (max-width: 768px) {
  .slot-panel {
    grid-template-columns: repeat(2, 1fr);
  }

  .detail-container {
    padding: 20px;
  }

  .booking-form {
    padding: 20px;
  }
}
</style>