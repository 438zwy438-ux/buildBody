<template>
  <div class="member-profiles-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会员档案管理</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增会员</el-button>
            <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">
              批量删除
            </el-button>
          </div>
        </div>
      </template>
      
      <!-- 搜索条件区 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="真实姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        <el-form-item label="VIP状态">
          <el-select v-model="searchForm.isVip" placeholder="请选择VIP状态" clearable>
            <el-option label="普通会员" :value="0" />
            <el-option label="VIP会员" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="档案ID" min-width="80" align="center" />
        <el-table-column prop="userId" label="用户ID" min-width="100" align="center" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" />
        <el-table-column prop="gender" label="性别" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="row.gender === 0 ? 'primary' : row.gender === 1 ? 'danger' : 'info'"
              size="small"
            >
              {{ row.gender === 0 ? '男' : row.gender === 1 ? '女' : '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="faceImgUrl" label="人脸照片" min-width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.faceImgUrl"
              :src="row.faceImgUrl"
              :preview-src-list="[row.faceImgUrl]"
              fit="cover"
              style="width: 50px; height: 50px; border-radius: 4px;"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="image-placeholder">
              <el-icon><User /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="isVip" label="VIP状态" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isVip === 1 ? 'warning' : 'info'" size="small">
              {{ row.isVip === 1 ? 'VIP' : '普通会员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" min-width="120" align="center">
          <template #default="{ row }">
            <span>{{ formatDate(row.birthDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
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
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      :before-close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入关联系统用户ID" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="0">男</el-radio>
            <el-radio :label="1">女</el-radio>
            <el-radio :label="2">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="人脸照片" prop="faceImgUrl">
          <el-upload
            class="avatar-uploader"
            action="/api/common/upload"
            :data="{ folder: 'face' }"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="{}"
          >
            <div class="avatar-uploader-content">
              <img v-if="form.faceImgUrl" :src="form.faceImgUrl" class="avatar" />
              <div v-else class="avatar-uploader-placeholder">
                <el-icon><Camera /></el-icon>
                <div class="upload-text">上传人脸照片</div>
              </div>
            </div>
          </el-upload>
          <div class="upload-tip">支持 JPG/PNG 格式，不超过 5MB</div>
        </el-form-item>
        <el-form-item label="VIP状态" prop="isVip">
          <el-radio-group v-model="form.isVip">
            <el-radio :label="0">普通会员</el-radio>
            <el-radio :label="1">VIP会员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker
            v-model="form.birthDate"
            type="date"
            placeholder="选择出生日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, User, Camera } from '@element-plus/icons-vue'
import { 
  getMemberProfileList, 
  createMemberProfile, 
  updateMemberProfile, 
  deleteMemberProfile 
} from '@/api/memberProfile'

// 响应式数据定义
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增会员')
const submitLoading = ref(false)
const formRef = ref(null)
const tableData = ref([])
const selectedRows = ref([])

// 搜索表单
const searchForm = reactive({
  realName: '',
  isVip: null
})

// 分页配置
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表单数据
const form = reactive({
  id: null,
  userId: null,
  realName: '',
  gender: 2,
  faceImgUrl: '',
  isVip: 0,
  birthDate: null
})

// 表单校验规则
const rules = {
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
    { pattern: /^\d+$/, message: '用户ID必须为数字', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
}

// 格式化函数
const formatDateTime = (value) => {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 16)
}

const formatDate = (value) => {
  if (!value) return '-'
  return value.substring(0, 10)
}

// 文件上传处理
const handleAvatarSuccess = (response, uploadFile) => {
  console.log('人脸照片上传响应:', response)
  
  if (typeof response === 'string') {
    form.faceImgUrl = response
  } else if (response.code === 200) {
    form.faceImgUrl = response.data
  } else {
    ElMessage.error('人脸照片上传失败')
    return
  }
  
  ElMessage.success('人脸照片上传成功')
}

const beforeAvatarUpload = (rawFile) => {
  const isValidType = ['image/jpeg', 'image/jpg', 'image/png'].includes(rawFile.type)
  const isLt5M = rawFile.size / 1024 / 1024 < 5

  if (!isValidType) {
    ElMessage.error('人脸照片只能是 JPG/JPEG/PNG 格式!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('人脸照片大小不能超过 5MB!')
    return false
  }
  return true
}

// 表格选择处理
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchData()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  fetchData()
}

// 搜索和重置
const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.realName = ''
  searchForm.isVip = null
  pagination.page = 1
  fetchData()
}

// 弹窗处理
const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    id: null,
    userId: null,
    realName: '',
    gender: 2,
    faceImgUrl: '',
    isVip: 0,
    birthDate: null
  })
}

// 新增操作
const handleAdd = () => {
  dialogTitle.value = '新增会员'
  dialogVisible.value = true
  nextTick(() => {
    resetForm()
  })
}

// 编辑操作
const handleEdit = (row) => {
  dialogTitle.value = '编辑会员'
  dialogVisible.value = true
  nextTick(() => {
    Object.assign(form, { ...row })
  })
}

// 删除操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除会员 "${row.realName}" 的档案吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用删除接口
    await handleDeleteRecord(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedRows.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条会员档案吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const ids = selectedRows.value.map(item => item.id)
    // 调用批量删除接口
    await handleBatchDeleteRecords(ids)
    ElMessage.success(`成功删除 ${ids.length} 条记录`)
    selectedRows.value = []
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    submitLoading.value = true
    
    if (form.id) {
      // 编辑操作
      await handleUpdateRecord(form)
      ElMessage.success('更新成功')
    } else {
      // 新增操作
      await handleAddRecord(form)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}

// API 接口方法
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.size
    }
    
    if (searchForm.realName) {
      params.realName = searchForm.realName
    }
    if (searchForm.isVip !== null) {
      params.isVip = searchForm.isVip
    }
    
    const res = await getMemberProfileList(params)
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
    
  } catch (error) {
    console.error('获取会员档案列表失败:', error)
    tableData.value = []
    pagination.total = 0
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleAddRecord = async (data) => {
  return await createMemberProfile(data)
}

const handleUpdateRecord = async (data) => {
  return await updateMemberProfile(data)
}

const handleDeleteRecord = async (id) => {
  return await deleteMemberProfile([id])
}

const handleBatchDeleteRecords = async (ids) => {
  return await deleteMemberProfile(ids)
}

// 生命周期
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.member-profiles-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
}

.avatar-uploader-content {
  width: 100px;
  height: 100px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.avatar-uploader-content:hover {
  border-color: #409eff;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.avatar-uploader-placeholder {
  text-align: center;
  color: #8c939d;
}

.avatar-uploader-placeholder .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  font-weight: 500;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.image-error,
.image-placeholder {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  color: #909399;
}

.image-placeholder .el-icon {
  font-size: 24px;
}
</style>