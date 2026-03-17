<template>
  <div class="fix-logs-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>维修记录管理</span>
          <el-button type="primary" @click="handleAdd">添加记录</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="器材名称">
          <el-input v-model="searchForm.equipmentName" placeholder="请输入器材名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待维修" :value="1" />
            <el-option label="维修中" :value="2" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="equipmentId" label="器材ID" />
        <el-table-column prop="equipmentName" label="器材名称" />
        <el-table-column prop="faultDesc" label="故障描述" />
        <el-table-column prop="reportTime" label="报修时间" />
        <el-table-column prop="fixTime" label="维修时间" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="danger">待维修</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning">维修中</el-tag>
            <el-tag v-else type="success">已完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="器材ID" prop="equipmentId">
          <el-input v-model="form.equipmentId" placeholder="请输入器材ID" />
        </el-form-item>
        <el-form-item label="器材名称" prop="equipmentName">
          <el-input v-model="form.equipmentName" placeholder="请输入器材名称" />
        </el-form-item>
        <el-form-item label="故障描述" prop="faultDesc">
          <el-input v-model="form.faultDesc" type="textarea" :rows="3" placeholder="请输入故障描述" />
        </el-form-item>
        <el-form-item label="报修时间" prop="reportTime">
          <el-date-picker
            v-model="form.reportTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="维修时间" prop="fixTime">
          <el-date-picker
            v-model="form.fixTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="维修备注" prop="fixNotes">
          <el-input v-model="form.fixNotes" type="textarea" :rows="3" placeholder="请输入维修备注" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">待维修</el-radio>
            <el-radio :label="2">维修中</el-radio>
            <el-radio :label="3">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFixLogList, createFixLog, updateFixLog, deleteFixLog } from '@/api/fixLog'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加记录')
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  equipmentName: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  equipmentId: null,
  equipmentName: '',
  faultDesc: '',
  reportTime: null,
  fixTime: null,
  fixNotes: '',
  status: 1
})

const rules = {
  equipmentId: [{ required: true, message: '请输入器材ID', trigger: 'blur' }],
  equipmentName: [{ required: true, message: '请输入器材名称', trigger: 'blur' }],
  faultDesc: [{ required: true, message: '请输入故障描述', trigger: 'blur' }],
  reportTime: [{ required: true, message: '请选择报修时间', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getFixLogList({
      current: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取维修记录列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.equipmentName = ''
  searchForm.status = null
  pagination.page = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '添加记录'
  form.id = null
  form.equipmentId = null
  form.equipmentName = ''
  form.faultDesc = ''
  form.reportTime = null
  form.fixTime = null
  form.fixNotes = ''
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑记录'
  form.id = row.id
  form.equipmentId = row.equipmentId
  form.equipmentName = row.equipmentName
  form.faultDesc = row.faultDesc
  form.reportTime = row.reportTime
  form.fixTime = row.fixTime
  form.fixNotes = row.fixNotes
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该维修记录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteFixLog([row.id])
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    console.log('取消删除')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateFixLog(form)
        } else {
          await createFixLog(form)
        }
        ElMessage.success(form.id ? '更新成功' : '添加成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('提交失败:', error)
      }
    }
  })
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
</style>