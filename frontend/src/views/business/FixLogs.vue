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
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="equipmentId" label="器材ID" />
        <el-table-column prop="equipmentName" label="器材名称" />
        <el-table-column prop="damagePosition" label="损坏位置" />
        <el-table-column prop="damageDescription" label="损坏说明" />
        <el-table-column prop="damageTime" label="损坏时间">
          <template #default="{ row }">
            {{ row.damageTime ? dayjs(row.damageTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="repairerName" label="维修人员" />
        <el-table-column prop="repairTime" label="维修时间">
          <template #default="{ row }">
            {{ row.repairTime ? dayjs(row.repairTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
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

    <el-dialog v-model="dialogVisible" title="添加维修记录" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="选择器械" prop="equipmentId">
          <el-select 
            v-model="form.equipmentId" 
            placeholder="请选择器械" 
            style="width: 100%"
            filterable
            @change="handleEquipmentChange"
            :loading="equipmentLoading"
          >
            <el-option
              v-for="item in equipmentList"
              :key="item.id"
              :label="`${item.name} (${item.code})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="器材名称" prop="equipmentName">
          <el-input v-model="form.equipmentName" placeholder="自动带出" disabled />
        </el-form-item>
        <el-form-item label="损坏位置" prop="damagePosition">
          <el-input v-model="form.damagePosition" placeholder="请输入损坏位置" />
        </el-form-item>
        <el-form-item label="损坏说明" prop="damageDescription">
          <el-input v-model="form.damageDescription" type="textarea" :rows="3" placeholder="请输入损坏说明" />
        </el-form-item>
        <el-form-item label="损坏时间" prop="damageTime">
          <el-date-picker
            v-model="form.damageTime"
            type="datetime"
            placeholder="选择损坏时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="维修人员姓名" prop="repairerName">
          <el-input v-model="form.repairerName" placeholder="请输入维修人员姓名" />
        </el-form-item>
        <el-form-item label="维修人员电话" prop="repairerPhone">
          <el-input v-model="form.repairerPhone" placeholder="请输入维修人员电话" />
        </el-form-item>
        <el-form-item label="维修时间" prop="repairTime">
          <el-date-picker
            v-model="form.repairTime"
            type="datetime"
            placeholder="选择维修时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFixLogList, createFixLog, deleteFixLog } from '@/api/fixLog'
import { getEquipmentList } from '@/api/equipment'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const tableData = ref([])
const equipmentLoading = ref(false)
const equipmentList = ref([])
const submitting = ref(false)

const searchForm = reactive({
  equipmentName: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  equipmentId: '',
  equipmentName: '',
  damagePosition: '',
  damageDescription: '',
  damageTime: null,
  repairerName: '',
  repairerPhone: '',
  repairTime: null
})

const rules = {
  equipmentId: [{ required: true, message: '请选择器械', trigger: 'change' }],
  equipmentName: [{ required: true, message: '器材名称不能为空', trigger: 'blur' }],
  damagePosition: [{ required: true, message: '请输入损坏位置', trigger: 'blur' }],
  damageDescription: [{ required: true, message: '请输入损坏说明', trigger: 'blur' }],
  damageTime: [{ required: true, message: '请选择损坏时间', trigger: 'change' }],
  repairerName: [{ required: true, message: '请输入维修人员姓名', trigger: 'blur' }],
  repairerPhone: [{ required: true, message: '请输入维修人员电话', trigger: 'blur' }]
}

const fetchEquipmentList = async () => {
  equipmentLoading.value = true
  try {
    const res = await getEquipmentList({ current: 1, size: 1000 })
    equipmentList.value = res.data?.records || []
  } catch (error) {
    console.error('获取器械列表失败:', error)
    equipmentList.value = []
  } finally {
    equipmentLoading.value = false
  }
}

const handleEquipmentChange = (equipmentId) => {
  const equipment = equipmentList.value.find(item => item.id === equipmentId)
  if (equipment) {
    form.equipmentName = equipment.name
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.size
    }
    if (searchForm.equipmentName) {
      params.equipmentName = searchForm.equipmentName
    }
    const res = await getFixLogList(params)
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
  } catch (error) {
    console.error('获取维修记录列表失败:', error)
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
  searchForm.equipmentName = ''
  pagination.page = 1
  fetchData()
}

const handleAdd = async () => {
  form.equipmentId = ''
  form.equipmentName = ''
  form.damagePosition = ''
  form.damageDescription = ''
  form.damageTime = null
  form.repairerName = ''
  form.repairerPhone = ''
  form.repairTime = null
  await fetchEquipmentList()
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
      submitting.value = true
      try {
        await createFixLog(form)
        ElMessage.success('添加成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error(error.response?.data?.msg || '操作失败')
      } finally {
        submitting.value = false
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