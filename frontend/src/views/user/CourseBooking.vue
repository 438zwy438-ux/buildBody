<template>
  <div class="course-booking-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程预约</span>
          <el-button type="primary" @click="handlePurchase">购买课程</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="教练">
          <el-input v-model="searchForm.coachName" placeholder="请输入教练姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="coachName" label="教练" />
        <el-table-column prop="courseTime" label="课程时间" />
        <el-table-column prop="maxPeople" label="最大人数" />
        <el-table-column prop="currentPeople" label="已报名" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="courseTimes" label="包含次数" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handlePurchase(row)">购买</el-button>
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

    <el-dialog v-model="purchaseDialogVisible" title="购买课程" width="500px">
      <el-form :model="purchaseForm" :rules="purchaseRules" ref="purchaseFormRef" label-width="100px">
        <el-form-item label="课程">
          <el-input v-model="selectedCourse.courseName" disabled />
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="selectedCourse.price" disabled />
        </el-form-item>
        <el-form-item label="包含次数">
          <el-input v-model="selectedCourse.courseTimes" disabled />
        </el-form-item>
        <el-form-item label="购买数量" prop="quantity">
          <el-input-number v-model="purchaseForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="总金额">
          <el-input :value="totalAmount" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="purchaseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmPurchase">确认购买</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getCourseList, purchaseCourse } from '@/api/course'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const loading = ref(false)
const purchaseDialogVisible = ref(false)
const purchaseFormRef = ref(null)
const tableData = ref([])
const selectedCourse = ref({})

const searchForm = reactive({
  courseName: '',
  coachName: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const purchaseForm = reactive({
  courseId: null,
  quantity: 1
})

const purchaseRules = {
  quantity: [{ required: true, message: '请输入购买数量', trigger: 'blur' }]
}

const totalAmount = computed(() => {
  if (!selectedCourse.value.price) return 0
  return (selectedCourse.value.price * purchaseForm.quantity).toFixed(2)
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.size,
      type: 1,
      status: 1
    }
    if (searchForm.courseName) {
      params.courseName = searchForm.courseName
    }
    if (searchForm.coachName) {
      params.coachName = searchForm.coachName
    }
    const res = await getCourseList(params)
    tableData.value = res.data?.records || []
    pagination.total = Number(res.data?.total || 0)
  } catch (error) {
    console.error('获取课程列表失败:', error)
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
  searchForm.courseName = ''
  searchForm.coachName = ''
  pagination.page = 1
  fetchData()
}

const handlePurchase = (row) => {
  selectedCourse.value = row
  purchaseForm.courseId = row.id
  purchaseForm.quantity = 1
  purchaseDialogVisible.value = true
}

const handleConfirmPurchase = async () => {
  if (!purchaseFormRef.value) return
  
  await purchaseFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await purchaseCourse({
          userId: userInfo.value.id,
          courseId: purchaseForm.courseId,
          quantity: purchaseForm.quantity
        })
        ElMessage.success('购买成功')
        purchaseDialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('购买失败:', error)
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
.course-booking-page {
  max-width: 1200px;
  margin: 0 auto;
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
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  min-height: 32px;
  z-index: 1;
}

:deep(.el-pagination) {
  display: flex !important;
  flex-wrap: nowrap;
  align-items: center;
}
</style>