import request from '@/utils/request'

export function askAi(userId, question) {
  return request({
    url: '/ai/ask',
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
