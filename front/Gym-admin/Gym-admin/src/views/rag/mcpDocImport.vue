<template>
  <div class="mcp-doc-import-page">
    <el-card shadow="never">
      <template #header>
        <div class="page-header">
          <div>
            <div class="page-title">MCP 文档导入 RAG</div>
            <div class="page-subtitle">
              从 docs-root 目录读取 Markdown / TXT 文档，选择章节后导入 RAG 向量知识库。
            </div>
          </div>

          <el-button type="primary" @click="loadFiles">
            刷新文件
          </el-button>
        </div>
      </template>

      <el-row :gutter="16">
        <!-- 左侧文件列表 -->
        <el-col :span="8">
          <el-card shadow="never" class="panel-card">
            <template #header>
              <div class="section-title">文档文件</div>
            </template>

            <el-table
              v-loading="fileLoading"
              :data="fileList"
              border
              size="small"
              highlight-current-row
              @row-click="handleSelectFile"
            >
              <el-table-column prop="fileName" label="文件名" min-width="160" />
              <el-table-column prop="size" label="大小" width="90">
                <template #default="{ row }">
                  {{ formatSize(row.size) }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <!-- 右侧章节预览 -->
        <el-col :span="16">
          <el-card shadow="never" class="panel-card">
            <template #header>
              <div class="section-header">
                <div>
                  <div class="section-title">章节预览</div>
                  <div class="current-file">
                    当前文件：{{ currentFile ? currentFile.relativePath : '未选择' }}
                  </div>
                </div>

                <el-button
                  type="success"
                  :disabled="!currentFile || selectedSectionIds.length === 0"
                  :loading="importLoading"
                  @click="openImportDialog"
                >
                  导入选中章节到 RAG
                </el-button>
              </div>
            </template>

            <el-empty
              v-if="!currentFile"
              description="请先选择左侧文件"
            />

            <template v-else>
              <el-table
                v-loading="sectionLoading"
                :data="sectionList"
                border
                size="small"
                @selection-change="handleSelectionChange"
              >
                <el-table-column type="selection" width="50" />

                <el-table-column prop="sectionId" label="ID" width="80" />

                <el-table-column prop="title" label="章节标题" min-width="160" />

                <el-table-column label="字符数" width="100">
                  <template #default="{ row }">
                    {{ row.length }}
                  </template>
                </el-table-column>

                <el-table-column prop="preview" label="内容预览" min-width="320" />

                <el-table-column type="expand">
                  <template #default="{ row }">
                    <pre class="content-preview">{{ row.content }}</pre>
                  </template>
                </el-table-column>
              </el-table>
            </template>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 导入确认弹窗 -->
    <el-dialog
      v-model="importDialog.visible"
      title="导入 RAG 确认"
      width="520px"
    >
      <el-form :model="importDialog.form" label-width="100px">
        <el-form-item label="导入标题">
          <el-input
            v-model="importDialog.form.title"
            placeholder="例如：场馆补充知识"
          />
        </el-form-item>

        <el-form-item label="分类">
          <el-input
            v-model="importDialog.form.category"
            placeholder="例如：场馆知识 / 预约规则 / 退款规则"
          />
        </el-form-item>

        <el-form-item label="文件">
          <el-text type="primary">
            {{ currentFile ? currentFile.relativePath : '-' }}
          </el-text>
        </el-form-item>

        <el-form-item label="章节数">
          {{ selectedSectionIds.length }}
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="importDialog.visible = false">
          取消
        </el-button>
        <el-button
          type="primary"
          :loading="importLoading"
          @click="submitImport"
        >
          确认导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getMcpDocFiles,
  previewMcpDoc,
  importMcpDocToRag
} from '@/api/mcpDocRagImport'

const fileLoading = ref(false)
const sectionLoading = ref(false)
const importLoading = ref(false)

const fileList = ref([])
const sectionList = ref([])
const currentFile = ref(null)
const selectedSectionIds = ref([])

const importDialog = reactive({
  visible: false,
  form: {
    title: '',
    category: ''
  }
})

/**
 * 兼容项目里的 Result 包装。
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
 * 加载 docs-root 下的文件。
 */
async function loadFiles() {
  fileLoading.value = true

  try {
    const res = await getMcpDocFiles()
    fileList.value = unwrapResult(res) || []
  } catch (e) {
    console.error(e)
    ElMessage.error('加载 MCP 文档文件失败')
  } finally {
    fileLoading.value = false
  }
}

/**
 * 选择文件后加载章节预览。
 */
async function handleSelectFile(row) {
  currentFile.value = row
  sectionList.value = []
  selectedSectionIds.value = []

  sectionLoading.value = true

  try {
    const res = await previewMcpDoc(row.relativePath)
    sectionList.value = unwrapResult(res) || []
  } catch (e) {
    console.error(e)
    ElMessage.error('预览文件失败')
  } finally {
    sectionLoading.value = false
  }
}

/**
 * 表格勾选变化。
 */
function handleSelectionChange(rows) {
  selectedSectionIds.value = rows.map(item => item.sectionId)
}

/**
 * 打开导入确认弹窗。
 */
function openImportDialog() {
  if (!currentFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }

  if (selectedSectionIds.value.length === 0) {
    ElMessage.warning('请选择要导入的章节')
    return
  }

  importDialog.form.title = currentFile.value.fileName
  importDialog.form.category = ''
  importDialog.visible = true
}

/**
 * 确认导入。
 */
async function submitImport() {
  if (!currentFile.value) {
    return
  }

  importLoading.value = true

  try {
    const res = await importMcpDocToRag({
      relativePath: currentFile.value.relativePath,
      sectionIds: selectedSectionIds.value,
      title: importDialog.form.title,
      category: importDialog.form.category
    })

    const data = unwrapResult(res) || {}

    ElMessage.success(data.message || '导入成功')

    importDialog.visible = false
  } catch (e) {
    console.error(e)
    ElMessage.error('导入 RAG 失败')
  } finally {
    importLoading.value = false
  }
}

/**
 * 文件大小格式化。
 */
function formatSize(size) {
  if (!size) {
    return '0 B'
  }

  if (size < 1024) {
    return size + ' B'
  }

  if (size < 1024 * 1024) {
    return (size / 1024).toFixed(1) + ' KB'
  }

  return (size / 1024 / 1024).toFixed(1) + ' MB'
}

onMounted(() => {
  loadFiles()
})
</script>

<style scoped>
.mcp-doc-import-page {
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

.panel-card {
  min-height: 620px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
}

.current-file {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.content-preview {
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  font-family: Consolas, Menlo, Monaco, monospace;
  font-size: 13px;
  line-height: 1.6;
}
</style>