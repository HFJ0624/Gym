import request from '@/utils/frontRequest'

// ==================== 商品相关接口 ====================

// 获取商品列表
// 后端接口：GET /front/goods/list
// 参数：page, limit, category, goodsName
export const GetGoodsList = (page, limit, params = {}) => {
  return request({
    url: '/front/goods/list',
    method: 'get',
    params: { page, limit, ...params }
  })
}

// 获取商品详情
// 后端接口：GET /front/goods/detail/:id
export const GetGoodsDetail = (id) => {
  return request({
    url: `/front/goods/detail/${id}`,
    method: 'get'
  })
}

// ==================== 购物车相关接口 ====================

// 获取购物车列表
// 后端接口：GET /front/cart/list
export const GetCartList = () => {
  return request({
    url: '/front/cart/list',
    method: 'get'
  })
}

// 添加商品到购物车
// 后端接口：POST /front/cart/add
// 参数：goodsId, quantity
export const AddToCart = (data) => {
  return request({
    url: '/front/cart/add',
    method: 'post',
    data
  })
}

// 更新购物车商品数量
// 后端接口：PUT /front/cart/update
// 参数：cartId, quantity
export const UpdateCartItem = (data) => {
  return request({
    url: '/front/cart/update',
    method: 'put',
    data
  })
}

// 删除购物车商品
// 后端接口：DELETE /front/cart/delete/:id
export const DeleteCartItem = (id) => {
  return request({
    url: `/front/cart/delete/${id}`,
    method: 'delete'
  })
}

// 清空购物车
// 后端接口：DELETE /front/cart/clear
export const ClearCart = () => {
  return request({
    url: '/front/cart/clear',
    method: 'delete'
  })
}

// ==================== 购物订单相关接口 ====================

// 创建购物订单
export const CreateShoppingOrder = (data) => {
  return request({
    url: '/front/shoppingOrder/create',
    method: 'post',
    data
  })
}

// 获取购物订单列表
export const GetShoppingOrderList = (page, limit, status = '') => {
  return request({
    url: '/front/shoppingOrder/list',
    method: 'get',
    params: { page, limit, status }
  })
}

// 获取购物订单详情
// 后端接口：GET /front/shoppingOrder/detail/:id
export const GetShoppingOrderDetail = (id) => {
  return request({
    url: `/front/shoppingOrder/detail/${id}`,
    method: 'get'
  })
}

// 取消购物订单
// 后端接口：PUT /front/shoppingOrder/cancel/:id
export const CancelShoppingOrder = (id) => {
  return request({
    url: `/front/shoppingOrder/cancel/${id}`,
    method: 'put'
  })
}

// 支付购物订单
// 后端接口：POST /front/shoppingOrder/pay/:id
export const PayShoppingOrder = (id) => {
  return request({
    url: `/front/shoppingOrder/pay/${id}`,
    method: 'post'
  })
}
