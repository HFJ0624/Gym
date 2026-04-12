<template>
  <div class="cart-page">
    <div class="container">
      <h2 class="page-title">购物车</h2>

      <!-- 购物车为空 -->
      <el-empty v-if="cartList.length === 0" description="购物车是空的">
        <el-button type="primary" @click="goShopping">去逛逛</el-button>
      </el-empty>

      <!-- 购物车列表 -->
      <div v-else class="cart-content">
        <!-- 全选栏 -->
        <div class="cart-header">
          <el-checkbox v-model="selectAll" @change="handleSelectAll">
            全选
          </el-checkbox>
          <div class="header-labels">
            <span>商品信息</span>
            <span>单价</span>
            <span>数量</span>
            <span>小计</span>
            <span>操作</span>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="cart-items">
          <div v-for="item in cartList" :key="item.id" class="cart-item">
            <el-checkbox v-model="item.selected" @change="updateSelectAll" />
            <div class="item-info">
              <img :src="item.image" :alt="item.goodsName" />
              <span class="item-name">{{ item.goodsName }}</span>
            </div>
            <span class="item-price">¥{{ item.price }}</span>
            <div class="item-quantity">
              <el-button
                :disabled="item.quantity <= 1"
                @click="changeQuantity(item, -1)"
                size="small"
                circle
              >-</el-button>
              <span class="quantity-num">{{ item.quantity }}</span>
              <el-button
                @click="changeQuantity(item, 1)"
                size="small"
                circle
              >+</el-button>
            </div>
            <span class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            <el-button
              type="danger"
              size="small"
              link
              @click="removeItem(item)"
            >
              删除
            </el-button>
          </div>
        </div>

        <!-- 底部结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox v-model="selectAll" @change="handleSelectAll">
              全选
            </el-checkbox>
            <el-button type="text" @click="clearSelected">清空选中</el-button>
          </div>
          <div class="footer-right">
            <span class="total-count">已选 <em>{{ selectedQuantity }}</em> 件</span>
            <span class="total-price">
              合计: <em>¥{{ selectedTotalPrice.toFixed(2) }}</em>
            </span>
            <el-button
              type="primary"
              size="large"
              :disabled="selectedItems.length === 0"
              @click="checkout"
            >
              结算
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCart } from '@/stores/cart'

const router = useRouter()
const cartStore = useCart()

const cartList = computed(() => cartStore.cartList)
const selectedItems = computed(() => cartStore.selectedItems)
const selectedTotalPrice = computed(() => cartStore.selectedTotalPrice)

const selectedQuantity = computed(() => {
  return selectedItems.value.reduce((total, item) => total + item.quantity, 0)
})

const selectAll = computed({
  get: () => cartList.value.length > 0 && cartList.value.every(item => item.selected),
  set: (value) => cartStore.toggleSelectAll(value)
})

const handleSelectAll = (value) => {
  cartStore.toggleSelectAll(value)
}

const updateSelectAll = () => {}

const changeQuantity = (item, delta) => {
  const newQuantity = item.quantity + delta
  if (newQuantity < 1) return
  cartStore.updateQuantity(item.id, newQuantity)
}

const removeItem = (item) => {
  ElMessageBox.confirm('确定要删除这件商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await cartStore.removeFromCart(item.id)
    ElMessage.success('已删除')
  }).catch(() => {})
}

const clearSelected = () => {
  ElMessageBox.confirm('确定要清空选中的商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    for (const item of selectedItems.value) {
      await cartStore.removeFromCart(item.id)
    }
    ElMessage.success('已清空')
  }).catch(() => {})
}

const checkout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push('/shopping/order')
}

const goShopping = () => {
  router.push('/shopping')
}

onMounted(() => {
  cartStore.loadCart()
})
</script>

<style scoped lang="scss">
.cart-page {
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

.cart-content {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.cart-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background: #fafafa;
  border-bottom: 1px solid #eee;

  .header-labels {
    flex: 1;
    display: flex;
    margin-left: 30px;

    span {
      flex: 1;
      text-align: center;
      color: #666;
      font-size: 14px;

      &:first-child {
        text-align: left;
        flex: 2;
      }
    }
  }
}

.cart-items {
  .cart-item {
    display: flex;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #eee;

    &:last-child {
      border-bottom: none;
    }

    .item-info {
      flex: 2;
      display: flex;
      align-items: center;
      gap: 15px;

      img {
        width: 80px;
        height: 80px;
        object-fit: cover;
        border-radius: 4px;
      }

      .item-name {
        font-size: 14px;
        color: #333;
      }
    }

    .item-price,
    .item-subtotal {
      flex: 1;
      text-align: center;
      font-size: 14px;
      color: #333;
    }

    .item-subtotal {
      color: #f7ba2a;
      font-weight: bold;
    }

    .item-quantity {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;

      .quantity-num {
        min-width: 40px;
        text-align: center;
      }
    }

    :deep(.el-button--text) {
      color: #f56c6c;
    }
  }
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #fafafa;
  border-top: 1px solid #eee;

  .footer-left {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .footer-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .total-count {
      em {
        color: #f7ba2a;
        font-style: normal;
        font-size: 18px;
      }
    }

    .total-price {
      em {
        color: #f7ba2a;
        font-style: normal;
        font-size: 24px;
        font-weight: bold;
      }
    }
  }
}
</style>
