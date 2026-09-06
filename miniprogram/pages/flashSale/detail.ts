// pages/flashSale/detail.ts —— 限时抢购详情
import { request } from '../../utils/request'

interface FlashSaleVO {
  id: number
  cakeId: number
  cakeName: string
  cakeImage: string
  flashPrice: number
  originalPrice: number
  stock: number
  beginTime: string
  endTime: string
  status: number
}

Page({
  data: {
    detail: null as FlashSaleVO | null,
    loading: false,
    error: '',
    seckilling: false,
  },

  onLoad(query: any) {
    const id = Number(query.id)
    if (id) {
      this.fetchDetail(id)
    }
  },

  fetchDetail(id: number) {
    this.setData({ loading: true, error: '' })
    request<FlashSaleVO>({ url: '/api/flashSale/detail/' + id })
      .then((detail) => {
        this.setData({ detail, loading: false })
      })
      .catch((e: Error) => {
        this.setData({ loading: false, error: e.message })
      })
  },

  doSeckill() {
    const { detail, seckilling } = this.data
    if (!detail || seckilling) return

    this.setData({ seckilling: true })
    request<string>({
      url: '/api/flashSale/' + detail.id + '/seckill',
      method: 'POST',
    })
      .then((msg) => {
        this.setData({ seckilling: false })
        wx.showToast({ title: msg || '抢购成功', icon: 'success' })
        setTimeout(() => {
          wx.navigateTo({ url: '/pages/flashSale/orders' })
        }, 1500)
      })
      .catch((e: Error) => {
        this.setData({ seckilling: false })
        wx.showToast({ title: e.message || '抢购失败', icon: 'none' })
      })
  },
})
