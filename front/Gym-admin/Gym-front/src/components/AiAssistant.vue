<template>
  <div class="ai-assistant">
    <!-- 悬浮 AI 小球 -->
    <div class="float-btn" @click="showChat = !showChat">
      <span class="icon">AI</span>
    </div>

    <!-- 聊天窗口 -->
    <div v-show="showChat" class="chat-panel">
      <!-- 头部 -->
      <div class="chat-header">
        体育场馆智能助手
      </div>

      <!-- 聊天内容 -->
      <div ref="chatBody" class="chat-body">
        <div
          v-for="(msg, idx) in messages"
          :key="idx"
          class="msg-row"
          :class="msg.role"
        >
          <div class="msg-bubble">
            <!-- 普通文本消息 -->
            <div
              v-if="!msg.draft"
              class="msg-content"
            >
              {{ msg.content }}
            </div>

            <!-- 草稿卡片消息 -->
            <div
              v-else
              class="draft-card"
            >
              <div class="draft-title">
                {{ msg.draft.title }}
              </div>

              <!-- 预约草稿 -->
              <template v-if="msg.draft.type === 'BOOKING'">
                <div class="draft-row">
                  <span class="label">场馆</span>
                  <span class="value">{{ msg.draft.venueName || '-' }}</span>
                </div>

                <div class="draft-row">
                  <span class="label">场地</span>
                  <span class="value">{{ msg.draft.courtName || '-' }}</span>
                </div>

                <div class="draft-row">
                  <span class="label">类型</span>
                  <span class="value">{{ msg.draft.courtType || '-' }}</span>
                </div>

                <div class="draft-row">
                  <span class="label">日期</span>
                  <span class="value">{{ msg.draft.date || '-' }}</span>
                </div>

                <div class="draft-row">
                  <span class="label">时间</span>
                  <span class="value">
                    {{ msg.draft.startTime || '-' }} - {{ msg.draft.endTime || '-' }}
                  </span>
                </div>

                <div class="draft-row">
                  <span class="label">单价</span>
                  <span class="value">
                    {{ msg.draft.hourPrice || '-' }} 元/小时
                  </span>
                </div>

                <div class="draft-row total">
                  <span class="label">总价</span>
                  <span class="value">
                    ¥{{ msg.draft.totalPrice || '-' }}
                  </span>
                </div>

                <div class="draft-token">
                  确认码：{{ msg.draft.confirmToken }}
                </div>
              </template>

              <!-- 商品下单草稿，预留 -->
              <template v-else-if="msg.draft.type === 'SHOPPING'">
                <div class="draft-row">
                  <span class="label">商品</span>
                  <span class="value">{{ msg.draft.goodsName || '-' }}</span>
                </div>

                <div class="draft-row">
                  <span class="label">数量</span>
                  <span class="value">{{ msg.draft.quantity || '-' }}</span>
                </div>

                <div class="draft-row">
                  <span class="label">单价</span>
                  <span class="value">{{ msg.draft.price || '-' }}</span>
                </div>

                <div class="draft-row total">
                  <span class="label">总价</span>
                  <span class="value">¥{{ msg.draft.totalPrice || '-' }}</span>
                </div>

                <div class="draft-token">
                  确认码：{{ msg.draft.confirmToken }}
                </div>
              </template>
            </div>

            <!-- AI 回复中的确认按钮 -->
            <div
              v-if="msg.role === 'ai' && msg.actions && !msg.actionUsed"
              class="action-box"
            >
              <el-button
                type="primary"
                size="small"
                :loading="msg.actionLoading"
                @click="handleConfirmAction(msg)"
              >
                {{ msg.actions.confirmLabel }}
              </el-button>

              <el-button
                size="small"
                :disabled="msg.actionLoading"
                @click="handleCancelAction(msg)"
              >
                取消
              </el-button>
            </div>

            <!-- 用户已经处理过该草稿 -->
            <div
              v-if="msg.role === 'ai' && msg.actions && msg.actionUsed"
              class="action-used"
            >
              已处理该草稿操作
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入框 -->
      <div class="chat-footer">
        <el-input
          v-model="inputMsg"
          placeholder="提问..."
          :disabled="sending"
          @keyup.enter="send"
        />

        <el-button
          type="primary"
          :loading="sending"
          @click="send"
        >
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuth } from '@/stores/auth'

