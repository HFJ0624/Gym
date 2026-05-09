<template>
  <div class="refund-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预约退款审核</span>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 160px"
          >
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="订单号 / 用户名 / 场地名"
            clearable
            style="width: 240px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="申请ID" width="90" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="venueName" label="场馆" min-width="160" />
        <el-table-column prop="courtName" label="场地" min-width="160" />
        <el-table-column prop="courtType" label="类型" width="100" />
        <el-table-column prop="refundAmount" label="退款金额" width="120" />
        <el-table-column prop="reason" label="原因" min-width="180" />

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">已拒绝</el-tag>
            <el-tag v-else>未知</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="申请时间" width="180" />

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              @click="openApprove(row)"
            >
              通过
            </el-button>

            <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              @click="openReject(row)"
            >
              拒绝
            </el-button>

            <span v-if="row.status !== 0">已处理</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        style="margin-top: 16px; text-align: right"
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        :page-size="queryParams.limit"
        :current-page="queryParams.current"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
      />
    </el-card>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditDialog.visible" :title="auditDialog.title" width="500px">
      <el-form>
        <el-form-item label="审核备注">
          <el-input
            v-model="auditDialog.auditRemark"
            type="textarea"
            :rows="4"
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="auditDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRefundPage,
  approveRefund,
  rejectRefund
} from '@/api/bookingRefund'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  current: 1,
  limit: 10,
  status: null,
  keyword: ''
})

const auditDialog = reactive({
  visible: false,
  title: '',
  type: '',
  row: null,
  auditRemark: ''
})

async function loadData() {
  loading.value = true

  try {
    const res = await getRefundPage(queryParams)

    /**
     * 这里根据你项目 Result 返回结构调整。
     * 如果你的数据是 res.data.list，就按下面写。
     */
    const page = res.data || {}

    tableData.value = page.list || []
    total.value = page.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.current = 1
  queryParams.limit = 10
  queryParams.status = null
  queryParams.keyword = ''
  loadData()
}

function handleCurrentChange(page) {
  queryParams.current = page
  loadData()
}

function handleSizeChange(size) {
  queryParams.limit = size
  queryParams.current = 1
  loadData()
}

function openApprove(row) {
  auditDialog.visible = true
  auditDialog.title = '审核通过退款'
  auditDialog.type = 'approve'
  auditDialog.row = row
  auditDialog.auditRemark = '审核通过，退款金额将退回用户余额。'
}

function openReject(row) {
  auditDialog.visible = true
  auditDialog.title = '审核拒绝退款'
  auditDialog.type = 'reject'
  auditDialog.row = row
  auditDialog.auditRemark = ''
}

async function submitAudit() {
  if (!auditDialog.row) {
    return
  }

  await ElMessageBox.confirm('确认执行该审核操作吗？', '提示', {
    type: 'warning'
  })

  const data = {
    id: auditDialog.row.id,
    auditRemark: auditDialog.auditRemark
  }

  if (auditDialog.type === 'approve') {
    await approveRefund(data)
    ElMessage.success('退款审核已通过')
  } else {
    await rejectRefund(data)
    ElMessage.success('退款审核已拒绝')
  }

  auditDialog.visible = false
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.refund-page {
  padding: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>