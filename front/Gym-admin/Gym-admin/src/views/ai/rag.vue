<template>
  <div class="rag-page">
    <!-- 页面标题 -->
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h2>RAG 知识库管理</h2>
            <p>
              用于将 MySQL 中的 knowledge_document 知识文档重新切分、向量化，并写入 PostgreSQL + pgvector。
            </p>
          </div>
        </div>
      </template>

      <el-alert
        title="说明：重建索引是管理操作，不会由普通用户提问自动触发。普通用户提问只会调用 /front/rag/ask 进行检索和回答。"
        type="info"
        show-icon
        :closable="false"
      />

      <div class="action-area">
        <el-button
          type="primary"
          size="large"
          :loading="rebuildLoading"
          @click="handleRebuild"
        >
          重建知识库索引
        </el-button>

        <el-button
          size="large"
          @click="handleRefreshTip"
        >
          查看操作说明
        </el-button>
      </div>

      <el-descriptions
        title="重建流程"
        :column="1"
        border
        class="flow-desc"
      >
        <el-descriptions-item label="第一步">
          从 MySQL 的 knowledge_document 表读取 enabled = 1 的知识。
        </el-descriptions-item>

        <el-descriptions-item label="第二步">
          将知识正文按 chunk.size 和 chunk.overlap 切分成多个知识片段。
        </el-descriptions-item>

        <el-descriptions-item label="第三步">
          调用火山方舟 embedding 模型，将每个知识片段转成向量。
        </el-descriptions-item>

        <el-descriptions-item label="第四步">
          将文本片段、metadata、embedding 向量写入 PostgreSQL 的 gym_knowledge 表。
        </el-descriptions-item>

        <el-descriptions-item label="第五步">
          将 knowledge_document.indexed_status 更新为 1。
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 测试问答区域 -->
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h3>知识库问答测试</h3>
            <p>
              重建索引后，可以在这里测试 RAG 是否能正常召回知识。
            </p>
          </div>
        </div>
      </template>

      <el-form label-width="100px">
        <el-form-item label="测试问题">
          <el-input
            v-model="testForm.question"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            placeholder="例如：平台预约规则？羽毛球馆可以停车吗？1号篮球场价格怎么算？"
          />
        </el-form-item>

        <el-form-item label="场馆ID">
          <el-input-number
            v-model="testForm.venueId"
            :min="1"
            placeholder="可选"
            clearable
          />
          <span class="form-tip">可选。测试某个场馆知识时填写。</span>
        </el-form-item>

        <el-form-item label="场地ID">
          <el-input-number
            v-model="testForm.courtId"
            :min="1"
            placeholder="可选"
            clearable
          />
          <span class="form-tip">可选。测试某个具体场地知识时填写。</span>
        </el-form-item>

        <el-form-item>
          <el-button
            type="success"
            :loading="testLoading"
            @click="handleTestAsk"
          >
            测试问答
          </el-button>

          <el-button @click="handleClearTest">
            清空
          </el-button>
        </el-form-item>
      </el-form>

      <div v-if="testResult.answer" class="answer-box">
        <h4>回答结果</h4>
        <p>{{ testResult.answer }}</p>
      </div>

      <div v-if="testResult.sources.length > 0" class="source-box">
        <h4>参考来源</h4>

        <el-table
          :data="testResult.sources"
          border
          style="width: 100%"
        >
          <el-table-column
            prop="title"
            label="知识标题"
            min-width="180"
          />

          <el-table-column
            prop="knowledgeScopeName"
            label="知识范围"
            width="120"
          />

          <el-table-column
            prop="venueName"
            label="场馆"
            width="160"
          />

          <el-table-column
            prop="courtName"
            label="场地"
            width="160"
          />

          <el-table-column
            prop="topic"
            label="主题"
            width="140"
          />

          <el-table-column
            label="相似度"
            width="100"
          >
            <template #default="{ row }">
              {{ formatScore(row.score) }}
            </template>
          </el-table-column>

          <el-table-column
            prop="contentPreview"
            label="命中文本"
            min-width="260"
          />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { rebuildRagKnowledge, testRagAsk } from '@/api/rag'

