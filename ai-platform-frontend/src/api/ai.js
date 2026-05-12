import request from '@/utils/request'

/** 真实接口：AI 对话（聊天提问） */
export function askAi(userId, question) {
  return request({
    url: '/ai/ask',
    method: 'post',
    params: { userId, question },
    timeout: 30000
  })
}

/** 真实接口：AI 分析代码 */
export function analyzeCode(userId, code) {
  return request({
    url: '/ai/analyze',
    method: 'post',
    params: { userId, code },
    timeout: 30000
  })
}

/** 模拟接口：后端返回固定结果 */
export function analyzeCodeMock(userId, code) {
  return request({
    url: '/ai/analyze/mock',
    method: 'post',
    params: { userId, code }
  })
}

/** 模拟接口：AI 对话 mock */
export function askAiMock(userId, question) {
  return request({
    url: '/ai/ask/mock',
    method: 'post',
    params: { userId, question }
  })
}

export function getAiFrequency(userId) {
  return request({
    url: `/ai/frequency/${userId}`,
    method: 'get'
  })
}
