<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409eff"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.memberCount || 0 }}</div>
              <div class="stat-label">会员总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67c23a"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayEntryCount || 0 }}</div>
              <div class="stat-label">今日入场</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#e6a23c"><Money /></el-icon>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatMoney(stats.monthIncome || 0) }}</div>
              <div class="stat-label">本月收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#f56c6c"><Calendar /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeCardCount || 0 }}</div>
              <div class="stat-label">有效会员卡</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近入场记录</span>
            </div>
          </template>
          <el-table :data="recentEntries" style="width: 100%" v-loading="loading">
            <el-table-column prop="userName" label="姓名" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="entryTime" label="入场时间" :formatter="formatTime" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === 'IN' ? 'success' : 'info'">{{ row.status === 'IN' ? '在场' : '已离场' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/admin/checkin')">入场登记</el-button>
            <el-button type="success" @click="$router.push('/admin/users')">添加会员</el-button>
            <el-button type="warning" @click="$router.push('/admin/courses')">预约课程</el-button>
            <el-button type="danger" @click="$router.push('/admin/equipment')">器材报修</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getDashboardStats, getRecentEntries } from '@/api/statistics'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const stats = reactive({
  memberCount: 0,
  todayEntryCount: 0,
  monthIncome: 0,
  activeCardCount: 0
})
const recentEntries = ref([])

const formatMoney = (value) => {
  return Number(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const formatTime = (row, column, cellValue) => {
  return formatDate(cellValue)
}

const fetchStats = async () => {
  try {
    const res = await getDashboardStats()
    Object.assign(stats, res.data)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchRecentEntries = async () => {
  loading.value = true
  try {
    const res = await getRecentEntries()
    recentEntries.value = res.data || []
  } catch (error) {
    console.error('获取入场记录失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
  fetchRecentEntries()
})
</script>

<style scoped>
.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  font-size: 48px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.quick-actions .el-button {
  height: 60px;
  font-size: 16px;
}
</style>