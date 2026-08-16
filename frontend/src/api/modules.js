import api from './index'
import axios from 'axios'

// AI 接口单独配置（后端路径是 /ai 不是 /api/ai）
const aiClient = axios.create({
  baseURL: '/',
  timeout: 30000
})

// 用户相关
export const userApi = {
  login: (data) => api.post('/user/login', data),
  register: (data) => api.post('/user/register', data),
  getInfo: () => api.get('/user/info')
}

// 烘焙店相关
export const bakeryApi = {
  list: (params) => api.get('/bakery/list', { params }),
  detail: (id) => api.get(`/bakery/detail/${id}`),
  categories: () => api.get('/bakery/categories')
}

// 蛋糕相关
export const cakeApi = {
  list: (bakeryId) => api.get(`/cake/list/${bakeryId || 1}`),
  detail: (id) => api.get(`/cake/detail/${id}`),
  byCategory: (categoryId) => api.get(`/cake/category/${categoryId}`)
}

// 购物车相关
export const cartApi = {
  list: () => api.get('/cart/list'),
  add: (data) => api.post('/cart/add', data),
  remove: (cakeId) => api.delete(`/cart/remove/${cakeId}`),
  checkout: () => api.post('/cart/checkout')
}

// 订单相关
export const orderApi = {
  create: (data) => api.post('/order/checkout', data),
  list: () => api.get('/order/list'),
  detail: (id) => api.get(`/order/${id}`),
  pay: (id) => api.put(`/order/pay/${id}`),
  cancel: (id) => api.put(`/order/cancel/${id}`)
}

// 评价相关
export const reviewApi = {
  create: (data) => api.post('/review/create', data),
  reply: (id, content) => api.put(`/review/reply/${id}`, null, { params: { replyContent: content } }),
  byBakery: (bakeryId, params) => api.get(`/review/bakery/${bakeryId}`, { params }),
  byCake: (cakeId, params) => api.get(`/review/cake/${cakeId}`, { params }),
  byOrder: (orderId) => api.get(`/review/order/${orderId}`)
}

// 秒杀相关
export const flashSaleApi = {
  detail: (id) => api.get(`/flashSale/detail/${id}`),
  seckill: (id) => api.post(`/flashSale/${id}/seckill`)
}

// 商家端
export const adminApi = {
  list: () => api.get('/order/admin/list'),
  accept: (orderId) => api.put(`/order/admin/${orderId}/accept`),
  deliver: (orderId) => api.put(`/order/admin/${orderId}/deliver`),
  complete: (orderId) => api.put(`/order/admin/${orderId}/complete`)
}

// AI 相关 - 使用单独的 client
export const aiApi = {
  recommend: async (userMsg) => {
    const response = await aiClient.get('/ai/recommend', { params: { userMsg } })
    const res = response.data
    if (res.code === 200) return res.data
    throw new Error(res.message)
  }
}
