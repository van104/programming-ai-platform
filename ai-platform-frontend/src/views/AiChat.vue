<template>
  <div class="chat-container">
    <!-- 头部导航 -->
    <div class="header">
      <button class="back-btn" @click="$router.push('/home')">返回首页</button>
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>

    <!-- 标题 -->
    <h2>AI 编程助教</h2>

    <!-- 消息列表 -->
    <div class="chat-area" ref="chatArea">
      <div v-if="messages.length === 0" class="empty-tip">
        输入编程问题，AI 助教将为你解答
      </div>
      <div v-for="(msg, index) in messages" :key="index" class="message-row" :class="msg.role">
        <div class="message-bubble">
          <div class="message-text">{{ msg.content }}</div>
        </div>
      </div>
      <div v-if="loading" class="message-row ai">
        <div class="message-bubble loading-bubble">
          思考中...
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <input
        v-model="question"
        class="question-input"
        placeholder="请输入编程问题..."
        @keyup.enter="send"
      />
      <button class="send-btn" :disabled="loading || !question.trim()" @click="send">
        发送
      </button>
    </div>
  </div>
</template>

<script>
import { askAi } from '@/api/ai'

export default {
  data() {
    return {
      question: '',
      messages: [],
      loading: false
    }
  },

  methods: {
    async send() {
      const text = this.question.trim()
      if (!text || this.loading) return

      const userId = localStorage.getItem('userId')

      this.messages.push({ role: 'user', content: text })
      this.question = ''
      this.loading = true
      this.$nextTick(() => this.scrollToBottom())

      try {
        const answer = await askAi(userId, text)
        this.messages.push({ role: 'ai', content: answer })
      } catch (error) {
        this.messages.push({ role: 'ai', content: 'AI 服务异常，请稍后重试' })
      } finally {
        this.loading = false
        this.$nextTick(() => this.scrollToBottom())
      }
    },

    scrollToBottom() {
      const el = this.$refs.chatArea
      if (el) {
        el.scrollTop = el.scrollHeight
      }
    },

    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('userId')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100vh;
  max-width: 700px;
  margin: 0 auto;
  padding: 0 20px;
}

.header {
  position: fixed;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
}

.back-btn {
  padding: 8px 16px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.back-btn:hover {
  background-color: #337ecc;
}

.logout-btn {
  padding: 8px 16px;
  background-color: #f44336;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: #d32f2f;
}

h2 {
  color: #333;
  margin: 60px 0 20px;
}

.chat-area {
  flex: 1;
  width: 100%;
  overflow-y: auto;
  padding: 10px 0;
  margin-bottom: 10px;
}

.empty-tip {
  text-align: center;
  color: #999;
  margin-top: 60px;
  font-size: 14px;
}

.message-row {
  display: flex;
  margin-bottom: 16px;
}

.message-row.user {
  justify-content: flex-end;
}

.message-row.ai {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message-row.user .message-bubble {
  background-color: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-row.ai .message-bubble {
  background-color: #f0f0f0;
  color: #333;
  border-bottom-left-radius: 4px;
}

.loading-bubble {
  color: #999 !important;
}

.input-area {
  display: flex;
  width: 100%;
  padding: 16px 0;
  border-top: 1px solid #e0e0e0;
  gap: 10px;
}

.question-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #dcdfe6;
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.question-input:focus {
  border-color: #409eff;
}

.send-btn {
  padding: 10px 24px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
  white-space: nowrap;
}

.send-btn:hover {
  background-color: #337ecc;
}

.send-btn:disabled {
  background-color: #b0d4f1;
  cursor: not-allowed;
}
</style>
