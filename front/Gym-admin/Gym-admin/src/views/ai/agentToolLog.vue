<template>
  <div class="agent-tool-log-page">
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h2>Agent 工具调用日志</h2>
            <p>
              用于查看智能助手每次调用了什么工具、传入了什么参数、返回了什么结果，以及是否发生异常。
            </p>
          </div>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form
        :model="queryForm"
        label-width="90px"
        class="query-form"
      >
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="用户ID">
              <el-input-number
                v-model="queryForm.userId"
                :min="1"
                :controls="false"
                placeholder="请输入用户ID"
                style="width: 100%"
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
            <el-form-item label="TraceID">
              <el-input
                v-model="queryForm.traceId"
                placeholder="一次对话追踪ID"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="用户问题">
              <el-input
                v-model="queryForm.userMessage"
                placeholder="按用户原始问题模糊查询"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                查询
              </el-button>

              <el-button @click="handleReset">
                重置
              </el-button>
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
        style="width: 100%"
      >
        <el-table-column
          prop="id"
          label="ID"
          width="80"
          align="center"
        />

        <el-table-column
          prop="userId"
          label="用户ID"
          width="90"
          align="center"
        />

        <el-table-column
          prop="toolName"
          label="工具名称"
          min-width="180"
          show-overflow-tooltip
        />

        <el-table-column
          prop="status"
          label="状态"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              v-if="row.status === 'SUCCESS'"
              type="success"
            >
              成功
            </el-tag>

            <el-tag
              v-else
              type="danger"
            >
              失败
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="durationMs"
          label="耗时"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            {{ row.durationMs }} ms
          </template>
        </el-table-column>

        <el-table-column
          prop="userMessage"
          label="用户问题"
          min-width="240"
          show-overflow-tooltip
        />

        <el-table-column
          prop="traceId"
          label="TraceID"
          min-width="180"
          show-overflow-tooltip
        />

        <el-table-column
          label="创建时间"
          width="180"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column
          label="操作"
          width="120"
          fixed="right"
          align="center"
        >
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="openDetail(row)"
            >
              查看详情
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
      width="960px"
    >
      <el-descriptions
        :column="2"
        border
      >
        <el-descriptions-item label="日志ID">
          {{ detail.id }}
        </el-descriptions-item>

        <el-descriptions-item label="用户ID">
          {{ detail.userId }}
        </el-descriptions-item>

        <el-descriptions-item label="工具名称">
          {{ detail.toolName }}
        </el-descriptions-item>

        <el-descriptions-item label="调用状态">
          <el-tag
            v-if="detail.status === 'SUCCESS'"
            type="success"
          >
            成功
          </el-tag>

          <el-tag
            v-else
            type="danger"
          >
            失败
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="耗时">
          {{ detail.durationMs }} ms
        </el-descriptions-item>

        <el-descriptions-item label="创建时间">
          {{ formatDateTime(detail.createTime) }}
        </el-descriptions-item>

        <el-descriptions-item label="TraceID" :span="2">
          {{ detail.traceId || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="工具类" :span="2">
          {{ detail.toolClass || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="方法名" :span="2">
          {{ detail.methodName || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="工具描述" :span="2">
          {{ detail.toolDesc || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="用户问题" :span="2">
          {{ detail.userMessage || '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="detail-section">
        <div class="section-title">工具入参 JSON</div>
        <pre>{{ formatJson(detail.argumentsJson) }}</pre>
      </div>

      <div class="detail-section">
        <div class="section-title">工具返回内容</div>
        <pre>{{ detail.resultText || '-' }}</pre>
      </div>

      <div
        v-if="detail.errorMessage"
        class="detail-section"
      >
        <div class="section-title error">异常信息</div>
        <pre>{{ detail.errorMessage }}</pre>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { pageAgentToolLog, getAgentToolLogDetail } from '@/api/agentToolLog'

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
  userMessage: ''
})

/**
 * 表格数据。
 */
const tableData = ref([])

/**
 * 总数。
 */
const total = ref(0)

/**
 * 加载状态。
 */
const loading = ref(false)

/**
 * 详情弹窗状态。
 */
const detailVisible = ref(false)

/**
 * 当前详情。
 */
const detail = reactive({})

onMounted(() => {
  loadData()
})

/**
 * 加载日志列表。
 */
const loadData = async () => {
  loading.value = true

  try {
    const res = await pageAgentToolLog(cleanQueryParams(queryForm))
    const data = unwrapData(res)

    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

/**
 * 查询。
 */
const handleSearch = () => {
  queryForm.pageNum = 1
  loadData()
}

/**
 * 重置查询条件。
 */
const handleReset = () => {
  queryForm.pageNum = 1
  queryForm.pageSize = 10
  queryForm.userId = undefined
  queryForm.traceId = ''
  queryForm.toolName = ''
  queryForm.status = ''
  queryForm.userMessage = ''

  loadData()
}

/**
 * 打开详情弹窗。
 */
const openDetail = async row => {
  const res = await getAgentToolLogDetail(row.id)
  const data = unwrapData(res)

  Object.keys(detail).forEach(key => delete detail[key])
  Object.assign(detail, data)

  detailVisible.value = true
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
</script>

<style scoped>
.agent-tool-log-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%;
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
</style>