// pages/order/detail.ts —— 订单详情
import { request } from '../../utils/request'

interface OrderItemVO {
  cakeId: number
  cakeName: string
  price: number
  amount: number
  customInfo: string
}

interface OrderVO {
  id: number
  orderNo: string
  totalPrice: number
  status: string
  createTime: string
  address?: string
  phone?: string
  remark?: string
  items?: OrderItemVO[]
}

Page({
  data: {
    order: null as OrderVO | null,
    loading: false,
    error: '',
    statusClass: '',
    canCancel: false,
    canPay: false,
  },
  onLoad(query: any) {
    const id = Number(query.id)
    if (id) {
      this.fetchDetail(id)
    }
  },
  fetchDetail(id: number) {
    this.setData({ loading: true, error: '' })
    request<OrderVO>({ url: '/api/order/' + id })
      .then((order) => {
        this.setData({
          order,
          loading: false,
          statusClass: this.getStatusClass(order.status),
          canCancel: order.status === '待支付' || order.status === '已支付',
          canPay: order.status === '待支付',
        })
      })
      .catch((e: Error) => {
        this.setData({ loading: false, error: e.message })
      })
  },
  getStatusClass(status: string): string {
    if (status === '待支付') return 'pending'
    if (status === '已支付' || status === '制作中' || status === '配送中') return 'processing'
    if (status === '已完成') return 'done'
    if (status === '已取消') return 'canceled'
    return ''
  },
  cancelOrder() {
    const order = this.data.order
    if (!order) return
    wx.showModal({
      title: '提示',
      content: '确定取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          request({ url: '/api/order/cancel/' + order.id, method: 'PUT' })
            .then(() => {
              wx.showToast({ title: '取消成功', icon: 'success' })
              this.fetchDetail(order.id)
            })
            .catch((err: Error) => {
              wx.showToast({ title: err.message || '取消失败', icon: 'none' })
            })
        }
      },
    })
  },
  payOrder() {
    const order = this.data.order
    if (!order) return
    request({
      url: '/api/order/pay',
      method: 'POST',
      data: { orderNo: order.orderNo, totalAmount: order.totalPrice },
    })
      .then(() => {
        wx.showToast({ title: '支付成功', icon: 'success' })
        this.fetchDetail(order.id)
      })
      .catch((e: Error) => {
        wx.showToast({ title: e.message || '支付失败', icon: 'none' })
      })
  },
})
