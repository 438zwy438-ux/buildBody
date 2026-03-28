<template>
  <div class="lockers-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>储物柜管理</span>
          <el-button type="primary" @click="handleAdd">添加储物柜</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="柜号">
          <el-input v-model="searchForm.boxNo" placeholder="请输入柜号" clearable />
        </el-form-item>
        <el-form-item label="位置">
          <el-select v-model="searchForm.areaCode" placeholder="请选择位置" clearable>
            <el-option label="男子更衣室" value="man" />
            <el-option label="女子更衣室" value="woman" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="空闲" :value="0" />
            <el-option label="使用中" :value="1" />
            <el-option label="故障" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="boxNo" label="柜号" />
        <el-table-column prop="areaCode" label="位置">
          <template #default="{ row }">
            {{ row.areaCode === 'man' ? '男子更衣室' : '女子更衣室' }}
          </template>
        </el-table-column>
        <el-table-column prop="currentUserId" label="使用人ID" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="success">空闲</el-tag>
            <el-tag v-else-if="row.status === 1" type="warning">使用中</el-tag>
            <el-tag v-else type="info">故障</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isLocker" label="锁状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isLocker === 1 ? 'warning' : 'success'">
              {{ row.isLocker === 1 ? '已上锁' : '已开锁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" type="warning" size="small" @click="handleRelease(row)">释放</el-button>
            <el-button v-if="row.status === 1 && row.isLocker === 1" type="success" size="small" @click="handleUnlock(row)">开锁</el-button>
            <el-button v-if="row.status === 1 && row.isLocker === 0" type="info" size="small" @click="handleLock(row)">上锁</el-button>
            <el-button v-if="row.status === 0" type="primary" size="small" @click="handleTempOpen(row)">临时开柜</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="柜号" prop="boxNo">
          <el-input v-model="form.boxNo" placeholder="请输入柜号" />
        </el-form-item>
        <el-form-item label="位置" prop="areaCode">
          <el-select v-model="form.areaCode" placeholder="请选择位置" style="width: 100%">
            <el-option label="男子更衣室" value="man" />
            <el-option label="女子更衣室" value="woman" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="空闲" :value="0" />
            <el-option label="使用中" :value="1" />
            <el-option label="故障" :value="2" />
          </el-select>
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
import { getLockerList, createLocker, updateLocker, deleteLocker, lockLockerAdmin, adminUnlockLocker, adminReleaseLocker } from '@/api/locker'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加储物柜')
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  boxNo: '',
  areaCode: '',
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  boxNo: '',
  areaCode: 'man',
  status: 0
})

const rules = {
  boxNo: [{ required: true, message: '请输入柜号', trigger: 'blur' }],
  areaCode: [{ required: true, message: '请选择位置', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.size
    }
    if (searchForm.boxNo) {
      params.boxNo = searchForm.boxNo
    }
    if (searchForm.areaCode) {
      params.areaCode = searchForm.areaCode
    }
    if (searchForm.status !== null && searchForm.status !== '') {
      params.status = searchForm.status
    }
    const res = await getLockerList(params)
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
  } catch (error) {
    console.error('获取储物柜列表失败:', error)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.boxNo = ''
  searchForm.areaCode = ''
  searchForm.status = null
  pagination.page = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '添加储物柜'
  form.id = null
  form.boxNo = ''
  form.areaCode = 'man'
  form.status = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑储物柜'
  form.id = row.id
  form.boxNo = row.boxNo
  form.areaCode = row.areaCode
  form.status = row.status
  dialogVisible.value = true
}

const handleRelease = async (row) => {
  try {
    await ElMessageBox.confirm('确定要释放该储物柜吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await adminReleaseLocker(row.id)
    ElMessage.success('释放成功')
    fetchData()
  } catch (error) {
    console.log('取消释放')
  }
}

const handleLock = async (row) => {
  try {
    await lockLockerAdmin(row.id)
    ElMessage.success('上锁成功')
    fetchData()
  } catch (error) {
    console.error('上锁失败:', error)
  }
}

const handleUnlock = async (row) => {
  try {
    await unlockLocker(row.id)
    ElMessage.success('解锁成功')
    fetchData()
  } catch (error) {
    console.error('解锁失败:', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该储物柜吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteLocker([row.id])
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    console.log('取消删除')
  }
}

const handleTempOpen = async (row) => {
  try {
    await ElMessageBox.confirm('确定要临时开柜吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await adminUnlockLocker(row.id)
    ElMessage.success('临时开柜成功')
    fetchData()
  } catch (error) {
    console.log('取消临时开柜')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateLocker(form)
        } else {
          await createLocker(form)
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

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>