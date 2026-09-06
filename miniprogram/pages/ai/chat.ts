// pages/ai/chat.ts —— AI 蛋糕推荐聊天
import { request } from '../../utils/request'

interface Cake {
  id: number
  name: string
  image: string
  price: number
  description: string
}

interface CakeRecommendation {
  recommendations: Cake[]
  reason: string
  deliverMessage: string
}

interface ChatMessage {
  role: 'user' | 'ai'
  content: string
  cakes?: Cake[]
}

Page({
  data: {
    messages: [
      {
        role: 'ai',
        content: '你好！我是 AI 蛋糕助手 🎂\n告诉我你的需求，比如：\n• 想要什么口味的蛋糕？\n• 适合什么场合？\n• 预算大概多少？\n我来为你推荐～',
      },
    ] as ChatMessage[],
    inputVal: '',
    loading: false,
    sessionId: 'session_' + Date.now(),
    scrollTop: 0,
  },

  onInput(e: any) {
    this.setData({ inputVal: e.detail.value })
  },

  sendMessage() {
    const { inputVal, loading } = this.data
    const text = inputVal.trim()
    if (!text || loading) return

    const userMsg: ChatMessage = { role: 'user', content: text }
    const messages = [...this.data.messages, userMsg]

    this.setData({
      messages,
      inputVal: '',
      loading: true,
      scrollTop: messages.length * 1000,
    })

    request<CakeRecommendation>({
      url: '/ai/recommend',
      method: 'GET',
      data: {
        userMsg: text,
        sessionId: this.data.sessionId,
      },
    })
      .then((res) => {
        const aiMsg: ChatMessage = {
          role: 'ai',
          content: res.deliverMessage || res.reason || '已为您找到推荐蛋糕～',
          cakes: res.recommendations || [],
        }
        const newMessages = [...this.data.messages, aiMsg]
        this.setData({
          messages: newMessages,
          loading: false,
          scrollTop: newMessages.length * 1000,
        })
      })
      .catch((e: Error) => {
        const errMsg: ChatMessage = {
          role: 'ai',
          content: '抱歉，出了点问题：' + (e.message || '网络异常'),
        }
        const newMessages = [...this.data.messages, errMsg]
        this.setData({
          messages: newMessages,
          loading: false,
          scrollTop: newMessages.length * 1000,
        })
      })
  },

  goCakeDetail(e: any) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/cake/detail?id=' + id })
  },
})
