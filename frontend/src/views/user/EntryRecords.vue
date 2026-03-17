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
        <el-table-column prop="duration" label="时长(分钟)" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === '在场' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getEntryLogList } from '@/api/entryLog'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const loading = ref(false)
const tableData = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getEntryLogList({
      current: pagination.page,
      size: pagination.size,
      userId: userInfo.value.id
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取入场记录失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  pagination.size = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  fetchData()
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