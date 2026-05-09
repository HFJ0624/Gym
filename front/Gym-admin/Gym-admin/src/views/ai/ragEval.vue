<template>
  <div class="rag-eval-page">
    <el-card shadow="never">
      <template #header>
        <div class="page-header">
          <div>
            <div class="page-title">RAG 质量评估</div>
            <div class="page-subtitle">
              使用标准问题集评估 Top1 命中率、TopK 命中率、来源正确率和无答案率。
            </div>
          </div>

          <el-button type="primary" @click="openRunDialog">
            运行评估
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 评估用例 -->
        <el-tab-pane label="评估用例" name="case">
          <div class="toolbar">
            <el-input
              v-model="caseQuery.keyword"
              placeholder="搜索问题"
              clearable
              style="width: 260px"
              @keyup.enter="loadCases"
            />

            <el-input
              v-model="caseQuery.category"
              placeholder="分类"
              clearable
              style="width: 180px"
            />

            <el-select
              v-model="caseQuery.enabled"
              placeholder="启用状态"
              clearable
              style="width: 140px"
            >
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>

            <el-button type="primary" @click="loadCases">查询</el-button>
            <el-button @click="resetCaseQuery">重置</el-button>
            <el-button type="success" @click="openCaseDialog()">新增用例</el-button>
          </div>

          <el-table :data="caseList" border stripe v-loading="caseLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="question" label="问题" min-width="260" />
            <el-table-column prop="expectedDocIds" label="期望文档ID" width="160" />
            <el-table-column prop="expectedKeywords" label="期望关键词" min-width="200" />
            <el-table-column label="期望无答案" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.expectedNoAnswer === 1 ? 'warning' : 'success'">
                  {{ row.expectedNoAnswer === 1 ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column label="启用" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.enabled === 1 ? 'success' : 'info'">
                  {{ row.enabled === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />

            <el-table-column label="操作" width="160" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="openCaseDialog(row)">编辑</el-button>
                <el-button type="danger" link @click="removeCase(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              background
              layout="total, sizes, prev, pager, next"
              :total="caseTotal"
              :page-size="caseQuery.limit"
              :current-page="caseQuery.current"
              @current-change="handleCasePageChange"
              @size-change="handleCaseSizeChange"
            />
          </div>
        </el-tab-pane>

        <!-- 评估批次 -->
        <el-tab-pane label="评估结果" name="run">
          <el-table :data="runList" border stripe v-loading="runLoading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="runNo" label="批次号" min-width="220" />
            <el-table-column prop="topK" label="TopK" width="80" />
            <el-table-column prop="minScore" label="阈值" width="100" />
            <el-table-column prop="totalCount" label="总数" width="80" />
            <el-table-column prop="top1HitRate" label="Top1命中率" width="130">
              <template #default="{ row }">{{ row.top1HitRate || 0 }}%</template>
            </el-table-column>
            <el-table-column prop="topkHitRate" label="TopK命中率" width="130">
              <template #default="{ row }">{{ row.topkHitRate || 0 }}%</template>
            </el-table-column>
            <el-table-column prop="sourceCorrectRate" label="来源正确率" width="130">
              <template #default="{ row }">{{ row.sourceCorrectRate || 0 }}%</template>
            </el-table-column>
            <el-table-column prop="noAnswerRate" label="无答案率" width="120">
              <template #default="{ row }">{{ row.noAnswerRate || 0 }}%</template>
            </el-table-column>
            <el-table-column prop="avgCostMs" label="平均耗时" width="120">
              <template #default="{ row }">{{ row.avgCostMs || 0 }} ms</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'SUCCESS' ? 'success' : row.status === 'FAILED' ? 'danger' : 'warning'">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />

            <el-table-column label="操作" width="110" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="openRunDetail(row)">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              background
              layout="total, sizes, prev, pager, next"
              :total="runTotal"
              :page-size="runQuery.limit"
              :current-page="runQuery.current"
              @current-change="handleRunPageChange"
              @size-change="handleRunSizeChange"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 用例弹窗 -->
    <el-dialog v-model="caseDialog.visible" :title="caseDialog.form.id ? '编辑评估用例' : '新增评估用例'" width="620px">
      <el-form :model="caseDialog.form" label-width="110px">
        <el-form-item label="问题">
          <el-input v-model="caseDialog.form.question" type="textarea" :rows="3" />
        </el-form-item>

        <el-form-item label="期望文档ID">
          <el-input v-model="caseDialog.form.expectedDocIds" placeholder="多个用英文逗号分隔，例如：1,2,3" />
        </el-form-item>

        <el-form-item label="期望关键词">
          <el-input v-model="caseDialog.form.expectedKeywords" placeholder="多个用英文逗号分隔，例如：预约,退款" />
        </el-form-item>

        <el-form-item label="期望无答案">
          <el-switch
            v-model="caseDialog.form.expectedNoAnswer"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item label="分类">
          <el-input v-model="caseDialog.form.category" />
        </el-form-item>

        <el-form-item label="启用">
          <el-switch
            v-model="caseDialog.form.enabled"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="caseDialog.form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="caseDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitCase">保存</el-button>
      </template>
    </el-dialog>

    <!-- 运行评估弹窗 -->
    <el-dialog v-model="runDialog.visible" title="运行 RAG 评估" width="480px">
      <el-form :model="runDialog.form" label-width="110px">
        <el-form-item label="TopK">
          <el-input-number v-model="runDialog.form.topK" :min="1" :max="20" />
        </el-form-item>

        <el-form-item label="最低相似度">
          <el-input-number
            v-model="runDialog.form.minScore"
            :min="0"
            :max="1"
            :step="0.05"
            :precision="2"
          />
        </el-form-item>

        <el-form-item label="分类过滤">
          <el-input v-model="runDialog.form.category" placeholder="为空表示评估全部启用用例" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="runDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="runDialog.loading" @click="submitRunEval">
          开始评估
        </el-button>
      </template>
    </el-dialog>

    <!-- 评估详情抽屉 -->
    <el-drawer v-model="detailDrawer.visible" title="RAG 评估详情" size="70%">
      <div v-loading="detailDrawer.loading">
        <template v-if="detailDrawer.run">
          <el-card shadow="never" class="detail-card">
            <template #header>批次指标</template>

            <el-row :gutter="12">
              <el-col :span="6">
                <div class="metric-card">
                  <div class="metric-label">Top1 命中率</div>
                  <div class="metric-value">{{ detailDrawer.run.top1HitRate || 0 }}%</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="metric-card">
                  <div class="metric-label">TopK 命中率</div>
                  <div class="metric-value">{{ detailDrawer.run.topkHitRate || 0 }}%</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="metric-card">
                  <div class="metric-label">来源正确率</div>
                  <div class="metric-value">{{ detailDrawer.run.sourceCorrectRate || 0 }}%</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="metric-card">
                  <div class="metric-label">无答案率</div>
                  <div class="metric-value">{{ detailDrawer.run.noAnswerRate || 0 }}%</div>
                </div>
              </el-col>
            </el-row>
          </el-card>

          <el-card shadow="never" class="detail-card">
            <template #header>明细结果</template>

            <el-table :data="detailDrawer.results" border stripe>
              <el-table-column prop="caseId" label="用例ID" width="80" />
              <el-table-column prop="question" label="问题" min-width="240" />
              <el-table-column prop="retrievedDocIds" label="命中文档ID" width="160" />
              <el-table-column label="Top1" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.top1Hit === 1 ? 'success' : 'danger'">
                    {{ row.top1Hit === 1 ? '命中' : '未命中' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="TopK" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.topkHit === 1 ? 'success' : 'danger'">
                    {{ row.topkHit === 1 ? '命中' : '未命中' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="来源正确" width="110">
                <template #default="{ row }">
                  <el-tag :type="row.sourceCorrect === 1 ? 'success' : 'danger'">
                    {{ row.sourceCorrect === 1 ? '正确' : '错误' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="无答案" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.actualNoAnswer === 1 ? 'warning' : 'info'">
                    {{ row.actualNoAnswer === 1 ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="maxScore" label="最高分" width="100" />
              <el-table-column prop="costMs" label="耗时" width="100">
                <template #default="{ row }">{{ row.costMs || 0 }} ms</template>
              </el-table-column>

              <el-table-column type="expand">
                <template #default="{ row }">
                  <div class="expand-box">
                    <div class="expand-title">期望文档ID</div>
                    <pre>{{ row.expectedDocIds || '-' }}</pre>

                    <div class="expand-title">期望关键词</div>
                    <pre>{{ row.expectedKeywords || '-' }}</pre>

                    <div class="expand-title">模型回答</div>
                    <pre>{{ row.answerText || '-' }}</pre>

                    <div class="expand-title">命中来源 JSON</div>
                    <pre>{{ formatJson(row.matchedSources) }}</pre>

                    <div v-if="row.errorMessage" class="expand-title error">异常信息</div>
                    <pre v-if="row.errorMessage" class="error-text">{{ row.errorMessage }}</pre>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRagEvalCasePage,
  saveRagEvalCase,
  deleteRagEvalCase,
  runRagEval,
  getRagEvalRunPage,
  getRagEvalRunDetail
} from '@/api/ragEval'

const activeTab = ref('case')

const caseLoading = ref(false)
const caseList = ref([])
const caseTotal = ref(0)

const caseQuery = reactive({
  current: 1,
  limit: 10,
  category: '',
  enabled: null,
  keyword: ''
})

const runLoading = ref(false)
const runList = ref([])
const runTotal = ref(0)

const runQuery = reactive({
  current: 1,
  limit: 10
})

const caseDialog = reactive({
  visible: false,
  form: {}
})

const runDialog = reactive({
  visible: false,
  loading: false,
  form: {
    topK: 3,
    minScore: 0.5,
    category: ''
  }
})

const detailDrawer = reactive({
  visible: false,
  loading: false,
  run: null,
  results: []
})

function unwrapResult(res) {
  if (!res) return null
  if (res.data && res.data.data !== undefined) return res.data.data
  if (res.data !== undefined) return res.data
  return res
}

async function loadCases() {
  caseLoading.value = true
  try {
    const res = await getRagEvalCasePage(caseQuery)
    const data = unwrapResult(res) || {}
    caseList.value = data.list || data.records || []
    caseTotal.value = data.total || 0
  } finally {
    caseLoading.value = false
  }
}

async function loadRuns() {
  runLoading.value = true
  try {
    const res = await getRagEvalRunPage(runQuery)
    const data = unwrapResult(res) || {}
    runList.value = data.list || data.records || []
    runTotal.value = data.total || 0
  } finally {
    runLoading.value = false
  }
}

function resetCaseQuery() {
  caseQuery.current = 1
  caseQuery.category = ''
  caseQuery.enabled = null
  caseQuery.keyword = ''
  loadCases()
}

function openCaseDialog(row) {
  caseDialog.visible = true
  caseDialog.form = row
    ? { ...row }
    : {
        question: '',
        expectedDocIds: '',
        expectedKeywords: '',
        expectedNoAnswer: 0,
        category: '',
        enabled: 1,
        remark: ''
      }
}

async function submitCase() {
  if (!caseDialog.form.question) {
    ElMessage.warning('请输入问题')
    return
  }

  await saveRagEvalCase(caseDialog.form)
  ElMessage.success('保存成功')
  caseDialog.visible = false
  loadCases()
}

async function removeCase(row) {
  await ElMessageBox.confirm('确认删除该评估用例吗？', '提示', {
    type: 'warning'
  })

  await deleteRagEvalCase(row.id)
  ElMessage.success('删除成功')
  loadCases()
}

function openRunDialog() {
  runDialog.visible = true
  runDialog.form = {
    topK: 3,
    minScore: 0.5,
    category: ''
  }
}

async function submitRunEval() {
  runDialog.loading = true

  try {
    const res = await runRagEval(runDialog.form)
    const data = unwrapResult(res)

    ElMessage.success('评估完成')
    runDialog.visible = false
    activeTab.value = 'run'
    loadRuns()

    if (data && data.id) {
      openRunDetail(data)
    }
  } finally {
    runDialog.loading = false
  }
}

async function openRunDetail(row) {
  detailDrawer.visible = true
  detailDrawer.loading = true
  detailDrawer.run = null
  detailDrawer.results = []

  try {
    const res = await getRagEvalRunDetail(row.id)
    const data = unwrapResult(res) || {}

    detailDrawer.run = data.run
    detailDrawer.results = data.results || []
  } finally {
    detailDrawer.loading = false
  }
}

function handleCasePageChange(page) {
  caseQuery.current = page
  loadCases()
}

function handleCaseSizeChange(size) {
  caseQuery.limit = size
  caseQuery.current = 1
  loadCases()
}

function handleRunPageChange(page) {
  runQuery.current = page
  loadRuns()
}

function handleRunSizeChange(size) {
  runQuery.limit = size
  runQuery.current = 1
  loadRuns()
}

function formatJson(value) {
  if (!value) return '-'

  try {
    return JSON.stringify(JSON.parse(value), null, 2)
  } catch (e) {
    return value
  }
}

onMounted(() => {
  loadCases()
  loadRuns()
})
</script>

<style scoped>
.rag-eval-page {
  padding: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
}

.page-subtitle {
  margin-top: 6px;
  color: #909399;
  font-size: 13px;
}

.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.detail-card {
  margin-bottom: 16px;
}

.metric-card {
  padding: 14px;
  border-radius: 8px;
  background: #f5f7fa;
}

.metric-label {
  color: #909399;
  font-size: 13px;
}

.metric-value {
  margin-top: 8px;
  font-size: 24px;
  font-weight: 700;
}

.expand-box {
  padding: 12px 24px;
}

.expand-title {
  margin-top: 10px;
  font-weight: 700;
}

.expand-box pre {
  white-space: pre-wrap;
  word-break: break-word;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 6px;
}

.error {
  color: #f56c6c;
}

.error-text {
  color: #f56c6c;
  background: #fef0f0 !important;
}
</style>