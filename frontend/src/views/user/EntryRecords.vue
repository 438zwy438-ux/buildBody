<template>
  <div class="entry-records-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>入场记录</span>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="entryTime" label="入场时间" />
        <el-table-column prop="exitTime" label="出场时间" />
        <el-table-column label="时长(分钟)">
          <template #default="{ row }">
            {{ calculateDuration(row) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'IN' ? 'success' : 'info'">{{ row.status === 'IN' ? '在场' : '已出场' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small"
              :disabled="row.status !== 'IN'"
              @click="handleCheckOut(row)"
            >
              出场
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyLogs, checkOut } from '@/api/entryLog'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMyLogs()
    tableData.value = res.data
  } catch (error) {
    console.error('获取入场记录失败:', error)
  } finally {
    loading.value = false
  }
}

const calculateDuration = (row) => {
  if (!row.exitTime) return '-'
  const entryTime = dayjs(row.entryTime)
  const exitTime = dayjs(row.exitTime)
  const duration = exitTime.diff(entryTime, 'minute', true)
  return Math.round(duration)
}

const handleCheckOut = async (row) => {
  try {
    await checkOut(row.id)
    ElMessage.success('出场成功')
    fetchData()
  } catch (error) {
    ElMessage.error('出场失败: ' + (error.message || '未知错误'))
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.entry-records-page {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>