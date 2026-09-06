// bakery/index.ts —— 店铺详情 + 店铺蛋糕列表
import { request } from '../../utils/request'

interface Cake {
  id: number
  name: string
  image: string
  price: number
  description: string
  sold: number
  customizable: number
}

Page({
  data: {
    bakery: null as any,
    cakes: [] as Cake[],
    loading: false,
    error: '',
  },
  onLoad(query: any) {
    const id = Number(query.id)
    this.fetchBakery(id)
    this.fetchCakes(id)
  },
  fetchBakery(id: number) {
    request({ url: '/api/bakery/detail/' + id })
      .then((bakery: any) => this.setData({ bakery }))
      .catch(() => {})
  },
  fetchCakes(id: number) {
    this.setData({ loading: true, error: '' })
    request<Cake[]>({ url: '/api/cake/list/' + id })
      .then((list) => this.setData({ cakes: list || [], loading: false }))
      .catch((e: Error) => this.setData({ loading: false, error: e.message }))
  },
  goCake(e: any) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/cake/detail?id=' + id })
  },
})