/**
 * 接收当前页面上下文。
 *
 * 普通页面：
 * <AiAssistant />
 *
 * 场馆详情页：
 * <AiAssistant :venue-id="venueId" />
 *
 * 场地详情页：
 * <AiAssistant :venue-id="venueId" :court-id="courtId" />
 */
const props = defineProps({
  /**
   * 当前页面场馆ID，可选。
   */
  venueId: {
    type: [Number, String],
    default: null
  },

  /**
   * 当前页面场地ID，可选。
   */
  courtId: {
    type: [Number, String],
    default: null
  }
})

/**
 * 是否显示聊天窗口。
 */
const showChat = ref(false)

/**
 * 输入框消息。
 */
const inputMsg = ref('')

/**
 * 聊天内容容器，用于滚动到底部。
 */
const chatBody = ref(null)

/**
 * 是否正在请求后端。
 */
const sending = ref(false)

const authStore = useAuth()

/**
 * 当前登录用户ID。
 *
 * 后端 Agent 记忆、Redis 草稿、确认码校验都依赖 userId。
 */
const userId = computed(() => authStore.user?.id)

/**
 * 聊天消息列表。
 *
 * 每条消息结构：
 * role: user / ai
 * content: 文本内容
 * draft: 草稿卡片数据
 * actions: 确认按钮动作
 * actionUsed: 是否已处理
 * actionLoading: 按钮 loading
 */
const messages = ref([
  {
    role: 'ai',
    content: '你好！我是体育场馆AI客服，有什么可以帮你？',
    draft: null,
    actions: null,
    actionUsed: false,
    actionLoading: false
  }
])

/**
 * 点击发送按钮或回车发送。
 */
const send = async () => {
  const text = inputMsg.value.trim()

  if (!text) {
    return ElMessage.warning('请输入内容')
  }

  await sendMessage(text)

  inputMsg.value = ''
}

/**
 * 统一发送消息方法。
 *
 * 使用场景：
 * 1. 用户手动输入
 * 2. 点击“确认预约”按钮
 * 3. 点击“确认下单”按钮
 * 4. 点击“取消”按钮
 *
 * @param {string} text 要发送给后端的文本
 */
const sendMessage = async text => {
  if (!text || !text.trim()) {
    return
  }

  if (!userId.value) {
    return ElMessage.warning('请先登录后再使用 AI 助手')
  }

  const msg = text.trim()

  /**
   * 先把用户消息展示出来。
   */
  messages.value.push({
    role: 'user',
    content: msg,
    draft: null,
    actions: null,
    actionUsed: false,
    actionLoading: false
  })

  sending.value = true

  try {
    const { data } = await axios.post(
      'http://localhost:9601/front/agent/chat',

      /**
       * 后端 @RequestBody AgentChatDto 接收这里：
       * message / venueId / courtId。
       */
      {
        message: msg,
        venueId: props.venueId ? Number(props.venueId) : null,
        courtId: props.courtId ? Number(props.courtId) : null
      },

      /**
       * 后端 @RequestParam Long userId 接收这里。
       */
      {
        params: {
          userId: userId.value
        },

        /**
         * Agent + RAG + 工具调用可能较慢。
         */
        timeout: 120000
      }
    )

    const replyText = getReplyText(data)

    /**
     * 解析确认动作：
     * 确认预约 123456
     * 确认下单 123456
     */
    const actions = parseConfirmActions(replyText)

    /**
     * 解析草稿卡片：
     * 预约草稿 / 商品草稿。
     */
    const draft = parseDraftCard(replyText, actions)

    messages.value.push({
      role: 'ai',
      content: replyText,
      draft,
      actions,
      actionUsed: false,
      actionLoading: false
    })
  } catch (e) {
    ElMessage.error('连接 AI 服务失败')
    console.error(e)

    messages.value.push({
      role: 'ai',
      content: 'AI服务暂时不可用，请稍后再试。',
      draft: null,
      actions: null,
      actionUsed: false,
      actionLoading: false
    })
  } finally {
    sending.value = false
    scrollToBottom()
  }
}

/**
 * 点击“确认预约 / 确认下单”按钮。
 *
 * 注意：
 * 前端只是自动发送“确认预约 123456”。
 * 真正的 Redis 草稿校验、确认码校验、真实预约执行仍然在后端完成。
 */
