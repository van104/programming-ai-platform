import request from '@/utils/request'

/**
 * 获取班级整体统计数据
 */
export function getClassStatistics() {
  return request({
    url: '/teacher/class/statistics',
    method: 'get'
  })
}

/**
 * 获取所有学生学习画像
 */
export function getAllStudentProfiles() {
  return request({
    url: '/teacher/student/profiles',
    method: 'get'
  })
}

/**
 * 获取指定学生学习画像
 */
export function getStudentProfile(userId) {
  return request({
    url: `/teacher/student/profile/${userId}`,
    method: 'get'
  })
}
