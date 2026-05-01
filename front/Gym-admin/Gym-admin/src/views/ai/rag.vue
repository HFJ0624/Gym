<template>
  <div class="rag-page">
    <!-- 顶部说明区域 -->
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h2>RAG 知识库管理</h2>
            <p>
              用于维护体育场馆系统的知识文档，并将知识切分、向量化后写入 PostgreSQL + pgvector。
            </p>
          </div>

          <div class="header-actions">
            <el-button type="primary" @click="openCreateDialog">
              新增知识
            </el-button>

            <el-button
              type="warning"
              :loading="rebuildLoading"
              @click="handleRebuild"
            >
              重建知识库索引
            </el-button>
            <el-button
                type="success"
                :loading="syncVenueLoading"
                @click="handleSyncVenue"
              >
              同步场馆知识
            </el-button>
          </div>
        </div>
      </template>

      <el-alert
        title="说明：新增或修改知识后，indexed_status 会变为未索引。需要点击“重建知识库索引”后，前台 RAG 问答才能检索到最新知识。"
        type="info"
        show-icon
        :closable="false"
      />
    </el-card>

    <!-- 查询条件 -->
    <el-card class="page-card" shadow="never">
      <el-form
        :model="queryForm"
        label-width="90px"
        class="query-form"
      >
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="知识标题">
              <el-input
                v-model="queryForm.title"
                placeholder="请输入知识标题"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="知识范围">
              <el-select
                v-model="queryForm.knowledgeScope"
                placeholder="全部"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in knowledgeScopeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="来源类型">
              <el-select
                v-model="queryForm.sourceType"
                placeholder="全部"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in sourceTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="启用状态">
              <el-select
                v-model="queryForm.enabled"
                placeholder="全部"
                clearable
                style="width: 100%"
              >
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="索引状态">
              <el-select
                v-model="queryForm.indexedStatus"
                placeholder="全部"
                clearable
                style="width: 100%"
              >
                <el-option label="已索引" :value="1" />
                <el-option label="未索引" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="场馆ID">
              <el-input-number
                v-model="queryForm.venueId"
                :min="1"
                :controls="false"
                placeholder="可选"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="场地ID">
              <el-input-number
                v-model="queryForm.courtId"
                :min="1"
                :controls="false"
                placeholder="可选"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                查询
              </el-button>

              <el-button @click="handleResetQuery">
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 知识列表 -->
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h3>知识文档列表</h3>
            <p>
              当前列表展示 MySQL 中的 knowledge_document 原始知识。是否能被 RAG 检索，取决于是否已重建索引。
            </p>
          </div>
        </div>
      </template>

      <el-table
        v-loading="tableLoading"
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
          prop="title"
          label="知识标题"
          min-width="180"
          show-overflow-tooltip
        />

        <el-table-column
          label="知识范围"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            <el-tag type="primary">
              {{ formatKnowledgeScope(row.knowledgeScope) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          label="来源类型"
          width="120"
          align="center"
        >
          <template #default="{ row }">
            {{ formatSourceType(row.sourceType) }}
          </template>
        </el-table-column>

        <el-table-column
          prop="venueName"
          label="场馆"
          width="150"
          show-overflow-tooltip
        />

        <el-table-column
          prop="courtName"
          label="场地"
          width="150"
          show-overflow-tooltip
        />

        <el-table-column
          prop="courtType"
          label="场地类型"
          width="110"
          show-overflow-tooltip
        />

        <el-table-column
          prop="topic"
          label="主题"
          width="140"
          show-overflow-tooltip
        />

        <el-table-column
          prop="priority"
          label="优先级"
          width="90"
          align="center"
        />

        <el-table-column
          label="启用"
          width="90"
          align="center"
        >
          <template #default="{ row }">
            <el-switch
              :model-value="row.enabled"
              :active-value="1"
              :inactive-value="0"
              @change="value => handleEnabledChange(row, value)"
            />
          </template>
        </el-table-column>

        <el-table-column
          label="索引状态"
          width="110"
          align="center"
        >
          <template #default="{ row }">
            <el-tag v-if="row.indexedStatus === 1" type="success">
              已索引
            </el-tag>

            <el-tag v-else type="danger">
              未索引
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          label="更新时间"
          width="180"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ formatDateTime(row.updateTime) }}
          </template>
        </el-table-column>

        <el-table-column
          label="操作"
          width="220"
          fixed="right"
          align="center"
        >
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="openEditDialog(row)"
            >
              编辑
            </el-button>

            <el-button
              type="info"
              link
              @click="openDetailDialog(row)"
            >
              查看
            </el-button>

            <el-button
              type="danger"
              link
              @click="handleDelete(row)"
            >
              删除
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
          @size-change="loadTableData"
          @current-change="loadTableData"
        />
      </div>
    </el-card>

    <!-- RAG 测试区域 -->
    <el-card class="page-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h3>RAG 问答测试</h3>
            <p>
              用于测试知识库是否能正常召回。测试前请确认知识已经完成索引重建。
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
            :controls="false"
            placeholder="可选"
            style="width: 220px"
          />

          <span class="form-tip">
            可选。填写后后端会优先检索该场馆知识。
          </span>
        </el-form-item>

        <el-form-item label="场地ID">
          <el-input-number
            v-model="testForm.courtId"
            :min="1"
            :controls="false"
            placeholder="可选"
            style="width: 220px"
          />

          <span class="form-tip">
            可选。填写后后端会优先检索该场地知识。
          </span>
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
            show-overflow-tooltip
          />

          <el-table-column
            prop="knowledgeScopeName"
            label="知识范围"
            width="120"
          />

          <el-table-column
            prop="venueName"
            label="场馆"
            width="150"
            show-overflow-tooltip
          />

          <el-table-column
            prop="courtName"
            label="场地"
            width="150"
            show-overflow-tooltip
          />

          <el-table-column
            prop="topic"
            label="主题"
            width="130"
            show-overflow-tooltip
          />

          <el-table-column
            label="相似度"
            width="100"
            align="center"
          >
            <template #default="{ row }">
              {{ formatScore(row.score) }}
            </template>
          </el-table-column>

          <el-table-column
            prop="contentPreview"
            label="命中文本"
            min-width="260"
            show-overflow-tooltip
          />
        </el-table>
      </div>
    </el-card>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="formDialogTitle"
      width="860px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="知识标题" prop="title">
              <el-input
                v-model="form.title"
                maxlength="200"
                show-word-limit
                placeholder="例如：平台预约规则"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="知识范围" prop="knowledgeScope">
              <el-select
                v-model="form.knowledgeScope"
                placeholder="请选择知识范围"
                style="width: 100%"
              >
                <el-option
                  v-for="item in knowledgeScopeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="来源类型" prop="sourceType">
              <el-select
                v-model="form.sourceType"
                placeholder="请选择来源类型"
                style="width: 100%"
              >
                <el-option
                  v-for="item in sourceTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="场馆ID">
              <el-input-number
                v-model="form.venueId"
                :min="1"
                :controls="false"
                placeholder="平台级知识可不填"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="场馆名称">
              <el-input
                v-model="form.venueName"
                placeholder="例如：羽毛球馆"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="场地ID">
              <el-input-number
                v-model="form.courtId"
                :min="1"
                :controls="false"
                placeholder="非场地级知识可不填"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="场地名称">
              <el-input
                v-model="form.courtName"
                placeholder="例如：1号羽毛球场"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="场地类型">
              <el-input
                v-model="form.courtType"
                placeholder="例如：篮球场、足球场、羽毛球场"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="公告ID">
              <el-input-number
                v-model="form.noticeId"
                :min="1"
                :controls="false"
                placeholder="公告知识可填写"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="主题">
              <el-input
                v-model="form.topic"
                placeholder="例如：预约规则、停车说明、场地价格"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="标签">
              <el-input
                v-model="form.tags"
                placeholder="多个标签用英文逗号分隔"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="优先级">
              <el-input-number
                v-model="form.priority"
                :min="0"
                :controls="false"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="是否启用">
              <el-switch
                v-model="form.enabled"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="知识正文" prop="content">
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="8"
                maxlength="5000"
                show-word-limit
                placeholder="请输入知识正文。这里的内容会被切分成 chunk 后写入向量库。"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="formDialogVisible = false">
          取消
        </el-button>

        <el-button
          type="primary"
          :loading="submitLoading"
          @click="handleSubmitForm"
        >
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 查看详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="知识详情"
      width="820px"
    >
      <el-descriptions
        :column="2"
        border
      >
        <el-descriptions-item label="ID">
          {{ detail.id }}
        </el-descriptions-item>

        <el-descriptions-item label="标题">
          {{ detail.title }}
        </el-descriptions-item>

        <el-descriptions-item label="知识范围">
          {{ formatKnowledgeScope(detail.knowledgeScope) }}
        </el-descriptions-item>

        <el-descriptions-item label="来源类型">
          {{ formatSourceType(detail.sourceType) }}
        </el-descriptions-item>

        <el-descriptions-item label="场馆">
          {{ detail.venueName || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="场地">
          {{ detail.courtName || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="主题">
          {{ detail.topic || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="标签">
          {{ detail.tags || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="启用状态">
          {{ detail.enabled === 1 ? '启用' : '禁用' }}
        </el-descriptions-item>

        <el-descriptions-item label="索引状态">
          {{ detail.indexedStatus === 1 ? '已索引' : '未索引' }}
        </el-descriptions-item>

        <el-descriptions-item label="创建时间">
          {{ formatDateTime(detail.createTime) }}
        </el-descriptions-item>

        <el-descriptions-item label="更新时间">
          {{ formatDateTime(detail.updateTime) }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="detail-content">
        <h4>知识正文</h4>
        <p>{{ detail.content }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageKnowledgeDocument,
  getKnowledgeDocumentDetail,
  saveKnowledgeDocument,
  updateKnowledgeDocument,
  updateKnowledgeDocumentEnabled,
  deleteKnowledgeDocument,
  rebuildRagKnowledge,
  testRagAsk,
  syncVenueKnowledge
} from '@/api/rag'

/**
 * 知识范围选项。
 *
 * 1 平台级：预约规则、退款规则等通用知识
 * 2 场馆级：某个场馆的停车、开放时间、设施说明
 * 3 场地级：某个具体场地的价格、设施、注意事项
 * 4 公告级：系统公告、维护通知
 * 5 FAQ：常见问题
 */
const knowledgeScopeOptions = [
  { label: '平台级知识', value: 1 },
  { label: '场馆级知识', value: 2 },
  { label: '场地级知识', value: 3 },
  { label: '公告级知识', value: 4 },
  { label: '常见问题', value: 5 }
]

/**
 * 来源类型选项。
 */
const sourceTypeOptions = [
  { label: '平台规则', value: 1 },
  { label: '预约规则', value: 2 },
  { label: '退款规则', value: 3 },
  { label: '场馆介绍', value: 4 },
  { label: '场馆设施', value: 5 },
  { label: '停车说明', value: 6 },
  { label: '开放时间', value: 7 },
  { label: '场地介绍', value: 8 },
  { label: '场地设施', value: 9 },
  { label: '场地价格', value: 10 },
  { label: '公告', value: 11 },
  { label: 'FAQ', value: 12 }
]

/**
 * 查询表单。
 */
const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  knowledgeScope: undefined,
  sourceType: undefined,
  venueId: undefined,
  courtId: undefined,
  enabled: undefined,
  indexedStatus: undefined
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
 * 表格加载状态。
 */
const tableLoading = ref(false)

/**
 * 重建索引状态。
 */
const rebuildLoading = ref(false)

/**
 * 弹窗状态。
 */
const formDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const submitLoading = ref(false)

/**
 * 当前是否编辑模式。
 */
const isEdit = ref(false)

/**
 * 表单引用。
 */
const formRef = ref(null)

/**
 * 表单标题。
 */
const formDialogTitle = computed(() => {
  return isEdit.value ? '编辑知识文档' : '新增知识文档'
})

/**
 * 表单数据。
 */
const form = reactive(getDefaultForm())

/**
 * 详情数据。
 */
const detail = reactive({})

/**
 * 表单校验规则。
 */
const formRules = {
  title: [
    { required: true, message: '请输入知识标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入知识正文', trigger: 'blur' }
  ],
  knowledgeScope: [
    { required: true, message: '请选择知识范围', trigger: 'change' }
  ],
  sourceType: [
    { required: true, message: '请选择来源类型', trigger: 'change' }
  ]
}

/**
 * 测试问答表单。
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
 * 测试加载状态。
 */
const testLoading = ref(false)

/**
 * 初始化。
 */
onMounted(() => {
  loadTableData()
})

/**
 * 加载知识列表。
 */
const loadTableData = async () => {
  tableLoading.value = true

  try {
    const res = await pageKnowledgeDocument(cleanQueryParams(queryForm))
    const data = unwrapData(res)

    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    tableLoading.value = false
  }
}

/**
 * 查询。
 */
const handleSearch = () => {
  queryForm.pageNum = 1
  loadTableData()
}

/**
 * 重置查询。
 */
const handleResetQuery = () => {
  queryForm.pageNum = 1
  queryForm.pageSize = 10
  queryForm.title = ''
  queryForm.knowledgeScope = undefined
  queryForm.sourceType = undefined
  queryForm.venueId = undefined
  queryForm.courtId = undefined
  queryForm.enabled = undefined
  queryForm.indexedStatus = undefined

  loadTableData()
}

/**
 * 打开新增弹窗。
 */
const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  formDialogVisible.value = true

  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

/**
 * 打开编辑弹窗。
 */
const openEditDialog = async row => {
  isEdit.value = true
  resetForm()

  /**
   * 优先调用详情接口，保证拿到完整 content。
   * 如果你的列表已经返回 content，也可以直接用 row。
   */
  const res = await getKnowledgeDocumentDetail(row.id)
  const data = unwrapData(res)

  Object.assign(form, {
    id: data.id,
    title: data.title,
    content: data.content,
    knowledgeScope: data.knowledgeScope,
    sourceType: data.sourceType,
    venueId: data.venueId,
    venueName: data.venueName,
    courtId: data.courtId,
    courtName: data.courtName,
    courtType: data.courtType,
    noticeId: data.noticeId,
    topic: data.topic,
    tags: data.tags,
    priority: data.priority ?? 0,
    enabled: data.enabled ?? 1
  })

  formDialogVisible.value = true

  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

/**
 * 打开详情弹窗。
 */
const openDetailDialog = async row => {
  const res = await getKnowledgeDocumentDetail(row.id)
  const data = unwrapData(res)

  Object.keys(detail).forEach(key => delete detail[key])
  Object.assign(detail, data)

  detailDialogVisible.value = true
}

/**
 * 提交新增/编辑表单。
 */
const handleSubmitForm = async () => {
  await formRef.value.validate()

  submitLoading.value = true

  try {
    const payload = cleanFormParams(form)

    if (isEdit.value) {
      await updateKnowledgeDocument(payload)
      ElMessage.success('更新成功')
    } else {
      await saveKnowledgeDocument(payload)
      ElMessage.success('新增成功')
    }

    formDialogVisible.value = false
    await loadTableData()
  } finally {
    submitLoading.value = false
  }
}

/**
 * 启用/禁用知识。
 *
 * 注意：
 * 启用状态变化后 indexed_status 会被后端重置为 0，
 * 需要重新点击“重建知识库索引”。
 */
const handleEnabledChange = async (row, value) => {
  const oldValue = row.enabled

  try {
    await updateKnowledgeDocumentEnabled(row.id, value)
    row.enabled = value
    row.indexedStatus = 0

    ElMessage.success(value === 1 ? '已启用' : '已禁用')
  } catch (error) {
    row.enabled = oldValue
    throw error
  }
}

/**
 * 删除知识。
 */
const handleDelete = async row => {
  await ElMessageBox.confirm(
    `确定要删除知识「${row.title}」吗？删除后需要重建索引，才能同步到向量库。`,
    '删除确认',
    {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    }
  )

  await deleteKnowledgeDocument(row.id)

  ElMessage.success('删除成功')

  /**
   * 如果当前页只有一条数据且不是第一页，删除后回到上一页。
   */
  if (tableData.value.length === 1 && queryForm.pageNum > 1) {
    queryForm.pageNum--
  }

  await loadTableData()
}

/**
 * 重建知识库索引。
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
     * 重建成功后刷新列表，查看 indexed_status 是否变成 1。
     */
    await loadTableData()
  } finally {
    rebuildLoading.value = false
  }
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

    if (testForm.venueId) {
      payload.venueId = testForm.venueId
    }

    if (testForm.courtId) {
      payload.courtId = testForm.courtId
    }

    const res = await testRagAsk(payload)
    const data = unwrapData(res)

    testResult.answer = data?.answer || '知识库暂无相关信息。'
    testResult.sources = data?.sources || []

    if (testResult.sources.length === 0) {
      ElMessage.info('本次没有命中参考来源，请检查知识是否已索引，或适当降低 min-score。')
    }
  } finally {
    testLoading.value = false
  }
}

/**
 * 清空测试。
 */
const handleClearTest = () => {
  testForm.question = ''
  testForm.venueId = undefined
  testForm.courtId = undefined
  testResult.answer = ''
  testResult.sources = []
}

/**
 * 默认表单。
 */
function getDefaultForm() {
  return {
    id: undefined,
    title: '',
    content: '',
    knowledgeScope: undefined,
    sourceType: undefined,
    venueId: undefined,
    venueName: '',
    courtId: undefined,
    courtName: '',
    courtType: '',
    noticeId: undefined,
    topic: '',
    tags: '',
    priority: 0,
    enabled: 1
  }
}

/**
 * 重置表单。
 */
function resetForm() {
  Object.assign(form, getDefaultForm())
}

/**
 * 清理查询参数。
 *
 * 避免把空字符串传给后端影响动态 SQL。
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
 * 清理表单参数。
 */
function cleanFormParams(params) {
  const payload = {}

  Object.keys(params).forEach(key => {
    const value = params[key]

    if (value !== undefined) {
      payload[key] = value
    }
  })

  return payload
}

/**
 * 兼容不同 request 封装。
 *
 * 有的项目返回：
 * res.data
 *
 * 有的项目返回：
 * res.data.data
 *
 * 有的项目拦截器直接返回：
 * data
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
 * 格式化知识范围。
 */
function formatKnowledgeScope(value) {
  const item = knowledgeScopeOptions.find(option => option.value === value)
  return item ? item.label : '未知'
}

/**
 * 格式化来源类型。
 */
function formatSourceType(value) {
  const item = sourceTypeOptions.find(option => option.value === value)
  return item ? item.label : '未知'
}

/**
 * 格式化相似度。
 */
function formatScore(score) {
  if (score === null || score === undefined) {
    return '-'
  }

  return Number(score).toFixed(4)
}

/**
 * 格式化日期。
 */
function formatDateTime(value) {
  if (!value) {
    return '-'
  }

  /**
   * 如果后端直接返回字符串，直接展示。
   */
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
  } catch (error) {
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
 * 同步场馆知识加载状态。
 */
const syncVenueLoading = ref(false)

/**
 * 同步场馆数据到 RAG 知识库。
 *
 * 注意：
 * 这个操作只会同步到 MySQL 的 knowledge_document。
 * 同步后 indexed_status = 0，需要再点击“重建知识库索引”。
 */
const handleSyncVenue = async () => {
  await ElMessageBox.confirm(
    '确定要从 venue 表同步场馆知识吗？同步后需要重建知识库索引，前台 RAG 才能检索到最新内容。',
    '同步确认',
    {
      type: 'warning',
      confirmButtonText: '确定同步',
      cancelButtonText: '取消'
    }
  )

  syncVenueLoading.value = true

  try {
    await syncVenueKnowledge()
    ElMessage.success('场馆知识同步成功，请继续点击“重建知识库索引”')

    /**
     * 同步后刷新列表，可以看到新生成的场馆知识。
     */
    await loadTableData()
  } finally {
    syncVenueLoading.value = false
  }
}
</script>

<style scoped>
.rag-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%;
}

.page-card {
  margin-bottom: 20px;
  border-radius: 10px;
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.card-header h2,
.card-header h3 {
  margin: 0 0 8px;
  font-weight: 600;
  color: #303133;
}

.card-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.header-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.query-form {
  margin-bottom: -18px;
}

.pagination-box {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
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
  color: #303133;
}

.answer-box p {
  margin: 0;
  white-space: pre-wrap;
  color: #303133;
}

.source-box {
  margin-top: 20px;
}

.source-box h4 {
  margin: 0 0 12px;
  color: #303133;
}

.detail-content {
  margin-top: 20px;
}

.detail-content h4 {
  margin: 0 0 10px;
  color: #303133;
}

.detail-content p {
  margin: 0;
  padding: 14px;
  background: #f5f7fa;
  border-radius: 8px;
  white-space: pre-wrap;
  line-height: 1.8;
  color: #303133;
}
</style>