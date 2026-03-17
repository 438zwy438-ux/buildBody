<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409eff"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">1,234</div>
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
              <div class="stat-value">567</div>
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
              <div class="stat-value">¥89,000</div>
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
              <div class="stat-value">23</div>
              <div class="stat-label">今日课程</div>
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
          <el-table :data="recentEntries" style="width: 100%">
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="time" label="入场时间" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === '在场' ? 'success' : 'info'">{{ row.status }}</el-tag>
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
            <el-button type="primary" @click="$router.push('/checkin')">入场登记</el-button>
            <el-button type="success" @click="$router.push('/users')">添加会员</el-button>
            <el-button type="warning" @click="$router.push('/courses')">预约课程</el-button>
            <el-button type="danger" @click="$router.push('/equipment')">器材报修</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const recentEntries = ref([
  { name: '张三', phone: '13800138000', time: '2024-03-17 09:30', status: '在场' },
  { name: '李四', phone: '13800138001', time: '2024-03-17 09:15', status: '在场' },
  { name: '王五', phone: '13800138002', time: '2024-03-17 08:45', status: '已离场' },
  { name: '赵六', phone: '13800138003', time: '2024-03-17 08:30', status: '在场' },
  { name: '孙七', phone: '13800138004', time: '2024-03-17 08:00', status: '已离场' }
])
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