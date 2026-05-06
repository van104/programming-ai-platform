<template>
  <div class="stat-container">
    <!-- 头部导航 -->
    <div class="header">
      <button class="ai-btn" @click="$router.push('/ai-chat')">AI 助教</button>
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>

    <h2>学习数据统计</h2>

    <!-- ECharts 图表容器 -->
    <div id="chart" class="chart-box"></div>

    <!-- 数据卡片 -->
    <div class="stat-cards">
      <div class="stat-card ai-card">
        <div class="stat-number">{{ statData.aiCount }}</div>
        <div class="stat-label">AI 使用次数</div>
      </div>
      <div class="stat-card submit-card">
        <div class="stat-number">{{ statData.submitCount }}</div>
        <div class="stat-label">代码提交次数</div>
      </div>
    </div>

    <button class="back-btn" @click="$router.push('/home')">返回首页</button>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getStat } from '@/api/stat'

export default {
  data() {
    return {
      statData: {
        aiCount: 0,
        submitCount: 0
      }
    }
  },

  async mounted() {
    try {
      const userId = localStorage.getItem('userId')
      const data = await getStat(userId)
      this.statData = data

      const chart = echarts.init(document.getElementById('chart'))
      chart.setOption({
        title: {
          text: '学习数据统计'
        },
        tooltip: {},
        xAxis: {
          data: ['AI使用次数', '提交次数']
        },
        yAxis: {},
        series: [{
          name: '次数',
          type: 'bar',
          data: [
            data.aiCount,
            data.submitCount
          ]
        }]
      })
    } catch (error) {
      console.error('加载统计数据失败:', error)
    }
  },

  methods: {
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
.stat-container {
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

.ai-btn {
  padding: 8px 16px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.ai-btn:hover {
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

.chart-box {
  width: 600px;
  height: 400px;
  margin-bottom: 30px;
}

.stat-cards {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
}

.stat-card {
  width: 200px;
  padding: 24px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.ai-card {
  background-color: #ecf5ff;
  border: 1px solid #d9ecff;
}

.submit-card {
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 8px;
}

.ai-card .stat-number {
  color: #409eff;
}

.submit-card .stat-number {
  color: #67c23a;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.back-btn {
  padding: 10px 24px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
  margin-bottom: 40px;
}

.back-btn:hover {
  background-color: #337ecc;
}
</style>
