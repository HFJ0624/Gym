<template>
  <div class="agent-trace-page">

    <!-- Trace 统计卡片 -->
    <el-row :gutter="12" class="stats-row">
      <el-col :span="6">
        <el-card shadow="never" class="stats-card">
          <div class="stats-label">总调用次数</div>
          <div class="stats-value">{{ stats.totalCount }}</div>
          <div>统计总次数</div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="never" class="stats-card success">
          <div class="stats-label">成功率</div>
          <div class="stats-value">{{ stats.successRate }}%</div>
          <div class="stats-sub">
            成功 {{ stats.successCount }} / 失败 {{ stats.failedCount }}
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="never" class="stats-card">
          <div class="stats-label">平均耗时</div>
          <div class="stats-value">{{ stats.avgCostMs }} ms</div>
          <div class="stats-sub">最大耗时 {{ stats.maxCostMs }} ms</div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="never" class="stats-card">
          <div class="stats-label">平均工具数</div>
          <div class="stats-value">{{ stats.avgToolCount }}</div>
          <div class="stats-sub">运行中 {{ stats.runningCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">

      <template #header>
        <div class="page-header">
          <div>
            <div class="page-title">Agent Trace 调用链</div>
            <div class="page-subtitle">
              查看每一次 Agent 请求的上下文增强、直达路由、LLM 调用、确认动作和最终回复。
            </div>
          </div>

          <el-button type="primary" @click="refreshPage">
            刷新
          </el-button>
        </div>
      </template>

      <!-- 查询区域 -->
      <el-form :inline="true" :model="queryParams" class="query-form">
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            clearable
            placeholder="全部状态"
            style="width: 160px"
          >
            <el-option label="成功" value="SUCCESS" />
            <el-option label="失败" value="FAILED" />
            <el-option label="运行中" value="RUNNING" />
          </el-select>
        </el-form-item>

        <el-form-item label="用户ID">
          <el-input
            v-model="queryParams.userId"
            clearable
            placeholder="请输入用户ID"
            style="width: 160px"
          />
        </el-form-item>

        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            clearable
            placeholder="traceId / 用户输入 / 回复内容"
            style="width: 300px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            查询
          </el-button>
          <el-button @click="resetQuery">
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- Trace 列表 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column label="Trace ID" min-width="260">
          <template #default="{ row }">
            <div class="trace-id-cell">
              <el-text type="primary" truncated>
                {{ row.traceId }}
              </el-text>

              <el-button
                type="primary"
                link
                size="small"
                @click.stop="copyTraceId(row.traceId)"
              >
                复制
              </el-button>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="userId" label="用户ID" width="100" />

        <el-table-column label="用户输入" min-width="260">
          <template #default="{ row }">
            <el-tooltip
              effect="dark"
              :content="row.userMessage || ''"
              placement="top"
              :disabled="!row.userMessage"
            >
              <div class="one-line">
                {{ row.userMessage || '-' }}
              </div>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column label="最终回复" min-width="280">
          <template #default="{ row }">
            <el-tooltip
              effect="dark"
              :content="row.finalReply || ''"
              placement="top"
              :disabled="!row.finalReply"
            >
              <div class="one-line">
                {{ row.finalReply || '-' }}
              </div>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="总耗时" width="110" align="center">
          <template #default="{ row }">
            {{ row.totalCostMs || 0 }} ms
          </template>
        </el-table-column>

        <el-table-column label="工具数" width="90" align="center">
          <template #default="{ row }">
            {{ row.toolCount || 0 }}
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180" />

        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="queryParams.limit"
          :current-page="queryParams.current"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="detailDrawer.visible"
      title="Agent Trace 详情"
      size="60%"
      destroy-on-close
    >
      <div v-loading="detailDrawer.loading">
        <template v-if="detailDrawer.trace">
          <!-- 基础信息 -->
          <el-card shadow="never" class="detail-card">
            <template #header>
              <div class="section-title">基础信息</div>
            </template>

            <el-descriptions :column="2" border>
              <el-descriptions-item label="Trace ID">
                <div class="trace-id-cell">
                  <el-text type="primary">
                    {{ detailDrawer.trace.traceId }}
                  </el-text>

                  <el-button
                    type="primary"
                    link
                    size="small"
                    @click="copyTraceId(detailDrawer.trace.traceId)"
                  >
                    复制
                  </el-button>
                </div>
              </el-descriptions-item>

              <el-descriptions-item label="状态">
                <el-tag :type="getStatusTagType(detailDrawer.trace.status)">
                  {{ getStatusText(detailDrawer.trace.status) }}
                </el-tag>
              </el-descriptions-item>

              <el-descriptions-item label="用户ID">
                {{ detailDrawer.trace.userId || '-' }}
              </el-descriptions-item>

              <el-descriptions-item label="用户名">
                {{ detailDrawer.trace.username || '-' }}
              </el-descriptions-item>

              <el-descriptions-item label="总耗时">
                {{ detailDrawer.trace.totalCostMs || 0 }} ms
              </el-descriptions-item>

              <el-descriptions-item label="工具调用数">
                {{ detailDrawer.trace.toolCount || 0 }}
              </el-descriptions-item>

              <el-descriptions-item label="创建时间">
                {{ detailDrawer.trace.createTime || '-' }}
              </el-descriptions-item>

              <el-descriptions-item label="更新时间">
                {{ detailDrawer.trace.updateTime || '-' }}
              </el-descriptions-item>

              <el-descriptions-item label="用户输入" :span="2">
                <pre class="text-block">{{ detailDrawer.trace.userMessage || '-' }}</pre>
              </el-descriptions-item>

              <el-descriptions-item label="最终回复" :span="2">
                <pre class="text-block">{{ detailDrawer.trace.finalReply || '-' }}</pre>
              </el-descriptions-item>

              <el-descriptions-item
                v-if="detailDrawer.trace.errorMessage"
                label="异常信息"
                :span="2"
              >
                <pre class="error-block">{{ detailDrawer.trace.errorMessage }}</pre>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 步骤时间线 -->
          <el-card shadow="never" class="detail-card">
            <template #header>
              <div class="section-title">执行步骤</div>
            </template>

            <el-empty
              v-if="!detailDrawer.steps || detailDrawer.steps.length === 0"
              description="暂无步骤数据"
            />

            <el-timeline v-else>
              <el-timeline-item
                v-for="step in detailDrawer.steps"
                :key="step.id"
                :timestamp="step.createTime"
                placement="top"
                :type="getStepTimelineType(step)"
              >
                <el-card shadow="never" class="step-card">
                  <div class="step-header">
                    <div class="step-title">
                      <el-tag size="small" :type="getStepTagType(step.stepType)">
                        {{ step.stepType }}
                      </el-tag>

                      <span class="step-name">
                        {{ step.stepName || '-' }}
                      </span>
                    </div>

                    <div class="step-meta">
                      <el-tag size="small" :type="step.status === 'FAILED' ? 'danger' : 'success'">
                        {{ step.status || 'SUCCESS' }}
                      </el-tag>
                      <span class="cost">{{ step.costMs || 0 }} ms</span>
                    </div>
                  </div>

                  <el-collapse class="step-collapse">
                    <el-collapse-item title="输入数据" name="input">
                      <pre class="json-block">{{ formatText(step.inputData) }}</pre>
                    </el-collapse-item>

                    <el-collapse-item title="输出数据" name="output">
                      <pre class="json-block">{{ formatText(step.outputData) }}</pre>
                    </el-collapse-item>

                    <el-collapse-item
                      v-if="step.errorMessage"
                      title="异常信息"
                      name="error"
                    >
                      <pre class="error-block">{{ step.errorMessage }}</pre>
                    </el-collapse-item>
                  </el-collapse>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </el-card>

          <!-- 工具调用日志 -->
          <el-card shadow="never" class="detail-card">
            <template #header>
              <div class="section-title">
                工具调用日志
                <el-tag size="small" type="info" style="margin-left: 8px">
                  {{ detailDrawer.toolLogs.length }} 条
                </el-tag>
              </div>
            </template>

            <el-table
              v-loading="detailDrawer.toolLogLoading"
              :data="detailDrawer.toolLogs"
              border
              stripe
              size="small"
              style="width: 100%"
              :row-class-name="getToolLogRowClassName"
            >
              <el-table-column label="工具名称" min-width="160">
                <template #default="{ row }">
                  <el-text type="primary">
                    {{ row.toolName || '-' }}
                  </el-text>
                </template>
              </el-table-column>

              <el-table-column label="工具描述" min-width="220">
                <template #default="{ row }">
                  <div class="one-line">
                    {{ row.toolDesc || '-' }}
                  </div>
                </template>
              </el-table-column>

              <el-table-column label="方法名" min-width="160">
                <template #default="{ row }">
                  <el-tag size="small">
                    {{ row.methodName || '-' }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column label="状态" width="90" align="center">
                <template #default="{ row }">
                  <el-tag :type="getToolStatusTagType(row.status)" size="small">
                    {{ getToolStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column label="耗时" width="120" align="center">
                <template #default="{ row }">
                  <el-tag
                    v-if="isSlowTool(row)"
                    type="warning"
                    size="small"
                  >
                    {{ row.durationMs || 0 }} ms
                  </el-tag>

                  <span v-else>
                    {{ row.durationMs || 0 }} ms
                  </span>
                </template>
              </el-table-column>

              <el-table-column prop="createTime" label="调用时间" width="180" />

              <el-table-column label="操作" width="100" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link @click="openToolLogDetail(row)">
                    详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-empty
              v-if="!detailDrawer.toolLogLoading && detailDrawer.toolLogs.length === 0"
              description="本次 Trace 暂无工具调用日志"
            />
          </el-card>

          <!-- RAG 检索日志 -->
          <el-card shadow="never" class="detail-card">
            <template #header>
              <div class="section-title">
                RAG 检索日志
                <el-tag size="small" type="info" style="margin-left: 8px">
                  {{ detailDrawer.ragLogs.length }} 条
                </el-tag>
              </div>
            </template>

            <div v-loading="detailDrawer.ragLogLoading">
              <el-empty
                v-if="!detailDrawer.ragLogs || detailDrawer.ragLogs.length === 0"
                description="本次 Trace 暂无 RAG 检索日志"
              />

              <el-collapse v-else>
                <el-collapse-item
                  v-for="log in detailDrawer.ragLogs"
                  :key="log.id"
                  :name="log.id"
                >
                  <template #title>
                    <div class="rag-title">
                      <span class="rag-question">
                        {{ log.question }}
                      </span>

                      <el-tag size="small" type="success">
                        max {{ log.maxScore || '-' }}
                      </el-tag>

                      <el-tag size="small" type="info">
                        min {{ log.minScore || '-' }}
                      </el-tag>
                    </div>
                  </template>

                  <el-descriptions :column="2" border>
                    <el-descriptions-item label="日志ID">
                      {{ log.id }}
                    </el-descriptions-item>

                    <el-descriptions-item label="Trace ID">
                      <div class="trace-id-cell">
                        <el-text type="primary">
                          {{ log.traceId || '-' }}
                        </el-text>

                        <el-button
                          v-if="log.traceId"
                          type="primary"
                          link
                          size="small"
                          @click="copyTraceId(log.traceId)"
                        >
                          复制
                        </el-button>
                      </div>
                    </el-descriptions-item>

                    <el-descriptions-item label="用户ID">
                      {{ log.userId || '-' }}
                    </el-descriptions-item>

                    <el-descriptions-item label="创建时间">
                      {{ log.createTime || '-' }}
                    </el-descriptions-item>

                    <el-descriptions-item label="用户问题" :span="2">
                      <pre class="text-block">{{ log.question || '-' }}</pre>
                    </el-descriptions-item>

                    <el-descriptions-item label="模型回答" :span="2">
                      <pre class="text-block">{{ log.answer || '-' }}</pre>
                    </el-descriptions-item>

                    <el-descriptions-item label="命中来源" :span="2">
                      <el-table
                        :data="parseMatchedSources(log.matchedSources)"
                        border
                        size="small"
                        style="width: 100%"
                      >
                        <el-table-column prop="docId" label="文档ID" width="90" />
                        <el-table-column prop="title" label="标题" min-width="160" />
                        <el-table-column prop="venueName" label="场馆" min-width="140" />
                        <el-table-column prop="courtName" label="场地" min-width="140" />
                        <el-table-column prop="courtType" label="类型" width="100" />
                        <el-table-column label="分数" width="110">
                          <template #default="{ row }">
                            {{ row.score || '-' }}
                          </template>
                        </el-table-column>
                        <el-table-column prop="contentPreview" label="命中文本" min-width="260" />
                      </el-table>
                    </el-descriptions-item>
                  </el-descriptions>
                </el-collapse-item>
              </el-collapse>
            </div>
          </el-card>

          <!-- 工具日志详情弹窗 -->
          <el-dialog
            v-model="toolLogDialog.visible"
            title="工具调用日志详情"
            width="760px"
            destroy-on-close
          >
            <div v-loading="toolLogDialog.loading">
              <template v-if="toolLogDialog.detail">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="日志ID">
                    {{ toolLogDialog.detail.id }}
                  </el-descriptions-item>

                  <el-descriptions-item label="Trace ID">
                    <el-text type="primary">
                      {{ toolLogDialog.detail.traceId || '-' }}
                    </el-text>
                  </el-descriptions-item>

                  <el-descriptions-item label="用户ID">
                    {{ toolLogDialog.detail.userId || '-' }}
                  </el-descriptions-item>

                  <el-descriptions-item label="状态">
                    <el-tag :type="getToolStatusTagType(toolLogDialog.detail.status)">
                      {{ getToolStatusText(toolLogDialog.detail.status) }}
                    </el-tag>
                  </el-descriptions-item>

                  <el-descriptions-item label="工具名称">
                    {{ toolLogDialog.detail.toolName || '-' }}
                  </el-descriptions-item>

                  <el-descriptions-item label="方法名">
                    {{ toolLogDialog.detail.methodName || '-' }}
                  </el-descriptions-item>

                  <el-descriptions-item label="工具类" :span="2">
                    {{ toolLogDialog.detail.toolClass || '-' }}
                  </el-descriptions-item>

                  <el-descriptions-item label="工具描述" :span="2">
                    {{ toolLogDialog.detail.toolDesc || '-' }}
                  </el-descriptions-item>

                  <el-descriptions-item label="用户原始输入" :span="2">
                    <pre class="text-block">{{ toolLogDialog.detail.userMessage || '-' }}</pre>
                  </el-descriptions-item>

                  <el-descriptions-item label="工具入参" :span="2">
                    <pre class="json-block">{{ formatText(toolLogDialog.detail.argumentsJson) }}</pre>
                  </el-descriptions-item>

                  <el-descriptions-item label="工具返回" :span="2">
                    <pre class="json-block">{{ formatText(toolLogDialog.detail.resultText) }}</pre>
                  </el-descriptions-item>

                  <el-descriptions-item
                    v-if="toolLogDialog.detail.errorMessage"
                    label="异常信息"
                    :span="2"
                  >
                    <pre class="error-block">{{ toolLogDialog.detail.errorMessage }}</pre>
                  </el-descriptions-item>

                  <el-descriptions-item label="耗时">
                    {{ toolLogDialog.detail.durationMs || 0 }} ms
                  </el-descriptions-item>

                  <el-descriptions-item label="创建时间">
                    {{ toolLogDialog.detail.createTime || '-' }}
                  </el-descriptions-item>
                </el-descriptions>
              </template>

              <el-empty v-else description="暂无工具日志详情" />
            </div>
          </el-dialog>
        </template>

        <el-empty v-else description="暂无详情" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { CopyDocument } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getAgentTracePage,
  getAgentTraceDetail,
  getAgentTraceStats
} from '@/api/agentTrace'
import {
  pageAgentToolLog,
  getAgentToolLogDetail
} from '@/api/agentToolLog'

import {
  getRagSearchLogsByTraceId
} from '@/api/ragSearchLog'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  current: 1,
  limit: 10,
  userId: '',
  status: '',
  keyword: ''
})

const detailDrawer = reactive({
  visible: false,
  loading: false,
  trace: null,
  steps: [],

  /**
   * 当前 Trace 关联的工具调用日志。
   */
  toolLogs: [],

  /**
   * 工具日志加载状态。
   */
  toolLogLoading: false,

   /**
   * 当前 Trace 关联的 RAG 检索日志。
   */
  ragLogs: [],

  /**
   * RAG 日志加载状态。
   */
  ragLogLoading: false
})

const stats = reactive({
  totalCount: 0,
  successCount: 0,
  failedCount: 0,
  runningCount: 0,
  successRate: 0,
  avgCostMs: 0,
  maxCostMs: 0,
  avgToolCount: 0
})

/**
 * 加载 Trace 统计卡片数据。
 */
async function loadStats() {
  try {
    const res = await getAgentTraceStats()
    const data = unwrapResult(res) || {}

    stats.totalCount = data.totalCount || 0
    stats.successCount = data.successCount || 0
    stats.failedCount = data.failedCount || 0
    stats.runningCount = data.runningCount || 0
    stats.successRate = data.successRate || 0
    stats.avgCostMs = data.avgCostMs || 0
    stats.maxCostMs = data.maxCostMs || 0
    stats.avgToolCount = data.avgToolCount || 0
  } catch (e) {
    console.error(e)
    ElMessage.error('加载 Trace 统计失败')
  }
}

const toolLogDialog = reactive({
  visible: false,
  loading: false,
  detail: null
})

/**
 * 兼容不同 Result 包装格式。
 *
 * 有些项目 axios 拦截器会直接返回 res.data，
 * 有些会返回完整 res。
 */
function unwrapResult(res) {
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
 * 加载 Trace 分页数据。
 */
async function loadData() {
  loading.value = true

  try {
    const params = {
      current: queryParams.current,
      limit: queryParams.limit
    }

    if (queryParams.userId) {
      params.userId = queryParams.userId
    }

    if (queryParams.status) {
      params.status = queryParams.status
    }

    if (queryParams.keyword) {
      params.keyword = queryParams.keyword
    }

    const res = await getAgentTracePage(params)
    const data = unwrapResult(res) || {}

    /**
     * PageInfo 常见字段：
     * list / total
     *
     * 如果你后端返回的是 records，
     * 这里也做了兼容。
     */
    tableData.value = data.list || data.records || []
    total.value = data.total || 0
  } catch (e) {
    console.error(e)
    ElMessage.error('加载 Agent Trace 列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 查询。
 */
function handleSearch() {
  queryParams.current = 1
  loadData()
}

/**
 * 重置。
 */
function resetQuery() {
  queryParams.current = 1
  queryParams.limit = 10
  queryParams.userId = ''
  queryParams.status = ''
  queryParams.keyword = ''
  loadData()
}

/**
 * 当前页变化。
 */
function handleCurrentChange(page) {
  queryParams.current = page
  loadData()
}

/**
 * 每页条数变化。
 */
function handleSizeChange(size) {
  queryParams.limit = size
  queryParams.current = 1
  loadData()
}

/**
 * 打开 Trace 详情抽屉。
 *
 * 同时加载：
 * 1. Trace 主信息
 * 2. Trace 步骤
 * 3. 工具调用日志
 * 4. RAG 检索日志
 */
async function openDetail(row) {
  if (!row || !row.traceId) {
    return
  }

  detailDrawer.visible = true
  detailDrawer.loading = true
  detailDrawer.toolLogLoading = true
  detailDrawer.ragLogLoading = true

  detailDrawer.trace = null
  detailDrawer.steps = []
  detailDrawer.toolLogs = []
  detailDrawer.ragLogs = []

  try {
    const [traceRes, toolLogRes, ragLogRes] = await Promise.all([
      getAgentTraceDetail(row.traceId),
      pageAgentToolLog({
        pageNum: 1,
        pageSize: 100,
        traceId: row.traceId
      }),
      getRagSearchLogsByTraceId(row.traceId)
    ])

    const traceData = unwrapResult(traceRes) || {}
    const toolLogData = unwrapResult(toolLogRes) || {}
    const ragLogData = unwrapResult(ragLogRes) || []

    detailDrawer.trace = traceData.trace || null
    detailDrawer.steps = traceData.steps || []

    detailDrawer.toolLogs = toolLogData.list || toolLogData.records || []

    /**
     * ragLogData 后端返回的是 List<RagSearchLog>。
     */
    detailDrawer.ragLogs = Array.isArray(ragLogData) ? ragLogData : []
  } catch (e) {
    console.error(e)
    ElMessage.error('加载 Agent Trace 详情失败')
  } finally {
    detailDrawer.loading = false
    detailDrawer.toolLogLoading = false
    detailDrawer.ragLogLoading = false
  }
}

/**
 * 打开工具日志详情。
 */
async function openToolLogDetail(row) {
  if (!row || !row.id) {
    return
  }

  toolLogDialog.visible = true
  toolLogDialog.loading = true
  toolLogDialog.detail = null

  try {
    const res = await getAgentToolLogDetail(row.id)
    toolLogDialog.detail = unwrapResult(res)
  } catch (e) {
    console.error(e)
    ElMessage.error('加载工具日志详情失败')
  } finally {
    toolLogDialog.loading = false
  }
}

/**
 * 复制 traceId。
 */
async function copyTraceId(traceId) {
  if (!traceId) {
    return
  }

  try {
    await navigator.clipboard.writeText(traceId)
    ElMessage.success('traceId 已复制')
  } catch (e) {
    /**
     * 兼容部分浏览器不支持 navigator.clipboard 的情况。
     */
    const input = document.createElement('input')
    input.value = traceId
    document.body.appendChild(input)
    input.select()
    document.execCommand('copy')
    document.body.removeChild(input)

    ElMessage.success('traceId 已复制')
  }
}

/**
 * 工具日志行样式。
 *
 * 失败工具：红色背景
 * 慢工具：黄色背景
 */
function getToolLogRowClassName({ row }) {
  if (!row) {
    return ''
  }

  if (row.status === 'FAIL' || row.status === 'FAILED') {
    return 'tool-log-failed-row'
  }

  if ((row.durationMs || 0) >= 3000) {
    return 'tool-log-slow-row'
  }

  return ''
}

/**
 * 工具耗时是否慢。
 */
function isSlowTool(row) {
  return row && (row.durationMs || 0) >= 3000
}

/**
 * 解析 RAG matchedSources。
 */
function parseMatchedSources(matchedSources) {
  if (!matchedSources) {
    return []
  }

  try {
    const list = JSON.parse(matchedSources)
    return Array.isArray(list) ? list : []
  } catch (e) {
    return []
  }
}

/**
 * 状态标签颜色。
 */
function getStatusTagType(status) {
  if (status === 'SUCCESS') {
    return 'success'
  }

  if (status === 'FAILED') {
    return 'danger'
  }

  if (status === 'RUNNING') {
    return 'warning'
  }

  return 'info'
}

/**
 * 工具调用状态标签。
 *
 * 兼容：
 * SUCCESS
 * FAIL
 * FAILED
 */
function getToolStatusTagType(status) {
  if (status === 'SUCCESS') {
    return 'success'
  }

  if (status === 'FAIL' || status === 'FAILED') {
    return 'danger'
  }

  return 'info'
}

/**
 * 工具调用状态中文。
 */
function getToolStatusText(status) {
  if (status === 'SUCCESS') {
    return '成功'
  }

  if (status === 'FAIL' || status === 'FAILED') {
    return '失败'
  }

  return status || '未知'
}

/**
 * 状态中文。
 */
function getStatusText(status) {
  if (status === 'SUCCESS') {
    return '成功'
  }

  if (status === 'FAILED') {
    return '失败'
  }

  if (status === 'RUNNING') {
    return '运行中'
  }

  return status || '未知'
}

/**
 * 步骤标签颜色。
 */
function getStepTagType(stepType) {
  const map = {
    TRACE_START: 'info',
    CONTEXT_PREPARE: 'primary',
    CONTEXT_REFRESH: 'primary',
    PENDING_ACTION: 'warning',
    DIRECT_ROUTE: 'success',
    LLM_CALL: 'danger',
    FINAL_REPLY: 'success',
    TRACE_FAILED: 'danger',
    TOOL_CALL: 'warning',
    RAG_RETRIEVE: 'primary'
  }

  return map[stepType] || 'info'
}

/**
 * 时间线颜色。
 */
function getStepTimelineType(step) {
  if (!step) {
    return 'info'
  }

  if (step.status === 'FAILED') {
    return 'danger'
  }

  if (step.stepType === 'LLM_CALL') {
    return 'warning'
  }

  if (step.stepType === 'FINAL_REPLY') {
    return 'success'
  }

  return 'primary'
}

/**
 * 尝试格式化 JSON 字符串。
 */
function formatText(value) {
  if (!value) {
    return '-'
  }

  if (typeof value !== 'string') {
    return JSON.stringify(value, null, 2)
  }

  const text = value.trim()

  if (!text) {
    return '-'
  }

  try {
    const json = JSON.parse(text)
    return JSON.stringify(json, null, 2)
  } catch (e) {
    return text
  }
}

async function refreshPage() {
  await Promise.all([
    loadStats(),
    loadData()
  ])
}

onMounted(() => {
  loadData()
  loadStats()
})
</script>

<style scoped>
.agent-trace-page {
  padding: 16px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

.page-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}

.query-form {
  margin-bottom: 12px;
}

.one-line {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.detail-card {
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
}

.text-block {
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  font-family: Consolas, Menlo, Monaco, monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #303133;
}

.error-block {
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  padding: 8px;
  border-radius: 6px;
  background: #fef0f0;
  color: #c45656;
  font-family: Consolas, Menlo, Monaco, monospace;
  font-size: 13px;
  line-height: 1.6;
}

.json-block {
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  padding: 8px;
  border-radius: 6px;
  background: #f5f7fa;
  color: #303133;
  font-family: Consolas, Menlo, Monaco, monospace;
  font-size: 13px;
  line-height: 1.6;
}

.step-card {
  border-radius: 8px;
}

.step-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.step-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.step-name {
  font-weight: 600;
  color: #303133;
}

.step-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cost {
  font-size: 13px;
  color: #909399;
}

.step-collapse {
  margin-top: 12px;
}
.stats-row {
  margin-bottom: 16px;
}

.stats-card {
  border-radius: 8px;
}

.stats-card.success {
  border-color: #d1f3d1;
}

.stats-label {
  font-size: 13px;
  color: #909399;
}

.stats-value {
  margin-top: 8px;
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.stats-sub {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}

.trace-id-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rag-title {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.rag-question {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/**
 * 工具失败行高亮。
 */
:deep(.tool-log-failed-row) {
  background-color: #fef0f0 !important;
}

/**
 * 慢工具调用行高亮。
 */
:deep(.tool-log-slow-row) {
  background-color: #fdf6ec !important;
}
</style>