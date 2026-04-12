<template>
  <div class="shopping-order-page">
    <div class="container">
      <h2 class="page-title">我的订单</h2>

      <!-- 订单状态筛选 -->
      <div class="order-tabs">
        <el-button
          v-for="tab in tabs"
          :key="tab.value"
          :type="activeTab === tab.value ? 'primary' : 'default'"
          @click="selectTab(tab.value)"
        >
          {{ tab.label }}
        </el-button>
      </div>

      <!-- 订单列表 -->
      <div class="order-list" v-loading="loading">
        <el-empty v-if="orderList.length === 0 && !loading" description="暂无订单" />

        <div v-else>
          <div v-for="order in orderList" :key="order.id" class="order-card">
            <!-- 订单头部 -->
            <div class="order-header">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <span class="order-time">{{ order.createTime }}</span>
              <el-tag :type="getStatusType(order.status)">
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>

            <!-- 订单商品 -->
            <div class="order-items">
              <div v-for="item in order.items" :key="item.id" class="order-item">
                <img :src="item.image" :alt="item.goodsName" />
                <div class="item-info">
                  <span class="item-name">{{ item.goodsName }}</span>
                  <span class="item-price">¥{{ item.price }} × {{ item.quantity }}</span>
                </div>
              </div>
            </div>

            <!-- 订单底部 -->
            <div class="order-footer">
              <span class="total">
                共 {{ order.totalQuantity }} 件商品，合计: <em>¥{{ order.totalAmount }}</em>
              </span>
              <div class="order-actions">
                <el-button
                  v-if="order.status === 1"
                  type="danger"
                  size="small"
                  @click="cancelOrder(order)"
                >
                  取消订单
                </el-button>
                <el-button
                  v-if="order.status === 1"
                  type="primary"
                  size="small"
                  @click="payOrder(order)"
                >
                  立即支付
                </el-button>
                <el-button
                  type="default"
                  size="small"
                  @click="viewOrder(order)"
                >
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadOrders"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { GetShoppingOrderList, CancelShoppingOrder, PayShoppingOrder } from '@/api/shopping'

const router = useRouter()

const orderList = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeTab = ref('')

const tabs = [
  { label: '全部', value: '' },
  { label: '待支付', value: 1 },
  { label: '已支付', value: 2 },
  { label: '已完成', value: 3 },
  { label: '已取消', value: 0 }
]

const getStatusText = (status) => {
  const map = { 0: '已取消', 1: '待支付', 2: '已支付', 3: '已完成' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'success' }
  return map[status] || 'info'
}

const selectTab = (value) => {
  activeTab.value = value
  page.value = 1
  loadOrders()
}

const loadOrders = async () => {
  loading.value = true
  try {
    // TODO: 后端接口调用
    // const res = await GetShoppingOrderList(page.value, pageSize.value, activeTab.value)
    // if (res.code === 200) {
    //   orderList.value = res.data.list
    //   total.value = res.data.total
    // }

    // 死数据演示
    orderList.value = [
      {
        id: 1,
        orderNo: 'SO202404100001',
        createTime: '2024-04-10 14:30:00',
        status: 1,
        totalAmount: '104.80',
        totalQuantity: 3,
        items: [
          {
            id: 1,
            goodsId: 1,
            goodsName: '运动水壶',
            image: 'https://via.placeholder.com/80',
            price: 39.9,
            quantity: 2
          },
          {
            id: 2,
            goodsId: 2,
            goodsName: '运动毛巾',
            image: 'https://via.placeholder.com/80',
            price: 25.0,
            quantity: 1
          }
        ]
      },
      {
        id: 2,
        orderNo: 'SO202404090002',
        createTime: '2024-04-09 10:15:00',
        status: 2,
        totalAmount: '15.00',
        totalQuantity: 1,
        items: [
          {
            id: 3,
            goodsId: 3,
            goodsName: '能量棒',
            image: 'https://via.placeholder.com/80',
            price: 15.0,
            quantity: 1
          }
        ]
      }
    ]
    total.value = orderList.value.length
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const cancelOrder = async (order) => {
  ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 后端接口调用
      // await CancelShoppingOrder(order.id)
      // await loadOrders()

      // 死数据演示
      order.status = 0
      ElMessage.success('订单已取消')
    } catch (error) {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }).catch(() => {})
}

const payOrder = async (order) => {
  ElMessageBox.confirm(`确认支付 ¥${order.totalAmount} 吗？`, '支付确认', {
    confirmButtonText: '确认支付',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 后端接口调用
      // await PayShoppingOrder(order.id)
      // await loadOrders()

      // 死数据演示
      order.status = 2
      ElMessage.success('支付成功')
    } catch (error) {
      console.error('支付失败:', error)
      ElMessage.error('支付失败')
    }
  }).catch(() => {})
}

const viewOrder = (order) => {
  ElMessage.info('订单详情功能开发中')
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped lang="scss">
.shopping-order-page {
  min-height: calc(100vh - 140px);
  background: #f5f5f5;
  padding: 30px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.order-tabs {
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.order-list {
  .order-card {
    background: #fff;
    border-radius: 8px;
    margin-bottom: 20px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

    .order-header {
      display: flex;
      align-items: center;
      gap: 15px;
      padding: 15px 20px;
      background: #fafafa;
      border-bottom: 1px solid #eee;

      .order-no,
      .order-time {
        font-size: 14px;
        color: #666;
      }
    }

    .order-items {
      padding: 20px;

      .order-item {
        display: flex;
        align-items: center;
        gap: 15px;
        padding: 10px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        img {
          width: 80px;
          height: 80px;
          object-fit: cover;
          border-radius: 4px;
        }

        .item-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 5px;

          .item-name {
            font-size: 14px;
            color: #333;
          }

          .item-price {
            font-size: 14px;
            color: #999;
          }
        }
      }
    }

    .order-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px 20px;
      border-top: 1px solid #eee;

      .total {
        font-size: 14px;
        color: #666;

        em {
          font-style: normal;
          color: #f7ba2a;
          font-size: 20px;
          font-weight: bold;
        }
      }

      .order-actions {
        display: flex;
        gap: 10px;
      }
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