/**
 * 重建索引加载状态。
 */
const rebuildLoading = ref(false)

/**
 * 测试问答加载状态。
 */
const testLoading = ref(false)

/**
 * 测试表单。
 *
 * question：必填
 * venueId：可选
 * courtId：可选
 */
const testForm = reactive({
  question: '',
  venueId: undefined,
  courtId: undefined
})

/**
 * 测试结果。
 */
const testResult = reactive({
  answer: '',
  sources: []
})

/**
 * 点击“重建知识库索引”。
 *
 * 注意：
 * 这个操作会清空并重建 pgvector 表，属于有副作用的管理操作，
 * 所以点击前要二次确认。
 */
const handleRebuild = async () => {
  await ElMessageBox.confirm(
    '确定要重建 RAG 知识库索引吗？该操作会重新生成向量数据，耗时取决于知识数量。',
    '重建确认',
    {
      type: 'warning',
      confirmButtonText: '确定重建',
      cancelButtonText: '取消'
    }
  )

  rebuildLoading.value = true

  try {
    await rebuildRagKnowledge()

    ElMessage.success('知识库索引重建成功')

    /**
     * 重建成功后，建议用户去测试问答区域验证召回效果。
     */
  } finally {
    rebuildLoading.value = false
  }
}

/**
 * 显示操作说明。
 */
const handleRefreshTip = () => {
  ElMessage.info('先在 MySQL 的 knowledge_document 表维护知识，再点击“重建知识库索引”，最后用测试问答验证效果。')
}

/**
 * 测试 RAG 问答。
 */
const handleTestAsk = async () => {
  if (!testForm.question || !testForm.question.trim()) {
    ElMessage.warning('请输入测试问题')
    return
  }

  testLoading.value = true
  testResult.answer = ''
  testResult.sources = []

  try {
    const payload = {
      question: testForm.question.trim()
    }

    /**
     * venueId 是可选参数。
     * 如果填写，后端会优先检索当前场馆相关知识。
     */
    if (testForm.venueId) {
      payload.venueId = testForm.venueId
    }

    /**
     * courtId 是可选参数。
     * 如果填写，后端会优先检索当前场地相关知识。
     */
    if (testForm.courtId) {
      payload.courtId = testForm.courtId
    }

    const res = await testRagAsk(payload)

    /**
     * 这里按你的统一 Result 格式处理：
     * {
     *   code: 200,
     *   data: {
     *     answer: '',
     *     sources: []
     *   }
     * }
     */
    testResult.answer = res.data?.answer || '知识库暂无相关信息。'
    testResult.sources = res.data?.sources || []

    if (testResult.sources.length === 0) {
      ElMessage.info('本次没有命中参考来源，请检查 knowledge_document 是否已入库，或降低 min-score。')
    }
  } finally {
    testLoading.value = false
  }
}

/**
 * 清空测试内容。
 */
const handleClearTest = () => {
  testForm.question = ''
  testForm.venueId = undefined
  testForm.courtId = undefined
  testResult.answer = ''
  testResult.sources = []
}

/**
 * 格式化相似度分数。
 */
const formatScore = score => {
  if (score === null || score === undefined) {
    return '-'
  }

  return Number(score).toFixed(4)
}
</script>

<style scoped>
.rag-page {
  padding: 20px;
}

.page-card {
  margin-bottom: 20px;
  border-radius: 10px;
}

.card-header h2,
.card-header h3 {
  margin: 0 0 8px;
  font-weight: 600;
}

.card-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.action-area {
  margin-top: 20px;
  margin-bottom: 20px;
}

.flow-desc {
  margin-top: 16px;
}

.form-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 13px;
}

.answer-box {
  margin-top: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  line-height: 1.8;
}

.answer-box h4 {
  margin: 0 0 10px;
}

.answer-box p {
  margin: 0;
  white-space: pre-wrap;
}

.source-box {
  margin-top: 20px;
}

.source-box h4 {
  margin: 0 0 12px;
}
</style>