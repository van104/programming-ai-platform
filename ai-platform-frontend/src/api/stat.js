import request from '@/utils/request'

export function getStat(userId) {
  return request({
    url: '/stat',
    method: 'get',
    params: { userId }
  })
}
