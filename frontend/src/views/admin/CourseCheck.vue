<template>
  <div class="course-check-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程核销</span>
        </div>
      </template>
      
      <el-table :data="bookings" v-loading="loading" border>
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="userName" label="会员" />
        <el-table-column prop="scheduleTime" label="预约时间">
          <template #default="{ row }">
            {{ formatDateTime(row.scheduleTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待核销</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">已完成</el-tag>
            <el-tag v-else-if="row.status === 2" type="info">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="预约发起时间">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0" 
              type="primary" 
              size="small" 
              @click="handleCheck(row)">
              核销
            </el-button>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已核销</el-tag>
            <el-tag v-else-if="row.status === 2" type="info" size="small">已取消</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getCoachBookings, checkCourse } from '@/api/courseBooking'
import { getCourseById } from '@/api/course'
import { getUserById } from '@/api/user'
import { getCoachProfileByUserId } from '@/api/coachProfile'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const loading = ref(false)
const bookings = ref([])

const isAdmin = computed(() => {
  const roles = userStore.roles || []
  return roles.includes('admin')
})

const isCoach = computed(() => {
  const roles = userStore.roles || []
  return roles.includes('coach')
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getCoachBookings()
    const bookingList = res.data || []
    
    // 为每个预约记录查询课程名称和会员姓名
    for (const booking of bookingList) {
      try {
        // 查询课程名称
        const courseRes = await getCourseById(booking.courseId)
        booking.courseName = courseRes.data?.name || '未知课程'
        
        // 查询会员姓名
        const userRes = await getUserById(booking.userId)
        booking.userName = userRes.data?.nickname || userRes.data?.username || '未知会员'
        
        // 查询教练姓名（仅管理员需要）
        if (isAdmin.value && courseRes.data?.coachUserId) {
          const coachRes = await getCoachProfileByUserId(courseRes.data.coachUserId)
          booking.coachName = coachRes.data?.realName || '未知教练'
        }
      } catch (error) {
        console.error('查询课程或用户信息失败:', error)
        booking.courseName = '未知课程'
        booking.userName = '未知会员'
      }
    }
    
    bookings.value = bookingList
  } catch (error) {
    console.error('获取预约列表失败:', error)
    bookings.value = []
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const handleCheck = async (booking) => {
  try {
    await ElMessageBox.confirm('确定要核销这个课程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await checkCourse(booking.id)
    ElMessage.success('课程核销成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('核销课程失败:', error)
      ElMessage.error(error.response?.data?.msg || '核销课程失败')
    }
  }
}

onMounted(() => {
  if (isAdmin.value || isCoach.value) {
    fetchData()
  }
})
</script>

<style scoped>
.course-check-page {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>