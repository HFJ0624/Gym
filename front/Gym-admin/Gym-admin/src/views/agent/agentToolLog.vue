<template>
  <div class="agent-tool-log-page">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="4">
        <el-card shadow="never" class="stats-card">
          <div class="stats-label">总调用数</div>
          <div class="stats-value">{{ stats.totalCount || 0 }}</div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="never" class="stats-card success">
          <div class="stats-label">成功调用</div>
          <div class="stats-value">{{ stats.successCount || 0 }}</div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="never" class="stats-card danger">
          <div class="stats-label">失败调用</div>
          <div class="stats-value">{{ stats.failCount || 0 }}</div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="never" class="stats-card warning">
          <div class="stats-label">慢调用</div>
          <div class="stats-value">{{ stats.slowCount || 0 }}</div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="never" class="stats-card">
          <div class="stats-label">Trace 数</div>
          <div class="stats-value">{{ stats.traceCount || 0 }}</div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="never" class="stats-card">
          <div class="stats-label">平均耗时</div>
          <div class="stats-value">{{ stats.avgDurationMs || 0 }} ms</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查询条件 -->
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h2>Agent 工具调用日志</h2>
            <p>查看智能助手每次调用的工具、入参、返回值、耗时和异常。</p>
          </div>
        </div>
      </template>

      <el-form :model="queryForm" label-width="95px" class="query-form">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="用户ID">
              <el-input-number
                v-model="queryForm.userId"
                :min="1"
                :controls="false"
                placeholder="请输入用户ID"
                style="width: 100%"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="工具名称">
              <el-input
                v-model="queryForm.toolName"
                placeholder="例如 askGymKnowledge"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="调用状态">
              <el-select
                v-model="queryForm.status"
                placeholder="全部"
                clearable
                style="width: 100%"
              >
                <el-option label="成功" value="SUCCESS" />
                <el-option label="失败" value="FAIL" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="慢调用阈值">
              <el-input-number
                v-model="queryForm.slowThresholdMs"
                :min="100"
                :step="500"
                :controls="false"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="TraceID">
              <el-input
                v-model="queryForm.traceId"
                placeholder="一次对话追踪ID"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="10">
            <el-form-item label="用户问题">
              <el-input
                v-model="queryForm.userMessage"
                placeholder="按用户原始问题模糊查询"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card class="page-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        row-key="id"
        :row-class-name="getRowClassName"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <el-table-column prop="userId" label="用户ID" width="90" align="center" />

        <el-table-column prop="toolName" label="工具名称" min-width="190" show-overflow-tooltip />

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'SUCCESS'" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="durationMs" label="耗时" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              v-if="isSlow(row)"
              type="warning"
            >
              {{ row.durationMs }} ms
            </el-tag>

            <span v-else>
              {{ row.durationMs }} ms
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="userMessage" label="用户问题" min-width="240" show-overflow-tooltip />

        <el-table-column prop="traceId" label="TraceID" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <el-button
              v-if="row.traceId"
              type="primary"
              link
              @click="openTraceChain(row.traceId)"
            >
              {{ row.traceId }}
            </el-button>

            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="190" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">
              详情
            </el-button>

            <el-button type="success" link @click="copyText(row.traceId)">
              复制Trace
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-box">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="Agent 工具调用详情"
      width="980px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detail.userId }}</el-descriptions-item>
        <el-descriptions-item label="工具名称">{{ detail.toolName }}</el-descriptions-item>

        <el-descriptions-item label="调用状态">
          <el-tag v-if="detail.status === 'SUCCESS'" type="success">成功</el-tag>
          <el-tag v-else type="danger">失败</el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="耗时">
          <el-tag v-if="isSlow(detail)" type="warning">
            {{ detail.durationMs }} ms 慢调用
          </el-tag>
          <span v-else>{{ detail.durationMs }} ms</span>
        </el-descriptions-item>

        <el-descriptions-item label="创建时间">
          {{ formatDateTime(detail.createTime) }}
        </el-descriptions-item>

        <el-descriptions-item label="TraceID" :span="2">
          <div class="copy-line">
            <span>{{ detail.traceId || '-' }}</span>
            <el-button
              v-if="detail.traceId"
              type="primary"
              link
              @click="copyText(detail.traceId)"
            >
              复制
            </el-button>
          </div>
        </el-descriptions-item>

        <el-descriptions-item label="工具类" :span="2">{{ detail.toolClass || '-' }}</el-descriptions-item>
        <el-descriptions-item label="方法名" :span="2">{{ detail.methodName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工具描述" :span="2">{{ detail.toolDesc || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户问题" :span="2">{{ detail.userMessage || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="detail-section">
        <div class="section-title">
          工具入参 JSON
          <el-button type="primary" link @click="copyText(detail.argumentsJson)">复制</el-button>
        </div>
        <pre>{{ formatJson(detail.argumentsJson) }}</pre>
      </div>

      <div class="detail-section">
        <div class="section-title">
          工具返回内容
          <el-button type="primary" link @click="copyText(detail.resultText)">复制</el-button>
        </div>
        <pre>{{ formatMaybeJson(detail.resultText) }}</pre>
      </div>

      <div v-if="detail.errorMessage" class="detail-section">
        <div class="section-title error">
          异常信息
          <el-button type="danger" link @click="copyText(detail.errorMessage)">复制</el-button>
        </div>
        <pre class="error-pre">{{ detail.errorMessage }}</pre>
      </div>
    </el-dialog>

    <!-- Trace 调用链抽屉 -->
    <el-drawer
      v-model="traceDrawerVisible"
      title="Trace 调用链"
      size="70%"
    >
      <div class="trace-header">
        <div>
          <span class="trace-label">TraceID：</span>
          <span>{{ currentTraceId }}</span>
        </div>

        <el-button type="primary" link @click="copyText(currentTraceId)">
          复制 TraceID
        </el-button>
      </div>

      <el-table
        v-loading="traceLoading"
        :data="traceList"
        border
        row-key="id"
        :row-class-name="getRowClassName"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <el-table-column prop="toolName" label="工具名称" min-width="190" show-overflow-tooltip />

        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'SUCCESS'" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="durationMs" label="耗时" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="isSlow(row)" type="warning">
              {{ row.durationMs }} ms
            </el-tag>
            <span v-else>{{ row.durationMs }} ms</span>
          </template>
        </el-table-column>

        <el-table-column prop="userMessage" label="用户问题" min-width="240" show-overflow-tooltip />

        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  pageAgentToolLog,
  getAgentToolLogDetail,
  getAgentToolLogStats
} from '@/api/agentToolLog'

/**
 * 查询条件。
 */
const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  userId: undefined,
  traceId: '',
  toolName: '',
  status: '',
  userMessage: '',
  slowThresholdMs: 3000
})

/**
 * 统计信息。
 */
const stats = reactive({
  totalCount: 0,
  successCount: 0,
  failCount: 0,
  slowCount: 0,
  traceCount: 0,
  avgDurationMs: 0
})

/**
 * 列表数据。
 */
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

/**
 * 详情。
 */
const detailVisible = ref(false)
const detail = reactive({})

/**
 * Trace 调用链。
 */
const traceDrawerVisible = ref(false)
const traceLoading = ref(false)
const currentTraceId = ref('')
const traceList = ref([])

onMounted(() => {
  loadData()
})

/**
 * 加载列表和统计。
 */
const loadData = async () => {
  loading.value = true

  try {
    await Promise.all([
      loadPage(),
      loadStats()
    ])
  } finally {
    loading.value = false
  }
}

/**
 * 加载分页数据。
 */
const loadPage = async () => {
  const res = await pageAgentToolLog(cleanQueryParams(queryForm))
  const data = unwrapData(res)

  tableData.value = data?.list || []
  total.value = data?.total || 0
}

/**
 * 加载统计数据。
 */
const loadStats = async () => {
  const res = await getAgentToolLogStats(cleanQueryParams(queryForm))
  const data = unwrapData(res) || {}

  stats.totalCount = data.totalCount || 0
  stats.successCount = data.successCount || 0
  stats.failCount = data.failCount || 0
  stats.slowCount = data.slowCount || 0
  stats.traceCount = data.traceCount || 0
  stats.avgDurationMs = data.avgDurationMs || 0
}

/**
 * 查询。
 */
const handleSearch = () => {
  queryForm.pageNum = 1
  loadData()
}

/**
 * 重置。
 */
const handleReset = () => {
  queryForm.pageNum = 1
  queryForm.pageSize = 10
  queryForm.userId = undefined
  queryForm.traceId = ''
  queryForm.toolName = ''
  queryForm.status = ''
  queryForm.userMessage = ''
  queryForm.slowThresholdMs = 3000

  loadData()
}

/**
 * 打开详情。
 */
const openDetail = async row => {
  const res = await getAgentToolLogDetail(row.id)
  const data = unwrapData(res)

  Object.keys(detail).forEach(key => delete detail[key])
  Object.assign(detail, data)

  detailVisible.value = true
}

/**
 * 打开 Trace 调用链。
 */
const openTraceChain = async traceId => {
  if (!traceId) {
    return
  }

  currentTraceId.value = traceId
  traceDrawerVisible.value = true
  traceLoading.value = true

  try {
    const res = await pageAgentToolLog({
      pageNum: 1,
      pageSize: 100,
      traceId,
      slowThresholdMs: queryForm.slowThresholdMs
    })

    const data = unwrapData(res)

    traceList.value = data?.list || []
  } finally {
    traceLoading.value = false
  }
}

/**
 * 是否慢调用。
 */
function isSlow(row) {
  if (!row || row.durationMs == null) {
    return false
  }

  return Number(row.durationMs) > Number(queryForm.slowThresholdMs || 3000)
}

/**
 * 表格行样式。
 */
function getRowClassName({ row }) {
  if (row.status === 'FAIL') {
    return 'fail-row'
  }

  if (isSlow(row)) {
    return 'slow-row'
  }

  return ''
}

/**
 * 清理查询参数。
 */
function cleanQueryParams(params) {
  const payload = {}

  Object.keys(params).forEach(key => {
    const value = params[key]

    if (value !== '' && value !== undefined && value !== null) {
      payload[key] = value
    }
  })

  return payload
}

/**
 * 兼容不同 request 封装。
 */
function unwrapData(res) {
  if (!res) {
    return null
  }

  if (res.data && res.data.data !== undefined) {
    return res.data.data
  }

  if (res.data !== undefined) {
    return res.data
  }

  return res
}

/**
 * 格式化 JSON。
 */
function formatJson(value) {
  if (!value) {
    return '-'
  }

  try {
    return JSON.stringify(JSON.parse(value), null, 2)
  } catch (e) {
    return value
  }
}

/**
 * 如果返回内容是 JSON，就格式化。
 * 如果不是 JSON，就原样展示。
 */
function formatMaybeJson(value) {
  if (!value) {
    return '-'
  }

  try {
    return JSON.stringify(JSON.parse(value), null, 2)
  } catch (e) {
    return value
  }
}

/**
 * 格式化时间。
 */
function formatDateTime(value) {
  if (!value) {
    return '-'
  }

  if (typeof value === 'string') {
    return value.replace('T', ' ')
  }

  try {
    const date = new Date(value)
    const year = date.getFullYear()
    const month = pad(date.getMonth() + 1)
    const day = pad(date.getDate())
    const hour = pad(date.getHours())
    const minute = pad(date.getMinutes())
    const second = pad(date.getSeconds())

    return `${year}-${month}-${day} ${hour}:${minute}:${second}`
  } catch (e) {
    return value
  }
}

/**
 * 补零。
 */
function pad(value) {
  return value < 10 ? `0${value}` : `${value}`
}

/**
 * 复制文本。
 */
async function copyText(text) {
  if (!text) {
    return ElMessage.warning('没有可复制内容')
  }

  try {
    await navigator.clipboard.writeText(String(text))
    ElMessage.success('已复制')
  } catch (e) {
    ElMessage.error('复制失败，请手动复制')
  }
}
</script>

<style scoped>
.agent-tool-log-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%;
}

