// pages/checkout/index.ts —— 结算确认
import { request } from '../../utils/request'

interface CartItem {
  cakeId: number
  bakeryId: number
  cakeName: string
  price: number
  image: string
  amount: number
}

interface CheckoutVO {
  items: CartItem[]
  totalPrice: number
}

Page({
  data: {
    items: [] as CartItem[],
    totalPrice: 0,
    address: '',
    phone: '',
    remark: '',
    submitting: false,
    balance: 100000, // 模拟余额 1000 元（单位：分）
  },
  onLoad() {
    this.fetchCheckout()
  },
  fetchCheckout() {
    request<CheckoutVO>({ url: '/api/cart/checkout', method: 'POST' })
      .then((data) => {
        if (data) {
          this.setData({
            items: data.items || [],
            totalPrice: data.totalPrice || 0,
          })
        }
      })
      .catch((e: Error) => {
        wx.showToast({ title: e.message || '获取结算信息失败', icon: 'none' })
      })
  },
  onAddressInput(e: any) {
    this.setData({ address: e.detail.value })
  },
  onPhoneInput(e: any) {
    this.setData({ phone: e.detail.value })
  },
  onRemarkInput(e: any) {
    this.setData({ remark: e.detail.value })
  },
  submitOrder() {
    if (!this.data.address.trim()) {
      wx.showToast({ title: '请填写配送地址', icon: 'none' })
      return
    }
    if (!this.data.phone.trim()) {
      wx.showToast({ title: '请填写联系电话', icon: 'none' })
      return
    }
    if (this.data.totalPrice > this.data.balance) {
      wx.showToast({ title: '余额不足', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    request<any>({
      url: '/api/order/checkout',
      method: 'POST',
      data: {
        address: this.data.address,
        phone: this.data.phone,
        remark: this.data.remark,
      },
    })
      .then((orderVO) => {
        this.setData({ submitting: false })
        wx.showModal({
          title: '下单成功',
          content: '订单已创建，是否立即支付？',
          confirmText: '去支付',
          cancelText: '稍后支付',
          success: (res) => {
            if (res.confirm && orderVO && orderVO.orderNo) {
              this.payOrder(orderVO.orderNo, orderVO.totalPrice)
            } else {
              wx.switchTab({ url: '/pages/order/index' })
            }
          },
        })
      })
      .catch((e: Error) => {
        this.setData({ submitting: false })
        wx.showToast({ title: e.message || '下单失败', icon: 'none' })
      })
  },
  payOrder(orderNo: string, totalAmount: number) {
    request({
      url: '/api/order/pay',
      method: 'POST',
      data: { orderNo, totalAmount },
    })
      .then(() => {
        wx.showToast({ title: '支付成功', icon: 'success' })
        setTimeout(() => {
          wx.switchTab({ url: '/pages/order/index' })
        }, 1500)
      })
      .catch((e: Error) => {
        wx.showToast({ title: e.message || '支付失败', icon: 'none' })
      })
  },
})
