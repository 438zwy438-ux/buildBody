<template>
  <div class="member-cards-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会员卡管理</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="会员姓名">
          <el-input v-model="searchForm.memberName" placeholder="请输入会员姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" />
        <el-table-column prop="templateId" label="模板ID" />
        <el-table-column prop="cardNo" label="卡号" />
        <el-table-column prop="totalCount" label="总次数" width="100" />
        <el-table-column prop="remainCount" label="剩余次数" width="100" />
        <el-table-column prop="activeTime" label="激活时间" width="180">
          <template #default="{ row }">
            {{ row.activeTime ? dayjs(row.activeTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="180">
          <template #default="{ row }">
            {{ row.expireTime ? dayjs(row.expireTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="购买时间" width="180">
          <template #default="{ row }">
            {{ row.createTime ? dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'info'"
            >
              {{ row.status === 1 ? '正常' : row.status === 2 ? '冻结' : '已过期' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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


    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="模板ID" prop="templateId">
          <el-input v-model="form.templateId" placeholder="请输入会员卡模板ID" />
        </el-form-item>
        <el-form-item label="卡号" prop="cardNo">
          <el-input v-model="form.cardNo" placeholder="请输入卡号" />
        </el-form-item>
        <el-form-item label="总次数" prop="totalCount">
          <el-input-number v-model="form.totalCount" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="剩余次数" prop="remainCount">
          <el-input-number v-model="form.remainCount" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="激活时间" prop="activeTime">
          <el-date-picker
            v-model="form.activeTime"
            type="datetime"
            placeholder="选择激活时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker
            v-model="form.expireTime"
            type="datetime"
            placeholder="选择过期时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="2">冻结</el-radio>
            <el-radio :label="0">已过期</el-radio>
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
import { getMemberCardList, createMemberCard, updateMemberCard, deleteMemberCard } from '@/api/memberCard'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加会员卡')
const formRef = ref(null)
const tableData = ref([])

const searchForm = reactive({
  memberName: '',
  phone: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  userId: null,
  templateId: null,
  cardNo: '',
  totalCount: 0,
  remainCount: 0,
  activeTime: null,
  expireTime: null,
  status: 1
})

const rules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  templateId: [{ required: true, message: '请输入会员卡模板ID', trigger: 'blur' }],
  cardNo: [{ required: true, message: '请输入卡号', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入总次数', trigger: 'blur' }],
  remainCount: [{ required: true, message: '请输入剩余次数', trigger: 'blur' }],
  activeTime: [{ required: true, message: '请选择激活时间', trigger: 'change' }],
  expireTime: [{ required: true, message: '请选择过期时间', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.size
    }
    if (searchForm.memberName) {
      params.memberName = searchForm.memberName
    }
    if (searchForm.phone) {
      params.phone = searchForm.phone
    }
    const res = await getMemberCardList(params)
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
  } catch (error) {
    console.error('获取会员卡列表失败:', error)
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
  searchForm.memberName = ''
  searchForm.phone = ''
  pagination.page = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '添加会员卡'
  form.id = null
  form.userId = null
  form.templateId = null
  form.cardNo = ''
  form.totalCount = 0
  form.remainCount = 0
  form.activeTime = null
  form.expireTime = null
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑会员卡'
  form.id = row.id
  form.userId = row.userId
  form.templateId = row.templateId
  form.cardNo = row.cardNo
  form.totalCount = row.totalCount
  form.remainCount = row.remainCount
  form.activeTime = row.activeTime
  form.expireTime = row.expireTime
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该会员卡吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteMemberCard([row.id])
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
          await updateMemberCard(form)
        } else {
          await createMemberCard(form)
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
  min-height: 40px;
  align-items: center;
}
</style>