// pages/mine/index.ts —— 个人中心
import { request } from '../../utils/request'

interface UserInfo {
  id: number
  phone: string
  nickname: string
  avatar: string
  role: string
  createTime: string
}

Page({
  data: {
    userInfo: null as UserInfo | null,
    menuItems: [
      {
        title: '购物车',
        desc: '查看已选商品',
        url: '/pages/cart/index',
      },
      {
        title: '设置',
        desc: '账号与安全',
        url: '',
      },
    ],
  },
  onShow() {
    this.fetchUserInfo()
  },
  fetchUserInfo() {
    const token = wx.getStorageSync('token')
    if (!token) return
    request<UserInfo>({ url: '/api/user/info' })
      .then((userInfo) => {
        if (userInfo) {
          wx.setStorageSync('userInfo', userInfo)
          this.setData({ userInfo })
        }
      })
      .catch(() => {})
  },
  goToPage(e: any) {
    const url = e.currentTarget.dataset.url
    if (url) {
      wx.navigateTo({ url })
    } else {
      wx.showToast({ title: '功能开发中', icon: 'none' })
    }
  },
  doLogout() {
    wx.showModal({
      title: '提示',
      content: '确定退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.reLaunch({ url: '/pages/login/login' })
        }
      },
    })
  },
})
