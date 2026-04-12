import { defineStore } from 'pinia'
import { ref } from 'vue'
import { CreateShoppingOrder, GetShoppingOrderList, CancelShoppingOrder, PayShoppingOrder } from '@/api/shopping'

export const useOrder = defineStore('order', () => {
  const orderList = ref([])
  const loading = ref(false)

  const loadOrders = async (page = 1, pageSize = 10, status = '') => {
    loading.value = true
    try {
      // TODO: 后端接口调用
      // const res = await GetShoppingOrderList(page, pageSize, status)
      // if (res.code === 200) {
      //   orderList.value = res.data.list
      //   return res.data
      // }

      // 死数据演示
      return {
        list: orderList.value,
        total: orderList.value.length
      }
    } catch (error) {
      console.error('加载订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const createOrder = async (cartItems, remark = '') => {
    loading.value = true
    try {
      //TODO: 后端接口调用
      const res = await CreateShoppingOrder({
        cartIds: cartItems.map(item => item.id),
        remark
      })
      if (res.code === 200) {
        const newOrder = res.data
        orderList.value.unshift(newOrder)
        return newOrder
      }

      return newOrder
    } catch (error) {
      console.error('创建订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const cancelOrder = async (orderId) => {
    try {
      // TODO: 后端接口调用
      // await CancelShoppingOrder(orderId)

      // 死数据演示
      const order = orderList.value.find(o => o.id === orderId)
      if (order) {
        order.status = 0
      }
    } catch (error) {
      console.error('取消订单失败:', error)
      throw error
    }
  }

  const payOrder = async (orderId) => {
    try {
      // TODO: 后端接口调用
      // await PayShoppingOrder(orderId)

      // 死数据演示
      const order = orderList.value.find(o => o.id === orderId)
      if (order) {
        order.status = 2
      }
      return true
    } catch (error) {
      console.error('支付失败:', error)
      throw error
    }
  }

  return {
    orderList,
    loading,
    loadOrders,
    createOrder,
    cancelOrder,
    payOrder
  }
})