const handleConfirmAction = async msg => {
  if (!msg.actions || msg.actionUsed) {
    return
  }

  msg.actionLoading = true

  try {
    /**
     * 先标记已使用，避免用户重复点击。
     */
    msg.actionUsed = true

    await sendMessage(msg.actions.confirmCommand)
  } finally {
    msg.actionLoading = false
  }
}

/**
 * 点击“取消”按钮。
 *
 * 自动发送：
 * 取消
 *
 * 后端会清理当前用户 Redis 草稿。
 */
const handleCancelAction = async msg => {
  if (!msg.actions || msg.actionUsed) {
    return
  }

  msg.actionLoading = true

  try {
    msg.actionUsed = true

    await sendMessage('取消')
  } finally {
    msg.actionLoading = false
  }
}

/**
 * 从后端 Result 中取出真正的回复文本。
 *
 * 兼容几种返回：
 * 1. { data: "xxx" }
 * 2. { data: { answer: "xxx" } }
 * 3. "xxx"
 */
function getReplyText(responseData) {
  if (!responseData) {
    return '暂时没有返回内容。'
  }

  const data = responseData.data

  if (typeof data === 'string') {
    return data
  }

  if (data && typeof data.answer === 'string') {
    return data.answer
  }

  if (typeof responseData === 'string') {
    return responseData
  }

  return JSON.stringify(data || responseData)
}

/**
 * 从 AI 回复文本中解析确认按钮动作。
 *
 * 当前支持：
 * 1. 确认预约 536348
 * 2. 确认下单 536348
 *
 * 返回示例：
 * {
 *   type: 'BOOKING',
 *   token: '536348',
 *   confirmLabel: '确认预约',
 *   confirmCommand: '确认预约 536348'
 * }
 */
function parseConfirmActions(text) {
  if (!text) {
    return null
  }

  /**
   * 匹配预约确认：
   * 确认预约 536348
   * 确认预约：536348
   * 确认预约: 536348
   */
  const bookingMatch = text.match(/确认预约\s*[:：]?\s*(\d{6})/)

  if (bookingMatch && bookingMatch[1]) {
    return {
      type: 'BOOKING',
      token: bookingMatch[1],
      confirmLabel: '确认预约',
      confirmCommand: `确认预约 ${bookingMatch[1]}`
    }
  }

  /**
   * 匹配商品下单确认：
   * 确认下单 536348
   * 确认下单：536348
   */
  const shoppingMatch = text.match(/确认下单\s*[:：]?\s*(\d{6})/)

  if (shoppingMatch && shoppingMatch[1]) {
    return {
      type: 'SHOPPING',
      token: shoppingMatch[1],
      confirmLabel: '确认下单',
      confirmCommand: `确认下单 ${shoppingMatch[1]}`
    }
  }

  return null
}

/**
 * 解析草稿卡片。
 *
 * 只有当 AI 回复中包含确认动作时，才尝试解析草稿。
 *
 * @param {string} text AI 回复文本
 * @param {Object|null} actions 确认动作
 */
function parseDraftCard(text, actions) {
  if (!text || !actions) {
    return null
  }

  if (actions.type === 'BOOKING') {
    return parseBookingDraft(text, actions)
  }

  if (actions.type === 'SHOPPING') {
    return parseShoppingDraft(text, actions)
  }

  return null
}

/**
 * 解析预约草稿。
 *
 * 支持后端文本：
 * 场馆：xxx
 * 场地：xxx
 * 类型：xxx
 * 日期：xxx
 * 开始时间：xxx
 * 结束时间：xxx
 * 单小时价格：xxx
 * 总价：xxx
 * 确认码：xxx
 */
function parseBookingDraft(text, actions) {
  return {
    type: 'BOOKING',
    title: '预约草稿',
    venueName: extractField(text, '场馆') || extractField(text, '场馆名称'),
    courtName: extractField(text, '场地') || extractField(text, '场地名称'),
    courtType: extractField(text, '类型') || extractField(text, '场地类型'),
    date: extractField(text, '日期') || extractField(text, '预约日期'),
    startTime: extractField(text, '开始时间'),
    endTime: extractField(text, '结束时间'),
    hourPrice: extractField(text, '单小时价格') || extractField(text, '单价'),
    totalPrice: extractField(text, '总价'),
    confirmToken: extractField(text, '确认码') || actions.token
  }
}

/**
 * 解析商品下单草稿。
 *
 * 这里先兼容常见字段。
 * 如果你的后端商品草稿字段不一样，后面按实际返回文本扩展。
 */
