<template>
  <div class="goods-detail-page">
    <div class="container">
      <!-- 面包屑 -->
      <div class="breadcrumb">
        <span @click="go('/shopping')" class="link">商城</span>
        <span class="separator">/</span>
        <span>{{ goods.goodsName }}</span>
      </div>

      <!-- 商品详情 -->
      <div v-if="goods.id" class="goods-detail">
        <div class="detail-left">
          <div class="goods-image">
            <img :src="goods.image" :alt="goods.goodsName" />
          </div>
        </div>
        <div class="detail-right">
          <h1 class="goods-name">{{ goods.goodsName }}</h1>
          <div class="goods-category">
            <el-tag>{{ getCategoryText(goods.category) }}</el-tag>
            <el-tag v-if="goods.status === 1" type="success">上架</el-tag>
            <el-tag v-else type="danger">下架</el-tag>
          </div>
          <div class="goods-price">
            <span class="price-label">价格</span>
            <span class="price">¥{{ goods.price }}</span>
          </div>
          <div class="goods-info-row">
            <span class="label">库存</span>
            <span class="value">{{ goods.stock > 0 ? goods.stock + ' 件' : '已售罄' }}</span>
          </div>
          <div class="goods-info-row" v-if="goods.remark">
            <span class="label">备注</span>
            <span class="value">{{ goods.remark }}</span>
          </div>

          <!-- 数量选择 -->
          <div class="quantity-section" v-if="goods.status === 1 && goods.stock > 0">
            <span class="label">数量</span>
            <div class="quantity-selector">
              <el-button
                :disabled="quantity <= 1"
                @click="changeQuantity(-1)"
                circle
              >-</el-button>
              <span class="quantity-num">{{ quantity }}</span>
              <el-button
                :disabled="quantity >= goods.stock"
                @click="changeQuantity(1)"
                circle
              >+</el-button>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              :disabled="goods.status !== 1 || goods.stock <= 0"
              @click="addToCart"
            >
              加入购物车
            </el-button>
            <el-button
              size="large"
              @click="go('/shopping/cart')"
            >
              去购物车
            </el-button>
          </div>
        </div>
      </div>

      <!-- 商品描述 -->
      <div v-if="goods.description" class="goods-description">
        <h3 class="section-title">商品描述</h3>
        <div class="description-content">
          {{ goods.description }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCart } from '@/stores/cart'
import { GetGoodsDetail } from '@/api/shopping'

const route = useRoute()
const router = useRouter()
const cartStore = useCart()

const loading = ref(false)
const goods = ref({})
const quantity = ref(1)

const getCategoryText = (category) => {
  const map = { 1: '饮品', 2: '零食', 3: '日用品' }
  return map[category] || category
}

const go = (path) => {
  router.push(path)
}

const changeQuantity = (delta) => {
  const newQuantity = quantity.value + delta
  if (newQuantity < 1 || newQuantity > goods.value.stock) return
  quantity.value = newQuantity
}

const addToCart = async () => {
  if (goods.value.status !== 1) {
    ElMessage.warning('该商品已下架')
    return
  }
  if (goods.value.stock <= 0) {
    ElMessage.warning('该商品库存不足')
    return
  }
  const success = await cartStore.addToCart(goods.value.id, quantity.value)
  if (success) {
    ElMessage.success(`已加入购物车 ${quantity.value} 件`)
  } else {
    ElMessage.error('加入购物车失败')
  }
}

const loadGoodsDetail = async () => {
  const goodsId = route.params.id
  if (!goodsId) {
    router.push('/shopping')
    return
  }

  loading.value = true
  try {

    //获取商品详情
    const res = await GetGoodsDetail(goodsId)
    if (res.code === 200) {
      goods.value = res.data
    } else {
      ElMessage.error(res.message || '获取商品详情失败')
    }
    
    if (!goods.value.id) {
      ElMessage.error('商品不存在')
    }
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadGoodsDetail()
})
</script>

<style scoped lang="scss">
.goods-detail-page {
  min-height: calc(100vh - 140px);
  background: #f5f5f5;
  padding: 30px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.breadcrumb {
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;

  .link {
    cursor: pointer;

    &:hover {
      color: #333;
    }
  }

  .separator {
    margin: 0 8px;
  }
}

.goods-detail {
  display: flex;
  gap: 40px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;

  .detail-left {
    width: 400px;
    flex-shrink: 0;

    .goods-image {
      width: 100%;
      height: 400px;
      border-radius: 8px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }

  .detail-right {
    flex: 1;

    .goods-name {
      font-size: 28px;
      margin: 0 0 15px 0;
      color: #333;
    }

    .goods-category {
      display: flex;
      gap: 10px;
      margin-bottom: 20px;
    }

    .goods-price {
      background: #fff9f0;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 20px;

      .price-label {
        font-size: 14px;
        color: #666;
      }

      .price {
        font-size: 32px;
        font-weight: bold;
        color: #f7ba2a;
        margin-left: 10px;
      }
    }

    .goods-info-row {
      display: flex;
      margin-bottom: 15px;

      .label {
        width: 80px;
        color: #666;
        font-size: 14px;
      }

      .value {
        color: #333;
        font-size: 14px;
      }
    }

    .quantity-section {
      display: flex;
      align-items: center;
      margin-bottom: 25px;

      .label {
        width: 80px;
        color: #666;
        font-size: 14px;
      }

      .quantity-selector {
        display: flex;
        align-items: center;
        gap: 10px;

        .quantity-num {
          min-width: 40px;
          text-align: center;
          font-size: 16px;
        }
      }
    }

    .action-buttons {
      display: flex;
      gap: 15px;
    }
  }
}

.goods-description {
  background: #fff;
  padding: 30px;
  border-radius: 8px;

  .section-title {
    font-size: 18px;
    margin: 0 0 20px 0;
    color: #333;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }

  .description-content {
    color: #666;
    line-height: 1.8;
    font-size: 14px;
  }
}
</style>
