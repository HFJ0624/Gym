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
              <span class="order-time">{{ order.payTime }}</span>
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

            <!-- 订单备注 -->
            <div v-if="order.remark" class="order-remark">
              <span class="remark-label">备注：</span>
              <span class="remark-text">{{ order.remark }}</span>
            </div>

            <!-- 订单底部 -->
            <div class="order-footer">
              <span class="total">
                共 {{ order.totalQuantity }} 件商品，合计: <em>¥{{ order.totalAmount }}</em>
              </span>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useOrder } from '@/stores/order'

const router = useRouter()
const orderStore = useOrder()

const allOrderList = computed(() => orderStore.orderList)
const orderList = computed(() => {
  if (!activeTab.value) return allOrderList.value
  return allOrderList.value.filter(order => order.status === activeTab.value)
})
const loading = computed(() => orderStore.loading)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeTab = ref('')

const tabs = [
  { label: '全部', value: '' },
  { label: '待支付', value: 0 },
  { label: '已支付', value: 1 },
  { label: '已取消', value: -1 }
]

const getStatusText = (status) => {
  const map = { '-1': '已取消', 0: '待支付', 1: '已支付'}
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { '-1': 'danger', 0: 'info', 1: 'success'}
  return map[status] || 'info'
}

const selectTab = (value) => {
  activeTab.value = value
  page.value = 1
  total.value = orderList.value.length
}

const loadOrders = async () => {
  try {
    await orderStore.loadOrders(page.value, pageSize.value, activeTab.value)
    total.value = orderList.value.length
  } catch (error) {
    console.error('加载订单失败:', error)
  }
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

    .order-remark {
      padding: 0 20px 20px;
      border-top: 1px solid #f0f0f0;
      padding-top: 15px;
      margin-top: -10px;

      .remark-label {
        font-size: 14px;
        color: #666;
      }

      .remark-text {
        font-size: 14px;
        color: #333;
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
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
