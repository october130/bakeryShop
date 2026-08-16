<template>
  <div class="ai-assistant">
    <button class="ai-fab" @click="toggleChat" :class="{ active: isOpen }">
      <el-icon :size="24"><ChatDotRound /></el-icon>
    </button>

    <transition name="slide-up">
      <div class="ai-chat" v-if="isOpen">
        <div class="chat-header">
          <div class="header-info">
            <div class="header-avatar">
              <el-icon :size="20"><Service /></el-icon>
            </div>
            <div>
              <h4>智能助手</h4>
              <p class="status">AI在线 · 随时为您服务</p>
            </div>
          </div>
          <button class="close-btn" @click="isOpen = false">
            <el-icon :size="18"><Close /></el-icon>
          </button>
        </div>

        <div class="chat-messages" ref="messagesRef">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.role]"
          >
            <div class="message-avatar" v-if="msg.role === 'assistant'">
              <el-icon :size="16"><Service /></el-icon>
            </div>
            <div class="message-content">
              {{ msg.content }}
            </div>
          </div>
          <div v-if="loading" class="message assistant">
            <div class="message-avatar">
              <el-icon :size="16"><Service /></el-icon>
            </div>
            <div class="message-content typing">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>

        <div class="chat-input">
          <input
            v-model="inputMessage"
            placeholder="输入您的问题..."
            @keyup.enter="sendMessage"
            :disabled="loading"
          />
          <button @click="sendMessage" :disabled="!inputMessage.trim() || loading">
            <el-icon :size="18"><Promotion /></el-icon>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { aiApi } from '@/api/modules'

const isOpen = ref(false)
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref(null)

const messages = ref([
  {
    role: 'assistant',
    content: '您好！我是蛋糕烘焙坊的智能助手，可以帮您推荐蛋糕、解答订购问题。请问有什么可以帮您的？'
  }
])

const toggleChat = () => {
  isOpen.value = !isOpen.value
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const sendMessage = async () => {
  const text = inputMessage.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputMessage.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const response = await aiApi.recommend(text)
    let reply = ''

    if (response.reason) {
      // 过滤掉 AI 的思考过程，只保留最终回答
      reply = response.reason
        .replace(/<think>[\s\S]*?<\/think>/gi, '')
        .replace(/<thinking>[\s\S]*?<\/thinking>/gi, '')
        .replace(/```[\s\S]*?```/g, '')
        .trim()
      // 如果过滤后为空，尝试找"不好意思"等关键词后的内容
      if (!reply && response.reason.includes('不好意思')) {
        reply = response.reason.substring(response.reason.indexOf('不好意思'))
      }
    }
    if (response.deliverMessage) {
      const deliverMsg = response.deliverMessage
        .replace(/<think>[\s\S]*?<\/think>/g, '')
        .replace(/<think>[\s\S]*?<\/think>/g, '')
        .trim()
      if (deliverMsg) {
        reply += (reply ? '\n\n' : '') + deliverMsg
      }
    }
    if (response.recommendations?.length > 0) {
      reply += '\n\n为您推荐：'
      response.recommendations.forEach((cake, i) => {
        reply += `\n${i + 1}. ${cake.name} - ¥${(cake.price / 100).toFixed(0)}`
      })
    }

    messages.value.push({
      role: 'assistant',
      content: reply || '抱歉，我暂时无法回答这个问题，请稍后再试。'
    })
  } catch (e) {
    messages.value.push({
      role: 'assistant',
      content: '网络似乎出了点问题，请稍后再试。'
    })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.ai-assistant {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 1000;
}

.ai-fab {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: $primary;
  color: $white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba($primary, 0.4);
  transition: $transition;

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 6px 20px rgba($primary, 0.5);
  }

  &.active {
    background: $text-light;
    box-shadow: $shadow-card;
  }
}

.ai-chat {
  position: absolute;
  bottom: 72px;
  right: 0;
  width: 380px;
  height: 520px;
  background: $white;
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: $primary;
  color: $white;

  .header-info {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .header-avatar {
    width: 36px;
    height: 36px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  h4 {
    font-size: 15px;
    font-weight: 500;
    margin-bottom: 2px;
  }

  .status {
    font-size: 12px;
    opacity: 0.8;
  }

  .close-btn {
    background: none;
    border: none;
    color: $white;
    cursor: pointer;
    opacity: 0.8;
    transition: $transition;

    &:hover {
      opacity: 1;
    }
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #f5f5f5;
}

.message {
  display: flex;
  gap: 8px;

  &.user {
    justify-content: flex-end;

    .message-content {
      background: $primary;
      color: $white;
      border-radius: 12px 12px 4px 12px;
    }
  }

  &.assistant {
    .message-content {
      background: $white;
      color: $text-dark;
      border-radius: 12px 12px 12px 4px;
    }
  }

  &-avatar {
    width: 28px;
    height: 28px;
    background: $primary;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $white;
    flex-shrink: 0;
  }

  &-content {
    max-width: 75%;
    padding: 10px 14px;
    font-size: 14px;
    line-height: 1.6;
    white-space: pre-wrap;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
  }
}

.typing {
  display: flex;
  gap: 4px;
  align-items: center;
  padding: 14px !important;

  span {
    width: 6px;
    height: 6px;
    background: $text-muted;
    border-radius: 50%;
    animation: bounce 1.4s infinite;

    &:nth-child(2) { animation-delay: 0.2s; }
    &:nth-child(3) { animation-delay: 0.4s; }
  }
}

@keyframes bounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}

.chat-input {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid $border;
  background: $white;

  input {
    flex: 1;
    padding: 10px 14px;
    border: 1px solid $border;
    border-radius: 20px;
    font-size: 14px;
    outline: none;
    transition: $transition;

    &:focus {
      border-color: $primary;
    }
  }

  button {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: $primary;
    color: $white;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: $transition;

    &:hover:not(:disabled) {
      background: $primary-dark;
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
}
</style>
