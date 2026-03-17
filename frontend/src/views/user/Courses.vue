<template>
  <div class="courses-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我的私教课" name="my-courses">
          <el-table :data="myCourses" v-loading="loading" border>
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="coachName" label="教练" />
            <el-table-column prop="price" label="单价" />
            <el-table-column prop="remainCount" label="剩余次数" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleBook(row)">预约</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="我的订单" name="my-orders">
          <el-table :data="myOrders" v-loading="loading" border>
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="amount" label="金额" />
            <el-table-column prop="totalCount" label="购买次数" />
            <el-table-column prop="remainCount" label="剩余次数" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已支付' : '待支付' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getMyPrivateCourses, getMyPrivateOrders } from '@/api/course'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const loading = ref(false)
const activeTab = ref('my-courses')
const myCourses = ref([])
const myOrders = ref([])

const fetchMyCourses = async () => {
  loading.value = true
  try {
    const res = await getMyPrivateCourses(userInfo.value.id)
    myCourses.value = res.data
  } catch (error) {
    console.error('获取我的课程失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchMyOrders = async () => {
  loading.value = true
  try {
    const res = await getMyPrivateOrders(userInfo.value.id)
    myOrders.value = res.data
  } catch (error) {
    console.error('获取我的订单失败:', error)
  } finally {
    loading.value = false
  }
}

const handleBook = (course) => {
  console.log('预约课程:', course)
}

onMounted(() => {
  fetchMyCourses()
  fetchMyOrders()
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