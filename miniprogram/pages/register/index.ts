// pages/register/index.ts —— 用户注册
import { request } from '../../utils/request'

Page({
  data: {
    phone: '',
    password: '',
    confirmPassword: '',
    nickname: '',
    submitting: false,
  },

  onPhone(e: any) {
    this.setData({ phone: e.detail.value })
  },

  onPassword(e: any) {
    this.setData({ password: e.detail.value })
  },

  onConfirmPassword(e: any) {
    this.setData({ confirmPassword: e.detail.value })
  },

  onNickname(e: any) {
    this.setData({ nickname: e.detail.value })
  },

  doRegister() {
    const { phone, password, confirmPassword, nickname } = this.data
    if (!phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' })
      return
    }
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' })
      return
    }
    if (password.length < 6) {
      wx.showToast({ title: '密码至少6位', icon: 'none' })
      return
    }
    if (password !== confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    request<any>({
      url: '/api/user/register',
      method: 'POST',
      data: { phone, password, nickname: nickname || '用户' + phone.slice(-4) },
    })
      .then((res) => {
        if (res && res.token) {
          wx.setStorageSync('token', res.token)
          wx.setStorageSync('userInfo', res.userInfo || {})
        }
        wx.showToast({ title: '注册成功', icon: 'success' })
        setTimeout(() => {
          wx.reLaunch({ url: '/pages/index/index' })
        }, 600)
      })
      .catch((e: Error) => {
        wx.showToast({ title: e.message || '注册失败', icon: 'none' })
      })
      .finally(() => {
        this.setData({ submitting: false })
      })
  },

  goLogin() {
    wx.navigateBack()
  },
})