function parseShoppingDraft(text, actions) {
  return {
    type: 'SHOPPING',
    title: '商品下单草稿',
    goodsName: extractField(text, '商品') || extractField(text, '商品名称'),
    quantity: extractField(text, '数量') || extractField(text, '购买数量'),
    price: extractField(text, '单价') || extractField(text, '商品单价'),
    totalPrice: extractField(text, '总价') || extractField(text, '合计'),
    confirmToken: extractField(text, '确认码') || actions.token
  }
}

/**
 * 从文本中提取字段值。
 *
 * 支持：
 * 场馆：沈阳航空航天大学体育馆
 * 场馆: 沈阳航空航天大学体育馆
 *
 * @param {string} text 原始文本
 * @param {string} fieldName 字段名
 */
function extractField(text, fieldName) {
  if (!text || !fieldName) {
    return ''
  }

  const reg = new RegExp(`${fieldName}\\s*[:：]\\s*([^\\n\\r]+)`)
  const match = text.match(reg)

  if (!match || !match[1]) {
    return ''
  }

  return cleanFieldValue(match[1])
}

/**
 * 清理字段值。
 */
function cleanFieldValue(value) {
  if (!value) {
    return ''
  }

  return value
    .replace(/\r/g, '')
    .replace(/\n/g, '')
    .trim()
}

/**
 * 滚动到底部。
 */
function scrollToBottom() {
  nextTick(() => {
    if (chatBody.value) {
      chatBody.value.scrollTop = chatBody.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 99999;
}

.float-btn {
  width: 60px;
  height: 60px;
  background: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 8px #0002;
}

.float-btn:hover {
  background: #337ecc;
}

.icon {
  font-size: 16px;
}

.chat-panel {
  position: absolute;
  right: 0;
  bottom: 70px;
  width: 390px;
  height: 560px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px #0002;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 14px;
  background: #409eff;
  color: #fff;
  font-weight: bold;
  font-size: 15px;
}

.chat-body {
  flex: 1;
  padding: 14px;
  overflow-y: auto;
  background: #f5f5f5;
}

/**
 * 消息行。
 */
.msg-row {
  display: flex;
  margin-bottom: 12px;
}

/**
 * AI 消息靠左。
 */
.msg-row.ai {
  justify-content: flex-start;
}

/**
 * 用户消息靠右。
 */
.msg-row.user {
  justify-content: flex-end;
}

/**
 * 消息气泡。
 */
.msg-bubble {
  max-width: 82%;
  padding: 8px 12px;
  border-radius: 12px;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}

/**
 * AI 气泡。
 */
.msg-row.ai .msg-bubble {
  background: #fff;
  color: #303133;
  border: 1px solid #e4e7ed;
}

/**
 * 用户气泡。
 */
.msg-row.user .msg-bubble {
  background: #409eff;
  color: #fff;
}

/**
 * 普通文本内容。
 */
.msg-content {
  white-space: pre-wrap;
}

/**
 * 草稿卡片。
 */
.draft-card {
  min-width: 270px;
  background: #ffffff;
  border-radius: 10px;
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

/**
 * 草稿标题。
 */
.draft-title {
  padding: 10px 12px;
  background: #ecf5ff;
  color: #409eff;
  font-weight: 600;
  border-bottom: 1px solid #d9ecff;
}

/**
 * 草稿行。
 */
.draft-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 12px;
  border-bottom: 1px solid #f0f2f5;
  font-size: 13px;
}

/**
 * 草稿字段名。
 */
.draft-row .label {
  color: #909399;
  flex-shrink: 0;
}

/**
 * 草稿字段值。
 */
.draft-row .value {
  color: #303133;
  text-align: right;
  word-break: break-word;
}

/**
 * 总价行。
 */
.draft-row.total .value {
  color: #f56c6c;
  font-weight: 600;
}

/**
 * 确认码区域。
 */
.draft-token {
  padding: 10px 12px;
  background: #fdf6ec;
  color: #e6a23c;
  font-size: 13px;
  font-weight: 600;
}

/**
 * 按钮区域。
 */
.action-box {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}

/**
 * 已处理提示。
 */
.action-used {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}

.chat-footer {
  display: flex;
  padding: 10px;
  gap: 8px;
  background: #fff;
  border-top: 1px solid #ebeef5;
}
</style>