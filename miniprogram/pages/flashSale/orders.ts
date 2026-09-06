// pages/flashSale/orders.ts —— 秒杀订单列表
import { request } from '../../utils/request'

interface FlashSaleOrder {
  id: number
  flashSaleId: number
  orderNo: string
  status: number
  createTime: string
  cakeName: string
  cakeImage: string
  flashPrice: number
}

Page({
  data: {
    orders: [] as FlashSaleOrder[],
    loading: false,
    error: '',
  },

  onShow() {
    this.fetchOrders()
  },

  fetchOrders() {
    this.setData({ loading: true, error: '' })
    request<FlashSaleOrder[]>({ url: '/api/flashSale/order/list' })
      .then((list) => {
        this.setData({ orders: list || [], loading: false })
      })
      .catch((e: Error) => {
        this.setData({ loading: false, error: e.message })
      })
  },

  payOrder(e: any) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确认支付该订单？',
      success: (res) => {
        if (res.confirm) {
          request({
            url: '/api/flashSale/order/pay/' + id,
            method: 'POST',
          })
            .then(() => {
              wx.showToast({ title: '支付成功', icon: 'success' })
              this.fetchOrders()
            })
            .catch((e: Error) => {
              wx.showToast({ title: e.message || '支付失败', icon: 'none' })
            })
        }
      },
    })
  },

  getStatusText(status: number): string {
    if (status === 0) return '待支付'
    if (status === 1) return '已支付'
    if (status === 2) return '已取消'
    return '未知'
  },
})
