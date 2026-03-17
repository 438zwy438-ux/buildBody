<template>
  <div class="home-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <h1>欢迎来到健身俱乐部</h1>
            <p>专业健身服务，助您达成健身目标</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409eff"><Calendar /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ memberCardInfo.cardName || '暂无会员卡' }}</div>
              <div class="stat-label">当前会员卡</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67c23a"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ entryCount }}次</div>
              <div class="stat-label">本月入场</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#e6a23c"><Coin /></el-icon>
            <div class="stat-info">
              <div class="stat-value">¥{{ memberCardInfo.balance || 0 }}</div>
              <div class="stat-label">卡内余额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门课程</span>
              <el-link type="primary" @click="$router.push('/user/course-booking')">查看更多</el-link>
            </div>
          </template>
          <el-table :data="hotCourses" style="width: 100%">
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="coachName" label="教练" />
            <el-table-column prop="price" label="价格" />
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleBook(row)">预约</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近入场记录</span>
              <el-link type="primary" @click="$router.push('/user/entry-records')">查看更多</el-link>
            </div>
          </template>
          <el-table :data="recentEntries" style="width: 100%">
            <el-table-column prop="entryTime" label="入场时间" />
            <el-table-column prop="exitTime" label="出场时间" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === '在场' ? 'success' : 'info'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" size="large" @click="$router.push('/user/course-booking')">预约课程</el-button>
            <el-button type="success" size="large" @click="$router.push('/user/member-card')">查看会员卡</el-button>
            <el-button type="warning" size="large" @click="$router.push('/user/orders')">我的订单</el-button>
            <el-button type="danger" size="large" @click="$router.push('/user/profile')">个人中心</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getMemberCardList } from '@/api/memberCard'
import { getEntryLogList } from '@/api/entryLog'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

const memberCardInfo = ref({})
const entryCount = ref(0)
const hotCourses = ref([
  { id: 1, courseName: '瑜伽基础', coachName: '张教练', price: 199 },
  { id: 2, courseName: '力量训练', coachName: '李教练', price: 299 },
  { id: 3, courseName: '有氧操', coachName: '王教练', price: 159 }
])
const recentEntries = ref([
  { entryTime: '2024-03-17 09:30', exitTime: '2024-03-17 11:30', status: '已离场' },
  { entryTime: '2024-03-16 14:00', exitTime: '2024-03-16 16:00', status: '已离场' },
  { entryTime: '2024-03-15 10:00', exitTime: null, status: '在场' }
])

const fetchMemberCard = async () => {
  try {
    const res = await getMemberCardList({
      current: 1,
      size: 1,
      userId: userInfo.value.id
    })
    if (res.data.records && res.data.records.length > 0) {
      memberCardInfo.value = res.data.records[0]
    }
  } catch (error) {
    console.error('获取会员卡信息失败:', error)
  }
}

const fetchEntryRecords = async () => {
  try {
    const res = await getEntryLogList({
      current: 1,
      size: 10,
      userId: userInfo.value.id
    })
    entryCount.value = res.data.total
    if (res.data.records) {
      recentEntries.value = res.data.records.slice(0, 3)
    }
  } catch (error) {
    console.error('获取入场记录失败:', error)
  }
}

const handleBook = (course) => {
  console.log('预约课程:', course)
}

onMounted(() => {
  fetchMemberCard()
  fetchEntryRecords()
})
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
}

.welcome-content {
  text-align: center;
  padding: 40px 20px;
}

.welcome-content h1 {
  margin: 0 0 10px 0;
  font-size: 36px;
}

.welcome-content p {
  margin: 0;
  font-size: 18px;
  opacity: 0.9;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  font-size: 48px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.quick-actions .el-button {
  height: 80px;
  font-size: 18px;
}
</style>