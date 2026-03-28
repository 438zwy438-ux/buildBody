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
                <!-- 会员人像展示区 -->
                <div class="member-photo-section">
                  <div class="photo-container">
                    <div class="photo-header">
                      <span class="photo-title">会员人像</span>
                      <el-tag 
                        v-if="!item.faceImgUrl" 
                        type="danger" 
                        size="small"
                        class="photo-warning"
                      >
                        该会员未录入人像！
                      </el-tag>
                    </div>
                    <div class="photo-content">
                      <el-image
                        v-if="item.faceImgUrl"
                        :src="item.faceImgUrl"
                        :preview-src-list="[item.faceImgUrl]"
                        fit="cover"
                        class="member-photo"
                        :alt="item.userName + '的人像照片'"
                      >
                        <template #error>
                          <div class="image-error">
                            <el-icon><Picture /></el-icon>
                            <span>图片加载失败</span>
                          </div>
                        </template>
                      </el-image>
                      <div v-else class="photo-placeholder">
                        <el-icon class="placeholder-icon"><User /></el-icon>
                        <span class="placeholder-text">暂无照片</span>
                      </div>
                    </div>
                    <div class="photo-tip">点击图片可放大查看</div>
                  </div>
                  
                  <!-- 会员信息区域 -->
                  <div class="member-info-section">
                    <div class="info-header">
                      <h4 class="member-name">{{ item.userName }}</h4>
                      <el-tag 
                        :type="getCardStatusType(item.cardStatusStr)" 
                        size="small"
                        class="status-tag"
                      >
                        {{ item.cardStatusStr }}
                      </el-tag>
                    </div>
                    
                    <el-descriptions :column="1" size="small" border>
                      <el-descriptions-item label="手机号">{{ item.phone }}</el-descriptions-item>
                      <el-descriptions-item label="会员卡">{{ item.cardName }}</el-descriptions-item>
                      <el-descriptions-item v-if="item.expireTime" label="过期时间">
                        {{ formatDateTime(item.expireTime) }}
                      </el-descriptions-item>
                      <el-descriptions-item v-if="item.remainCount !== null" label="剩余次数">
                        {{ item.remainCount }} 次
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                </div>
                
                <!-- 操作按钮区域 -->
                <div class="result-actions">
                  <el-button 
                    v-if="item.canEntry" 
                    type="primary" 
                    size="small" 
                    @click="handleCheckIn(item.userId)"
                    class="action-btn"
                  >
                    确认入场
                  </el-button>
                  <el-button 
                    v-else 
                    type="primary" 
                    size="small" 
                    disabled
                    class="action-btn"
                  >
                    确认入场
                  </el-button>
                  <el-button 
                    v-if="item.canEntry" 
                    type="warning" 
                    size="small" 
                    @click="handleCheckOut(item.userId)"
                    class="action-btn"
                  >
                    确认出场
                  </el-button>
                  <el-button 
                    v-else 
                    type="warning" 
                    size="small" 
                    disabled
                    class="action-btn"
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
              <span>入场记录</span>
            </template>
            <div class="table-container">
              <el-table :data="tableData" v-loading="loading" border>
                <el-table-column prop="userId" label="用户ID" width="200" />
                <el-table-column prop="userName" label="姓名" width="80" />
                <el-table-column prop="phone" label="手机号" />
                <el-table-column prop="entryTime" label="入场时间">
                  <template #default="{ row }">
                    {{ formatDateTime(row.entryTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="exitTime" label="出场时间">
                  <template #default="{ row }">
                    {{ formatDateTime(row.exitTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'IN' ? 'success' : 'info'">{{ row.status === 'IN' ? '在场' : '已离场' }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row }">
                    <el-button 
                      v-if="row.status === 'IN'" 
                      type="warning" 
                      size="small" 
                      @click="handleTableCheckOut(row)"
                    >
                      确认出场
                    </el-button>
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
import { Picture, User } from '@element-plus/icons-vue'

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

const handleTableCheckOut = async (row) => {
  try {
    await checkOut(row.id)
    ElMessage.success('出场成功')
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
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.result-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 会员人像展示区样式 */
.member-photo-section {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.photo-container {
  flex: 0 0 200px;
  text-align: center;
}

.photo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.photo-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.photo-warning {
  font-size: 12px;
  font-weight: 500;
}

.photo-content {
  position: relative;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  border: 2px solid #e4e7ed;
}

.member-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #c0c4cc;
}

.image-error .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.photo-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #c0c4cc;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 8px;
}

.placeholder-text {
  font-size: 12px;
  color: #909399;
}

.photo-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}

/* 会员信息区域样式 */
.member-info-section {
  flex: 1;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.member-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.status-tag {
  font-weight: 500;
}

/* 操作按钮区域样式 */
.result-actions {
  text-align: right;
  padding-top: 16px;
  border-top: 1px solid #f0f2f5;
}

.action-btn {
  margin-left: 8px;
  min-width: 80px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .member-photo-section {
    flex-direction: column;
    gap: 16px;
  }
  
  .photo-container {
    flex: none;
  }
  
  .info-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
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

:deep(.el-descriptions) {
  margin-top: 8px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-descriptions__content) {
  color: #303133;
  font-weight: 400;
}
</style>