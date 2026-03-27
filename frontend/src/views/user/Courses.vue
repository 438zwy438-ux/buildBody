<template>
  <div class="courses-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的私教课</span>
        </div>
      </template>
      
      <el-table :data="myCourses" v-loading="loading" border>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="coachRealName" label="教练" />
        <el-table-column prop="totalCount" label="总次数" />
        <el-table-column prop="remainCount" label="剩余次数" />
        <el-table-column prop="totalAmount" label="总金额" />
        <el-table-column prop="createTime" label="购买时间">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleBook(row)" :disabled="row.remainCount <= 0">预约上课</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getMyCoursesOrders } from '@/api/order'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const loading = ref(false)
const myCourses = ref([])

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const fetchMyCourses = async () => {
  loading.value = true
  try {
    const res = await getMyCoursesOrders()
    myCourses.value = res.data || []
  } catch (error) {
    console.error('获取我的私教课失败:', error)
  } finally {
    loading.value = false
  }
}

const handleBook = (order) => {
  router.push('/user/course-booking')
}

onMounted(() => {
  fetchMyCourses()
})
</script>

<style scoped>
.courses-page {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>