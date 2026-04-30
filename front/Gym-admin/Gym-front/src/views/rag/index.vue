<template>
  <div class="rag-page">
    <div class="rag-container">
      <!-- 页面标题区域 -->
      <div class="page-header">
        <div>
          <h2>场馆知识库问答</h2>
          <p>
            你可以询问场馆设施、停车说明、开放时间、预约规则、退款规则、场地价格等问题。
          </p>
        </div>
      </div>

      <!-- 当前上下文提示 -->
      <el-alert
        v-if="contextText"
        :title="contextText"
        type="info"
        show-icon
        :closable="false"
        class="context-alert"
      />

      <!-- 提问区域 -->
      <el-card class="ask-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>请输入你的问题</span>
          </div>
        </template>

        <el-input
          v-model="question"
          type="textarea"
          :rows="4"
          maxlength="300"
          show-word-limit
          placeholder="例如：羽毛球馆可以停车吗？1号篮球场价格怎么算？预约取消后会退款吗？"
        />

        <div class="quick-questions">
          <span class="quick-title">常见问题：</span>

          <el-tag
            v-for="item in quickQuestions"
            :key="item"
            class="quick-tag"
            @click="setQuestion(item)"
          >
            {{ item }}
          </el-tag>
        </div>

        <div class="actions">
          <el-button
            type="primary"
            :loading="loading"
            @click="handleAsk"
          >
            提问
          </el-button>

          <el-button @click="handleClear">
            清空
          </el-button>
        </div>
      </el-card>

      <!-- 回答区域 -->
      <el-card v-if="answer" class="answer-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>回答</span>
          </div>
        </template>

        <div class="answer-content">
          {{ answer }}
        </div>
      </el-card>

      <!-- 来源区域 -->
      <el-card v-if="sources.length > 0" class="source-card-wrapper" shadow="never">
        <template #header>
          <div class="card-header">
            <span>参考来源</span>
            <span class="source-count">共 {{ sources.length }} 条</span>
          </div>
        </template>

        <div class="source-list">
          <el-card
            v-for="(source, index) in sources"
            :key="getSourceKey(source, index)"
            class="source-card"
            shadow="never"
          >
            <div class="source-header">
              <div class="source-title">
                {{ index + 1 }}. {{ source.title || '未命名知识' }}
              </div>

              <el-tag size="small" type="success">
                {{ source.knowledgeScopeName || formatKnowledgeScope(source.knowledgeScope) }}
              </el-tag>
            </div>

            <div class="source-meta">
              <span v-if="source.venueName">
                场馆：{{ source.venueName }}
              </span>

              <span v-if="source.courtName">
                场地：{{ source.courtName }}
              </span>

              <span v-if="source.courtType">
                类型：{{ source.courtType }}
              </span>

              <span v-if="source.topic">
                主题：{{ source.topic }}
              </span>

              <span v-if="source.score !== null && source.score !== undefined">
                相似度：{{ formatScore(source.score) }}
              </span>
            </div>

            <div v-if="source.tags" class="source-tags">
              <el-tag
                v-for="tag in splitTags(source.tags)"
                :key="tag"
                size="small"
                type="info"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>

            <div class="source-preview">
              {{ source.contentPreview }}
            </div>
          </el-card>
        </div>
      </el-card>

      <!-- 空状态提示 -->
      <el-empty
        v-if="!answer && !loading"
        description="输入问题后，系统会从场馆知识库中检索相关资料并生成回答"
        class="empty-box"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { askRag } from '@/api/rag'

const route = useRoute()

/**
 * 从路由 query 中读取场馆和场地上下文。
 *
 * 支持三种访问方式：
 * 1. /rag
 * 2. /rag?venueId=1&venueName=羽毛球馆
 * 3. /rag?venueId=1&venueName=羽毛球馆&courtId=101&courtName=1号羽毛球场
 */
const venueId = computed(() => {
  return route.query.venueId ? Number(route.query.venueId) : null
})

const courtId = computed(() => {
  return route.query.courtId ? Number(route.query.courtId) : null
})

const venueName = computed(() => {
  return route.query.venueName || ''
})

const courtName = computed(() => {
  return route.query.courtName || ''
})

