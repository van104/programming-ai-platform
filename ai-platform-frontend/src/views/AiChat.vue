<template>
  <div class="chat-container">
    <div class="header">
      <button class="back-btn" @click="$router.push('/home')">返回首页</button>
    </div>

    <h2>{{ chatMode ? 'AI 对话' : 'AI 代码分析' }}</h2>

    <!-- 模式切换 + 快速填入测试代码 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="mode-switch">
          <span class="tool-label">接口：</span>
          <button
            class="mode-btn"
            :class="{ active: !mockMode }"
            @click="mockMode = false"
          >真实</button>
          <button
            class="mode-btn"
            :class="{ active: mockMode }"
            @click="mockMode = true"
          >模拟</button>
        </div>
        <div class="mode-switch">
          <span class="tool-label">类型：</span>
          <button
            class="mode-btn"
            :class="{ active: !chatMode }"
            @click="chatMode = false"
          >AI 分析</button>
          <button
            class="mode-btn"
            :class="{ active: chatMode }"
            @click="chatMode = true"
          >AI 对话</button>
        </div>
      </div>
      <div v-show="!chatMode" class="test-buttons">
        <span class="tool-label">快速测试：</span>
        <button class="test-btn" @click="fillSample('java')">Java</button>
        <button class="test-btn" @click="fillSample('python')">Python</button>
        <button class="test-btn" @click="fillSample('js')">JavaScript</button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="chat-area" ref="chatArea">
      <div v-if="messages.length === 0" class="empty-tip">
        {{ chatMode ? '输入问题，AI 将为你解答' : '输入代码，AI 将为你分析' }}
      </div>
      <div v-for="(msg, index) in messages" :key="index" class="message-row" :class="msg.role">
        <div class="message-bubble">
          <div class="message-label">{{ msg.role === 'user' ? (chatMode ? '我的问题' : '我的代码') : (chatMode ? 'AI 回答' : 'AI 分析结果') }}</div>
          <pre class="message-text">{{ msg.content }}</pre>
        </div>
      </div>
      <div v-if="loading" class="message-row ai">
        <div class="message-bubble loading-bubble">{{ chatMode ? '思考中...' : '分析中...' }}</div>
      </div>
    </div>

    <!-- 输入区域：textarea -->
    <div class="input-area">
      <textarea
        v-model="code"
        class="code-input"
        rows="6"
        :placeholder="chatMode ? '在此输入问题...' : '在此输入代码...'"
      ></textarea>
      <button class="send-btn" :disabled="loading || !code.trim()" @click="submit">
        {{ chatMode ? '发送问题' : '提交 AI 分析' }}
      </button>
    </div>
  </div>
</template>

<script>
import { askAi, askAiMock, analyzeCode, analyzeCodeMock } from '@/api/ai'
import { samples } from '@/test-samples'

export default {
  data() {
    return {
      code: '',
      messages: [],
      loading: false,
      mockMode: false,
      chatMode: false
    }
  },

  methods: {
    async submit() {
      const text = this.code.trim()
      if (!text || this.loading) return

      const userId = localStorage.getItem('userId')

      this.messages.push({ role: 'user', content: text })
      this.code = ''
      this.loading = true
      this.$nextTick(() => this.scrollToBottom())

      try {
        let answer
        if (this.chatMode) {
          // AI 对话
          answer = this.mockMode
            ? await askAiMock(userId, text)
            : await askAi(userId, text)
        } else {
          // AI 分析 → 调用 /ai/analyze，增加提交次数
          answer = this.mockMode
            ? await analyzeCodeMock(userId, text)
            : await analyzeCode(userId, text)
        }
        this.messages.push({ role: 'ai', content: answer })
      } catch (error) {
        const msg = error.response?.data?.message || error.message || 'AI 服务异常'
        this.messages.push({ role: 'ai', content: msg })
      } finally {
        this.loading = false
        this.$nextTick(() => this.scrollToBottom())
      }
    },
    //
    fillSample(lang) {
      const map = { java: samples.javaCorrect, python: samples.pythonSort, js: samples.jsAsync }
      this.code = map[lang] || ''
    },
    //
    scrollToBottom() {
      const el = this.$refs.chatArea
      if (el) {
        el.scrollTop = el.scrollHeight
      }
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
  max-width: 85%;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
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

.message-label {
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 6px;
  opacity: 0.8;
}

.message-text {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
}

.loading-bubble {
  color: #999 !important;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 10px;
  gap: 8px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mode-switch {
  display: flex;
  align-items: center;
  gap: 6px;
}
.tool-label {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}
.mode-btn {
  padding: 4px 12px;
  font-size: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 12px;
  background: #fff;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}
.mode-btn.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}
.mode-btn:not(.active):hover {
  color: #409eff;
  border-color: #409eff;
}

.test-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}
.test-btn {
  padding: 4px 12px;
  font-size: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 12px;
  background: #fff;
  color: #409eff;
  cursor: pointer;
  transition: all 0.2s;
}
.test-btn:hover {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.input-area {
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 16px 0;
  border-top: 1px solid #e0e0e0;
  gap: 10px;
}

.code-input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  font-size: 14px;
  font-family: 'Courier New', Courier, monospace;
  outline: none;
  resize: vertical;
  transition: border-color 0.3s;
  box-sizing: border-box;
}
.code-input:focus {
  border-color: #409eff;
}

.send-btn {
  align-self: flex-end;
  padding: 10px 24px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}
.send-btn:hover {
  background-color: #337ecc;
}
.send-btn:disabled {
  background-color: #b0d4f1;
  cursor: not-allowed;
}
</style>
