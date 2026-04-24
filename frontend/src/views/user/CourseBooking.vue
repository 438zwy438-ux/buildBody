<template>
  <div class="course-booking-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的私教课</span>
        </div>
      </template>

      <el-table :data="orderList" v-loading="loading" border>
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

    <el-dialog v-model="bookingDialogVisible" title="预约上课" width="800px">
      <el-tabs v-model="activeDayIndex" type="card">
        <el-tab-pane v-for="(day, index) in availableSlots" :key="index" :label="day.date">
          <el-row :gutter="10">
            <el-col v-for="(slot, slotIndex) in day.slots" :key="slotIndex" :span="8">
              <el-button
                :disabled="slot.isBooked"
                :type="selectedSlot === slot.timeSlot ? 'primary' : 'default'"
                @click="handleSelectSlot(slot)"
                style="width: 100%; margin-bottom: 10px;"
              >
                {{ slot.timeSlot }}
              </el-button>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmBook" :disabled="!selectedSlot">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getMyCoursesOrders } from '@/api/order'
import { getAvailableSlots, bookCourse } from '@/api/course'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const loading = ref(false)
const bookingDialogVisible = ref(false)
const orderList = ref([])
const availableSlots = ref([])
const activeDayIndex = ref(0)
const selectedSlot = ref('')
const selectedStartTime = ref('')
const selectedEndTime = ref('')
const currentOrder = ref(null)

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const fetchOrderList = async () => {
  loading.value = true
  try {
    const res = await getMyCoursesOrders()
    // 后端已经过滤了私教课订单和状态，直接使用返回的数据
    orderList.value = res.data || []
  } catch (error) {
    console.error('获取订单列表失败:', error)
    orderList.value = []
  } finally {
    loading.value = false
  }
}

const handleBook = async (row) => {
  currentOrder.value = row
  try {
    // 使用行数据中的教练ID获取可用时间槽
    const res = await getAvailableSlots(row.coachUserId)
    availableSlots.value = res.data || []
    activeDayIndex.value = 0
    selectedSlot.value = ''
    selectedStartTime.value = ''
    selectedEndTime.value = ''
    bookingDialogVisible.value = true
  } catch (error) {
    console.error('获取可用时间槽失败:', error)
    ElMessage.error('获取可用时间槽失败')
  }
}

const handleSelectSlot = (slot) => {
  if (slot.isBooked) return
  selectedSlot.value = slot.timeSlot
  selectedStartTime.value = slot.startTime
  selectedEndTime.value = slot.endTime
}

const handleConfirmBook = async () => {
  if (!selectedSlot.value || !currentOrder.value) {
    ElMessage.warning('请选择时间段')
    return
  }

  try {
    // 将ISO时间格式转换为yyyy-MM-dd HH:mm:ss格式
    const date = new Date(selectedStartTime.value)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    const formattedTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`

    await bookCourse({
      orderId: currentOrder.value.id,
      scheduleTime: formattedTime
    })
    ElMessage.success('预约成功')
    bookingDialogVisible.value = false
    fetchOrderList()
  } catch (error) {
    console.error('预约失败:', error)
    ElMessage.error(error.response?.data?.msg || error.message || '预约失败')
  }
}

onMounted(() => {
  fetchOrderList()
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
</style>