/**
 * 当前问答上下文提示文案。
 */
const contextText = computed(() => {
  if (venueName.value && courtName.value) {
    return `当前问答范围：${venueName.value} - ${courtName.value}`
  }

  if (venueName.value) {
    return `当前问答范围：${venueName.value}`
  }

  return ''
})

/**
 * 用户问题。
 */
const question = ref('')

/**
 * 模型回答。
 */
const answer = ref('')

/**
 * 引用来源。
 */
const sources = ref([])

/**
 * 加载状态。
 */
const loading = ref(false)

/**
 * 常见问题快捷入口。
 */
const quickQuestions = [
  '用户怎么预约场地？',
  '预约取消后会退款吗？',
  '羽毛球馆可以停车吗？',
  '场馆开放时间是什么？',
  '1号篮球场价格怎么算？'
]

/**
 * 设置快捷问题。
 */
const setQuestion = item => {
  question.value = item
}

/**
 * 发起 RAG 问答。
 */
const handleAsk = async () => {
  const text = question.value.trim()

  if (!text) {
    ElMessage.warning('请输入问题')
    return
  }

  loading.value = true
  answer.value = ''
  sources.value = []

  try {
    const payload = {
      question: text
    }

    /**
     * 如果当前页面带了 venueId，就传给后端。
     * 后端会优先检索当前场馆相关知识。
     */
    if (venueId.value) {
      payload.venueId = venueId.value
    }

    /**
     * 如果当前页面带了 courtId，也传给后端。
     * 后端会优先检索当前具体场地相关知识。
     */
    if (courtId.value) {
      payload.courtId = courtId.value
    }

    const res = await askRag(payload)

    answer.value = res.data?.answer || '知识库暂无相关信息。'
    sources.value = res.data?.sources || []

    if (!sources.value.length) {
      ElMessage.info('本次回答没有命中明确的参考来源')
    }
  } catch (error) {
    console.error('RAG 问答失败：', error)
  } finally {
    loading.value = false
  }
}

/**
 * 清空当前问答。
 */
const handleClear = () => {
  question.value = ''
  answer.value = ''
  sources.value = []
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

/**
 * 格式化知识范围。
 */
const formatKnowledgeScope = scope => {
  const map = {
    1: '平台级知识',
    2: '场馆级知识',
    3: '场地级知识',
    4: '公告级知识',
    5: '常见问题'
  }

  return map[scope] || '未知来源'
}

/**
 * 拆分 tags。
 */
const splitTags = tags => {
  if (!tags) {
    return []
  }

  return tags
    .split(',')
    .map(item => item.trim())
    .filter(Boolean)
}

/**
 * 来源列表 key。
 */
const getSourceKey = (source, index) => {
  return `${source.docId || 'doc'}-${source.courtId || 'court'}-${source.venueId || 'venue'}-${index}`
}
</script>

<style scoped>
.rag-page {
  min-height: calc(100vh - 80px);
  padding: 24px;
  background: #f5f7fa;
}

.rag-container {
  max-width: 960px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 600;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.context-alert {
  margin-bottom: 16px;
}

.ask-card,
.answer-card,
.source-card-wrapper {
  margin-bottom: 18px;
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.quick-questions {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.quick-title {
  color: #606266;
  font-size: 14px;
}

.quick-tag {
  cursor: pointer;
}

.actions {
  margin-top: 18px;
  display: flex;
  gap: 10px;
}

.answer-content {
  white-space: pre-wrap;
  line-height: 1.9;
  color: #303133;
  font-size: 15px;
}

.source-count {
  color: #909399;
  font-size: 13px;
  font-weight: normal;
}

.source-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.source-card {
  border-radius: 10px;
}

.source-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.source-title {
  font-weight: 600;
  color: #303133;
}

.source-meta {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: #909399;
  font-size: 13px;
}

.source-tags {
  margin-top: 8px;
}

.tag-item {
  margin-right: 6px;
  margin-bottom: 6px;
}

.source-preview {
  margin-top: 10px;
  color: #606266;
  line-height: 1.7;
  font-size: 14px;
}

.empty-box {
  margin-top: 40px;
}
</style>