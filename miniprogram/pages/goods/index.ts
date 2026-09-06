// pages/goods/index.ts —— 全部商品（连锁分店共用商品）
import { request } from '../../utils/request'

interface Cake {
  id: number
  bakeryId: number
  categoryId: number
  name: string
  image: string
  price: number
  description: string
  customizable: number
  status: number
  sold: number
}

interface CartItem {
  cakeId: number
  amount: number
}

Page({
  data: {
    cakes: [] as Cake[],
    loading: false,
    error: '',
    cartCount: 0,
  },
  onLoad() {
    this.fetchCakes()
    this.fetchCartCount()
  },
  onShow() {
    this.fetchCartCount()
  },
  onPullDownRefresh() {
    Promise.all([this.fetchCakes(), this.fetchCartCount()]).finally(() => wx.stopPullDownRefresh())
  },
  fetchCakes(): Promise<void> {
    this.setData({ loading: true, error: '' })
    return request<Cake[]>({ url: '/api/cake/list/1' })
      .then((list) => {
        this.setData({ cakes: list || [], loading: false })
      })
      .catch((e: Error) => {
        this.setData({ loading: false, error: e.message })
      })
  },
  fetchCartCount(): Promise<void> {
    return request<CartItem[]>({ url: '/api/cart/list' })
      .then((list) => {
        const cartCount = (list || []).reduce((sum, item) => sum + item.amount, 0)
        this.setData({ cartCount })
      })
      .catch(() => {})
  },
  goDetail(e: any) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/cake/detail?id=' + id })
  },
  goCart() {
    wx.navigateTo({ url: '/pages/cart/index' })
  },
})
