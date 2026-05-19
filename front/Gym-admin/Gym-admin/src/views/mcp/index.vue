<template>
  <div class="mcp-agent-page">
    <el-card shadow="never">
      <template #header>
        <div class="page-header">
          <div>
            <div class="page-title">MCP 项目助手</div>
            <div class="page-subtitle">
              后台管理员专用，用于测试 MCP 外部工具调用能力。第一版仅用于 MCP 测试，不影响用户前台 Agent。
            </div>
          </div>

          <el-button @click="clearMessages">
            清空对话
          </el-button>
        </div>
      </template>

      <!-- 对话区域 -->
      <div class="chat-box" ref="chatBoxRef">
        <div
          v-for="(item, index) in messages"
          :key="index"
          class="message-row"
          :class="item.role === 'user' ? 'message-user' : 'message-assistant'"
        >
          <div class="message-card">
            <div class="message-role">
              {{ item.role === 'user' ? '管理员' : 'MCP Agent' }}
            </div>

            <pre class="message-content">{{ item.content }}</pre>
          </div>
        </div>

        <el-empty
          v-if="messages.length === 0"
          description="暂无对话。可以输入 MCP 测试问题。"
        />
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入MCP问题"
          @keydown.enter.exact.prevent="sendMessage"
        />

        <div class="input-actions">
          <div class="input-tip">
            Enter 发送，Shift + Enter 换行
          </div>

          <el-button
            type="primary"
            :loading="loading"
            @click="sendMessage"
          >
            发送
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { chatWithMcpAgent } from '@/api/mcpAgent'

/**
 * 聊天消息列表。
 *
 * role = user：管理员输入
 * role = assistant：MCP Agent 回复
 */
const messages = ref([])

/**
 * 输入框内容。
 */
const inputMessage = ref('')

/**
 * 接口请求 loading。
 */
const loading = ref(false)

/**
 * 聊天区域 DOM，用于自动滚动到底部。
 */
const chatBoxRef = ref(null)

/**
 * 第一版快捷问题。
 *
 * 注意：
 * 这些问题依赖你当前接入的 MCP Server。
 * 如果你用的是 server-everything，可以测试 echo / add 这类工具。
 * 后续接 GitHub MCP 后，可以把这些改成 GitHub 相关问题。
 */
const quickQuestions = [
  '请调用 echo 工具返回 hello gym',
  '请计算 2 + 3',
  '请说明当前 MCP 工具可以做什么'
]

/**
 * 兼容后端 Result 包装。
 *
 * 有些项目 axios 拦截器会直接返回 data；
 * 有些会返回完整响应对象。
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
 * 使用快捷测试问题。
 */
function useQuickQuestion(question) {
  inputMessage.value = question
}

/**
 * 发送消息。
 */
async function sendMessage() {
  const text = inputMessage.value.trim()

  if (!text) {
    ElMessage.warning('请输入问题')
    return
  }

  /*
   * 先把管理员消息追加到页面。
   */
  messages.value.push({
    role: 'user',
    content: text
  })

  inputMessage.value = ''
  loading.value = true

  await scrollToBottom()

  try {
    /*
     * 调用后台 MCP Agent 接口。
     */
    const res = await chatWithMcpAgent({
      message: text
    })

    const data = unwrapResult(res) || {}

    /*
     * 后端返回格式：
     * {
     *   reply: "xxx"
     * }
     */
    const reply = data.reply || 'MCP Agent 未返回内容。'

    messages.value.push({
      role: 'assistant',
      content: reply
    })

    await scrollToBottom()
  } catch (e) {
    console.error(e)

    messages.value.push({
      role: 'assistant',
      content: 'MCP Agent 调用失败，请检查后端日志、MCP Server 是否启动、Node/npx 是否可用。'
    })

    ElMessage.error('MCP Agent 调用失败')
  } finally {
    loading.value = false
  }
}

/**
 * 清空当前页面对话。
 *
 * 注意：
 * 这里只清空前端展示，不清空后端 ChatMemory。
 * 如果后续需要清空后端记忆，可以再加一个后端接口。
 */
function clearMessages() {
  messages.value = []
}

/**
 * 滚动到底部。
 */
async function scrollToBottom() {
  await nextTick()

  if (chatBoxRef.value) {
    chatBoxRef.value.scrollTop = chatBoxRef.value.scrollHeight
  }
}
</script>

<style scoped>
.mcp-agent-page {
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

.top-alert {
  margin-bottom: 16px;
}

.quick-area {
  margin-bottom: 16px;
}

.quick-title {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.chat-box {
  height: 480px;
  padding: 16px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #f5f7fa;
}

.message-row {
  display: flex;
  margin-bottom: 14px;
}

.message-user {
  justify-content: flex-end;
}

.message-assistant {
  justify-content: flex-start;
}

.message-card {
  max-width: 72%;
  padding: 12px;
  border-radius: 10px;
  background: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.message-user .message-card {
  background: #ecf5ff;
}

.message-role {
  margin-bottom: 6px;
  font-size: 12px;
  font-weight: 700;
  color: #409eff;
}

.message-content {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Menlo, Monaco, monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #303133;
}

.input-area {
  margin-top: 16px;
}

.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.input-tip {
  font-size: 12px;
  color: #909399;
}
</style>