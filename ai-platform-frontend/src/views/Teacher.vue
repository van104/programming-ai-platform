<template>
  <div class="teacher-container">
    <!-- 头部导航 -->
    <div class="header">
      <button class="nav-btn" @click="$router.push('/home')">返回首页</button>
    </div>

    <h2>教师教学支持中心</h2>

    <!-- 班级概览 -->
    <div class="overview-cards">
      <div class="overview-card">
        <div class="overview-number">{{ stats.totalStudentCount }}</div>
        <div class="overview-label">学生总数</div>
      </div>
      <div class="overview-card">
        <div class="overview-number">{{ stats.totalAiUsageCount }}</div>
        <div class="overview-label">总 AI 使用次数</div>
      </div>
      <div class="overview-card">
        <div class="overview-number">{{ stats.totalSubmissionCount }}</div>
        <div class="overview-label">总提交次数</div>
      </div>
      <div class="overview-card">
        <div class="overview-number">{{ stats.avgAiUsage }}</div>
        <div class="overview-label">人均 AI 使用</div>
      </div>
      <div class="overview-card">
        <div class="overview-number">{{ stats.avgSubmission }}</div>
        <div class="overview-label">人均提交</div>
      </div>
    </div>

    <!-- 学生学习画像列表 -->
    <h3>学生学习概况</h3>
    <div class="table-wrapper">
      <table class="profile-table">
        <thead>
          <tr>
            <th>用户 ID</th>
            <th>AI 使用次数</th>
            <th>提交次数</th>
            <th>练习数</th>
            <th>活跃度</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="profile in profiles" :key="profile.id">
            <td>{{ profile.userId }}</td>
            <td>{{ profile.aiUsageCount }}</td>
            <td>{{ profile.submissionCount }}</td>
            <td>{{ profile.distinctExerciseCount }}</td>
            <td>
              <span class="activity-tag" :class="profile.activityLevel">
                {{ profile.activityLevel || '未知' }}
              </span>
            </td>
          </tr>
          <tr v-if="profiles.length === 0">
            <td colspan="5" class="empty-row">暂无学生学习数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="actions">
      <button class="refresh-btn" @click="load">刷新数据</button>
    </div>
  </div>
</template>

<script>
import { getClassStatistics, getAllStudentProfiles } from '@/api/teacher'

export default {
  data() {
    return {
      stats: {},
      profiles: []
    }
  },

  async mounted() {
    await this.load()
  },

  methods: {
    async load() {
      try {
        const [stats, profiles] = await Promise.all([
          getClassStatistics(),
          getAllStudentProfiles()
        ])
        this.stats = stats
        this.profiles = profiles
      } catch (error) {
        console.error('加载教学数据失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.teacher-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
  padding: 0 20px;
}

.header {
  position: fixed;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
}

.nav-btn {
  padding: 8px 16px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.nav-btn:hover {
  background-color: #337ecc;
}


h2 {
  color: #333;
  margin: 60px 0 10px;
}

h3 {
  color: #333;
  margin: 30px 0 15px;
  text-align: center;
}

.overview-cards {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  justify-content: center;
  margin-bottom: 10px;
}

.overview-card {
  width: 150px;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
}

.overview-number {
  font-size: 32px;
  font-weight: bold;
  color: #67c23a;
  margin-bottom: 6px;
}

.overview-label {
  font-size: 13px;
  color: #666;
}

.table-wrapper {
  width: 100%;
  max-width: 900px;
  overflow-x: auto;
}

.profile-table {
  width: 100%;
  border-collapse: collapse;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

.profile-table thead {
  background-color: #67c23a;
  color: #fff;
}

.profile-table th {
  padding: 12px 16px;
  text-align: left;
  font-size: 14px;
  font-weight: 500;
}

.profile-table td {
  padding: 12px 16px;
  font-size: 14px;
  border-bottom: 1px solid #ebeef5;
  color: #333;
}

.profile-table tbody tr:hover {
  background-color: #f5f7fa;
}

.profile-table tbody tr:last-child td {
  border-bottom: none;
}

.activity-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.activity-tag.高活跃 {
  background-color: #fef0f0;
  color: #f56c6c;
}

.activity-tag.中活跃 {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.activity-tag.低活跃 {
  background-color: #ecf5ff;
  color: #409eff;
}

.empty-row {
  text-align: center;
  color: #999;
  padding: 30px 0;
}

.actions {
  margin-top: 20px;
  margin-bottom: 40px;
}

.refresh-btn {
  padding: 10px 24px;
  background-color: #67c23a;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.refresh-btn:hover {
  background-color: #529b2e;
}
</style>
