// pages/cake/detail.ts —— 蛋糕详情
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

Page({
  data: {
    cake: null as Cake | null,
    loading: false,
    error: '',
    amount: 1,
  },
  onLoad(query: Record<string, string>) {
    if (query.id) {
      this.fetchDetail(Number(query.id))
    }
  },
  fetchDetail(cakeId: number) {
    this.setData({ loading: true, error: '' })
    request<Cake>({ url: '/api/cake/detail/' + cakeId })
      .then((cake) => {
        this.setData({ cake, loading: false })
      })
      .catch((e: Error) => {
        this.setData({ loading: false, error: e.message })
      })
  },
  changeAmount(e: any) {
    const delta = Number(e.currentTarget.dataset.delta)
    let amount = this.data.amount + delta
    if (amount < 1) amount = 1
    if (amount > 99) amount = 99
    this.setData({ amount })
  },
  addToCart() {
    const cake = this.data.cake
    if (!cake) return
    request({
      url: '/api/cart/add',
      method: 'POST',
      data: { cakeId: cake.id, amount: this.data.amount },
    })
      .then(() => {
        wx.showToast({ title: '已加入购物车', icon: 'success' })
      })
      .catch((e: Error) => {
        wx.showToast({ title: e.message || '加入失败', icon: 'none' })
      })
  },
  buyNow() {
    const cake = this.data.cake
    if (!cake) return
    // 先加入购物车，再跳转结算页
    request({
      url: '/api/cart/add',
      method: 'POST',
      data: { cakeId: cake.id, amount: this.data.amount },
    })
      .then(() => {
        wx.navigateTo({ url: '/pages/checkout/index' })
      })
      .catch((e: Error) => {
        wx.showToast({ title: e.message || '操作失败', icon: 'none' })
      })
  },
})
