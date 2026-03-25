<template>
  <div class="checkin-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>入场管理</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>会员核验</span>
            </template>
            <el-form :model="searchForm" label-width="80px">
              <el-form-item label="手机号">
                <el-input v-model="searchForm.phone" placeholder="请输入会员手机号" clearable />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSearch" :loading="searchLoading">搜索</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
            
            <div v-if="searchResult.length > 0" class="search-result">
              <el-divider>搜索结果</el-divider>
              <div v-for="(item, index) in searchResult" :key="index" class="result-item">
                <div class="result-info">
                  <p><strong>姓名:</strong> {{ item.realName }}</p>
                  <p><strong>手机:</strong> {{ item.phone }}</p>
                  <p><strong>会员卡:</strong> {{ item.cardName }}</p>
                  <p><strong>会员卡状态:</strong> 
                    <el-tag :type="getCardStatusType(item.cardStatusStr)">{{ item.cardStatusStr }}</el-tag>
                  </p>
                  <p v-if="item.expireTime"><strong>过期时间:</strong> {{ formatDateTime(item.expireTime) }}</p>
                  <p v-if="item.remainCount !== null"><strong>剩余次数:</strong> {{ item.remainCount }}</p>
                </div>
                <div class="result-actions">
                  <el-button 
                    v-if="item.canEntry" 
                    type="primary" 
                    size="small" 
                    @click="handleCheckIn(item.userId)"
                  >
                    确认入场
                  </el-button>
                  <el-button 
                    v-else 
                    type="primary" 
                    size="small" 
                    disabled
                  >
                    确认入场
                  </el-button>
                  <el-button 
                    v-if="item.canEntry" 
                    type="warning" 
                    size="small" 
                    @click="handleCheckOut(item.userId)"
                  >
                    确认出场
                  </el-button>
                  <el-button 
                    v-else 
                    type="warning" 
                    size="small" 
                    disabled
                  >
                    确认出场
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="16">
          <el-card shadow="hover" class="table-card">
            <template #header>
              <span>今日入场记录</span>
            </template>
            <div class="table-container">
              <el-table :data="tableData" v-loading="loading" border>
                <el-table-column prop="userId" label="用户ID" width="80" />
                <el-table-column prop="realName" label="姓名" />
                <el-table-column prop="phone" label="手机号" />
                <el-table-column prop="entryTime" label="入场时间" />
                <el-table-column prop="exitTime" label="出场时间" />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === '在场' ? 'success' : 'info'">{{ row.status }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
          
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.size"
              :page-sizes="[10, 20, 50, 100]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getEntryLogList, searchMember, checkIn, checkOut } from '@/api/entryLog'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const searchLoading = ref(false)
const tableData = ref([])
const searchResult = ref([])

const searchForm = reactive({
  phone: ''
})

const pagination = reactive({
  page:1,
  size: 10,
  total: 0
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getEntryLogList({
      current: pagination.page,
      size: pagination.size
    })
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
  } catch (error) {
    console.error('获取入场记录失败:', error)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (!searchForm.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  
  searchLoading.value = true
  try {
    const res = await searchMember({ phone: searchForm.phone })
    searchResult.value = res.data
  } catch (error) {
    console.error('搜索会员失败:', error)
  } finally {
    searchLoading.value = false
  }
}

const handleReset = () => {
  searchForm.phone = ''
  searchResult.value = []
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const getCardStatusType = (status) => {
  const statusMap = {
    '正常': 'success',
    '已过期': 'danger',
    '次数不足': 'warning',
    '卡状态异常(冻结/作废)': 'danger',
    '无会员卡': 'info'
  }
  return statusMap[status] || 'info'
}

const handleCheckIn = async (userId) => {
  try {
    await checkIn(userId)
    ElMessage.success('入场成功')
    handleSearch()
    fetchData()
  } catch (error) {
    console.error('入场失败:', error)
  }
}

const handleCheckOut = async (userId) => {
  try {
    await checkOut(userId)
    ElMessage.success('出场成功')
    handleSearch()
    fetchData()
  } catch (error) {
    console.error('出场失败:', error)
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding: 10px 0;
}

.search-result {
  margin-top: 20px;
}

.result-item {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 10px;
  background-color: #f9fafc;
}

.result-info p {
  margin: 5px 0;
  color: #606266;
}

.result-actions {
  margin-top: 10px;
  text-align: right;
}

.table-card {
  margin-bottom: 20px;
}

.table-container {
  min-height: 200px;
  max-height: 500px;
  overflow: auto;
}

:deep(.el-table) {
  flex: 1;
}

:deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: 100%;
}
</style>