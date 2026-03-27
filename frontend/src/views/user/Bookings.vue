<template>
  <div class="bookings-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部预约" name="all">
          <el-table :data="allBookings" v-loading="loading" border>
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="coachName" label="教练" />
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
                <el-button v-if="row.status === 0" type="danger" size="small" @click="handleCancel(row)">取消</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="待核销" name="pending">
          <el-table :data="pendingBookings" v-loading="loading" border>
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="coachName" label="教练" />
            <el-table-column prop="scheduleTime" label="预约时间">
              <template #default="{ row }">
                {{ formatDateTime(row.scheduleTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="预约发起时间">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="handleCancel(row)">取消</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="已完成" name="completed">
          <el-table :data="completedBookings" v-loading="loading" border>
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="coachName" label="教练" />
            <el-table-column prop="scheduleTime" label="预约时间">
              <template #default="{ row }">
                {{ formatDateTime(row.scheduleTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="checkTime" label="核销时间">
              <template #default="{ row }">
                {{ formatDateTime(row.checkTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="预约发起时间">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="已取消" name="cancelled">
          <el-table :data="cancelledBookings" v-loading="loading" border>
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="coachName" label="教练" />
            <el-table-column prop="scheduleTime" label="预约时间">
              <template #default="{ row }">
                {{ formatDateTime(row.scheduleTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="预约发起时间">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyBookings, cancelBooking } from '@/api/courseBooking'
import { getCourseById } from '@/api/course'
import { getCoachProfileByUserId } from '@/api/coachProfile'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const activeTab = ref('all')
const allBookings = ref([])

const pendingBookings = computed(() => allBookings.value.filter(booking => booking.status === 0))
const completedBookings = computed(() => allBookings.value.filter(booking => booking.status === 1))
const cancelledBookings = computed(() => allBookings.value.filter(booking => booking.status === 2))

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMyBookings()
    const bookings = res.data || []
    
    // 为每个预约记录查询课程名称和教练姓名
    for (const booking of bookings) {
      try {
        const courseRes = await getCourseById(booking.courseId)
        booking.courseName = courseRes.data?.name || '未知课程'
        
        if (courseRes.data?.coachUserId) {
          const coachRes = await getCoachProfileByUserId(courseRes.data.coachUserId)
          booking.coachName = coachRes.data?.realName || '未知教练'
        }
      } catch (error) {
        console.error('查询课程或教练信息失败:', error)
        booking.courseName = '未知课程'
        booking.coachName = '未知教练'
      }
    }
    
    allBookings.value = bookings
  } catch (error) {
    console.error('获取预约列表失败:', error)
    allBookings.value = []
  } finally {
    loading.value = false
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const handleCancel = async (booking) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？取消后将恢复一次私教课次数。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelBooking(booking.id)
    ElMessage.success('预约已取消')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约失败:', error)
      ElMessage.error(error.response?.data?.msg || '取消预约失败')
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.bookings-page {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>