.stats-row {
  margin-bottom: 16px;
}

.stats-card {
  border-radius: 10px;
}

.stats-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.stats-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.stats-card.success .stats-value {
  color: #67c23a;
}

.stats-card.danger .stats-value {
  color: #f56c6c;
}

.stats-card.warning .stats-value {
  color: #e6a23c;
}

.page-card {
  margin-bottom: 20px;
  border-radius: 10px;
}

.card-header h2 {
  margin: 0 0 8px;
  font-weight: 600;
  color: #303133;
}

.card-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.query-form {
  margin-bottom: -18px;
}

.pagination-box {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.detail-section {
  margin-top: 20px;
}

.section-title {
  margin-bottom: 8px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title.error {
  color: #f56c6c;
}

pre {
  margin: 0;
  padding: 14px;
  background: #f5f7fa;
  border-radius: 8px;
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  color: #303133;
  max-height: 360px;
  overflow-y: auto;
}

.error-pre {
  background: #fef0f0;
  color: #c45656;
}

.copy-line {
  display: flex;
  align-items: center;
  gap: 8px;
}

.trace-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px 14px;
  background: #f5f7fa;
  border-radius: 8px;
}

.trace-label {
  color: #909399;
  font-weight: 600;
}

:deep(.fail-row) {
  background-color: #fef0f0 !important;
}

:deep(.slow-row) {
  background-color: #fdf6ec !important;
}
</style>