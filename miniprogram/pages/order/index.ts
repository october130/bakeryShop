// pages/order/index.ts —— 订单列表
import { request } from '../../utils/request'

interface OrderVO {
  id: number
  orderNo: string
  totalPrice: number
  status: string
  createTime: string
  statusClass?: string
  canCancel?: boolean
}

Page({
  data: {
    orders: [] as OrderVO[],
    loading: false,
    error: '',
  },
  onShow() {
    this.fetchOrders()
  },
  fetchOrders() {
    this.setData({ loading: true, error: '' })
    request<OrderVO[]>({ url: '/api/order/list' })
      .then((list) => {
        const orders = (list || []).map((o) => ({
          ...o,
          statusClass: this.getStatusClass(o.status),
          canCancel: o.status === '待支付' || o.status === '已支付',
        }))
        this.setData({ orders, loading: false })
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
  goDetail(e: any) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/order/detail?id=' + id })
  },
  cancelOrder(e: any) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          request({ url: '/api/order/cancel/' + id, method: 'PUT' })
            .then(() => {
              wx.showToast({ title: '取消成功', icon: 'success' })
              this.fetchOrders()
            })
            .catch((err: Error) => {
              wx.showToast({ title: err.message || '取消失败', icon: 'none' })
            })
        }
      },
    })
  },
})
