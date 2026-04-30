<template>
  <div class="page">
    <el-card shadow="never">
      <template #header>
        <div class="header-content">
          <span>我的消息</span>
          <el-button type="primary" @click="handleMarkAllRead">全部标记已读</el-button>
        </div>
      </template>

      <el-tabs v-model="readStatus" @tab-change="loadNotifications">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="未读" name="0" />
        <el-tab-pane label="已读" name="1" />
      </el-tabs>

      <el-table :data="notificationList" style="width: 100%">
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.readStatus === 0" type="danger">未读</el-tag>
            <el-tag v-else type="info">已读</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="typeName" label="类型" width="120" />

        <el-table-column prop="title" label="标题" width="180" />

        <el-table-column prop="content" label="内容" />

        <el-table-column prop="createTime" label="时间" width="180" />

        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button
              v-if="row.readStatus === 0"
              type="primary"
              link
              @click="handleMarkRead(row.id)"
            >
              标记已读
            </el-button>

            <el-button
              type="danger"
              link
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="prev, pager, next, total"
          @current-change="loadNotifications"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
import {
  getMyNotifications,
  markNotificationRead,
  markAllNotificationsRead,
  deleteNotification
} from '@/api/notification'
import { ElMessage, ElMessageBox } from 'element-plus'

const notificationList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const readStatus = ref('all')

const getReadStatusParam = () => {
  if (readStatus.value === 'all') {
    return null
  }
  return Number(readStatus.value)
}

const loadNotifications = async () => {
  const res = await getMyNotifications(
    pageNum.value,
    pageSize.value,
    getReadStatusParam()
  )

  notificationList.value = res.data.list || []
  total.value = res.data.total || 0
}

const handleMarkRead = async id => {
  await markNotificationRead(id)
  ElMessage.success('已标记为已读')
  await loadNotifications()
}

const handleMarkAllRead = async () => {
  await markAllNotificationsRead()
  ElMessage.success('已全部标记为已读')
  await loadNotifications()
}

const handleDelete = async id => {
  await ElMessageBox.confirm('确定要删除该消息吗？', '提示', {
    type: 'warning'
  })

  await deleteNotification(id)
  ElMessage.success('删除成功')
  await loadNotifications()
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped lang="scss">
.page {
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 18px;
}

:deep(.el-card) {
  border: 1px solid #e5e5e5;
  border-radius: 0;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #e5e5e5;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-button--primary) {
  background: #1a1a1a;
  border-color: #1a1a1a;
  border-radius: 0;
}

:deep(.el-button--default) {
  border-color: #1a1a1a;
  border-radius: 0;
}

:deep(.el-input__wrapper) {
  border-radius: 0;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 0;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>