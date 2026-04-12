import { defineStore } from 'pinia'
import { GetCartList, AddToCart, UpdateCartItem, DeleteCartItem, ClearCart } from '@/api/shopping'

export const useCart = defineStore('cart', {
  state: () => ({
    cartList: [],
    cartCount: 0
  }),

  getters: {
    // 计算购物车总价
    totalPrice: (state) => {
      return state.cartList.reduce((total, item) => {
        return total + (item.price * item.quantity)
      }, 0)
    },

    // 计算购物车商品总数
    totalQuantity: (state) => {
      return state.cartList.reduce((total, item) => {
        return total + item.quantity
      }, 0)
    },

    // 获取选中的商品
    selectedItems: (state) => {
      return state.cartList.filter(item => item.selected)
    },

    // 计算选中商品的总价
    selectedTotalPrice: (state) => {
      return state.cartList
        .filter(item => item.selected)
        .reduce((total, item) => {
          return total + (item.price * item.quantity)
        }, 0)
    }
  },

  actions: {
    // 加载购物车列表
    async loadCart() {
      try {
        //TODO: 后端接口调用
        const res = await GetCartList()
        if (res.code === 200) {
          this.cartList = res.data.list.map(item => ({
            ...item,
            selected: true
          }))
          this.cartCount = this.cartList.length
        }

        this.cartCount = this.cartList.length
      } catch (error) {
        console.error('加载购物车失败:', error)
      }
    },

    // 添加商品到购物车
    async addToCart(goodsId, quantity = 1) {
      try {
        //TODO: 后端接口调用
        await AddToCart({ goodsId, quantity })
        await this.loadCart()

        this.cartCount = this.cartList.length
        return true
      } catch (error) {
        console.error('添加购物车失败:', error)
        return false
      }
    },

    // 更新购物车商品数量
    async updateQuantity(cartId, quantity) {
      try {
        await UpdateCartItem({ cartId, quantity })

        const item = this.cartList.find(item => item.id === cartId)
        if (item) {
          item.quantity = quantity
        }
      } catch (error) {
        console.error('更新购物车失败:', error)
      }
    },

    // 删除购物车商品
    async removeFromCart(cartId) {
      try {
        await DeleteCartItem(cartId)

        this.cartList = this.cartList.filter(item => item.id !== cartId)
        this.cartCount = this.cartList.length
      } catch (error) {
        console.error('删除购物车商品失败:', error)
      }
    },

    // 切换商品选中状态
    toggleSelect(cartId) {
      const item = this.cartList.find(item => item.id === cartId)
      if (item) {
        item.selected = !item.selected
      }
    },

    // 全选/取消全选
    toggleSelectAll(selected) {
      this.cartList.forEach(item => {
        item.selected = selected
      })
    },

    // 清空购物车
    async clearCart() {
      try {
        // TODO: 后端接口调用
        // await ClearCart()

        // 死数据演示
        this.cartList = []
        this.cartCount = 0
      } catch (error) {
        console.error('清空购物车失败:', error)
      }
    }
  }
})
