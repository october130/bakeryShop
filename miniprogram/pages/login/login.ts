// login.ts —— 手机号+密码登录
import { request } from '../../utils/request'

Page({
  data: {
    phone: '',
    password: '',
    submitting: false,
  },

  onPhone(e: any) {
    this.setData({ phone: e.detail.value })
  },

  onPassword(e: any) {
    this.setData({ password: e.detail.value })
  },

  doLogin() {
    const { phone, password } = this.data
    if (!phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    request<any>({
      url: '/api/user/login',
      method: 'POST',
      data: { phone, password },
    })
      .then((res) => {
        if (res && res.token) {
          wx.setStorageSync('token', res.token)
          wx.setStorageSync('userInfo', res.userInfo || {})
        }
        wx.showToast({ title: '登录成功', icon: 'success' })
        setTimeout(() => {
          wx.reLaunch({ url: '/pages/index/index' })
        }, 600)
      })
      .catch((e: Error) => {
        wx.showToast({ title: e.message || '登录失败', icon: 'none' })
      })
      .finally(() => {
        this.setData({ submitting: false })
      })
  },

  goRegister() {
    wx.navigateTo({ url: '/pages/register/index' })
  },
})
