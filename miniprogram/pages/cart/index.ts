// pages/cart/index.ts —— 购物车
import { request } from '../../utils/request'

interface CartItem {
  cakeId: number
  bakeryId: number
  cakeName: string
  price: number
  image: string
  amount: number
}

Page({
  data: {
    items: [] as CartItem[],
    loading: false,
    error: '',
    totalPrice: 0,
  },
  onShow() {
    this.fetchCart()
  },
  fetchCart() {
    this.setData({ loading: true, error: '' })
    request<CartItem[]>({ url: '/api/cart/list' })
      .then((list) => {
        const items = list || []
        const totalPrice = items.reduce((sum, item) => sum + item.price * item.amount, 0)
        this.setData({ items, totalPrice, loading: false })
      })
      .catch((e: Error) => {
        this.setData({ loading: false, error: e.message })
      })
  },
  removeItem(e: any) {
    const cakeId = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定删除该商品吗？',
      success: (res) => {
        if (res.confirm) {
          request({ url: '/api/cart/remove/' + cakeId, method: 'DELETE' })
            .then(() => {
              wx.showToast({ title: '删除成功', icon: 'success' })
              this.fetchCart()
            })
            .catch((err: Error) => {
              wx.showToast({ title: err.message || '删除失败', icon: 'none' })
            })
        }
      },
    })
  },
  goCheckout() {
    if (this.data.items.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }
    wx.navigateTo({ url: '/pages/checkout/index' })
  },
  goShopping() {
    wx.switchTab({ url: '/pages/goods/index' })
  },
})
