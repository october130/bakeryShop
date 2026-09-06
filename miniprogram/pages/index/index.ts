// index.ts —— 烘焙店列表（先验证"小程序↔后端"连通）
import { request } from '../../utils/request'
import { IMG_URL } from '../../config'

interface Bakery {
  id: number
  name: string
  address: string
  image: string
  avgPrice: number
  distance: number | null
}

interface FlashSale {
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
    banners: [
      { id: 1, imageUrl: IMG_URL + '/banner/banner1.jpg' },
      { id: 2, imageUrl: IMG_URL + '/banner/banner2.jpg' },
      { id: 3, imageUrl: IMG_URL + '/banner/banner3.jpg' },
    ],
    bakeries: [] as Bakery[],
    flashSales: [] as FlashSale[],
    loading: false,
    error: '',
  },
  onLoad() {
    this.fetchBakeries()
    this.fetchFlashSales()
  },
  // 店铺图片映射（临时，等后端加图片字段后删除）
  shopImages: [IMG_URL + '/shop/shop1.jpg', IMG_URL + '/shop/shop2.jpg', IMG_URL + '/shop/shop3.jpg'],

  fetchBakeries() {
    this.setData({ loading: true, error: '' })
    request<Bakery[]>({ url: '/api/bakery/list' })
      .then((list) => {
        const bakeries = (list || []).map((b, idx) => ({
          ...b,
          image: b.image || this.shopImages[idx] || '',
        }))
        this.setData({ bakeries, loading: false })
      })
      .catch((e: Error) => {
        this.setData({ loading: false, error: e.message })
      })
  },
  goBakery(e: any) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/bakery/index?id=' + id })
  },
  fetchFlashSales() {
    request<FlashSale[]>({ url: '/api/flashSale/list' })
      .then((list) => {
        this.setData({ flashSales: list || [] })
      })
      .catch(() => {})
  },
  goFlashSale(e: any) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/flashSale/detail?id=' + id })
  },
  goAiChat() {
    wx.navigateTo({ url: '/pages/ai/chat' })
  },
})
