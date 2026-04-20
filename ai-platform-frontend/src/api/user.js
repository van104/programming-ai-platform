import request from '@/utils/request'

/**
 * 获取用户列表
 */
export function getUserList() {
  return request({
    url: '/user/list',
    method: 'get'
  })
}
