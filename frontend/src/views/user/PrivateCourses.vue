<template>
  <div class="private-courses-page">
    <div class="page-header">
      <h1>私教课程</h1>
      <p>选择适合您的专业私教课程，开启健身之旅</p>
    </div>
    
    <div class="courses-container">
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
      
      <div v-else class="courses-grid">
        <div 
          v-for="course in courses" 
          :key="course.id" 
          class="course-card"
          @click="viewCourseDetail(course.id)"
        >
          <div class="course-image">
            <el-image
              :src="course.coverImg"
              fit="cover"
              style="width: 100%; height: 200px"
              :preview-src-list="[course.coverImg]"
            >
              <template #error>
                <div class="image-error">
                  <el-icon :size="40"><Picture /></el-icon>
                  <span>暂无图片</span>
                </div>
              </template>
            </el-image>
          </div>
          
          <div class="course-info">
            <h3 class="course-name">{{ course.name }}</h3>
            
            <div class="course-meta">
              <el-tag :type="course.status === 1 ? 'success' : 'info'" size="small">
                {{ course.status === 1 ? '上架' : '下架' }}
              </el-tag>
              <span class="course-price">¥{{ course.price }}</span>
            </div>
            
            <div class="course-details">
              <div class="detail-item">
                <el-icon><Clock /></el-icon>
                <span>{{ course.duration }}分钟/次</span>
              </div>
              <div class="detail-item">
                <el-icon><Collection /></el-icon>
                <span>{{ course.courseTimes }}次服务</span>
              </div>
            </div>
            
            <div class="course-coach">
              <el-avatar :size="30" :src="getCoachAvatar(course.coachUserId)" />
              <span class="coach-name">{{ getCoachName(course.coachUserId) }}</span>
            </div>
            
            <div class="course-actions">
              <el-button v-if="canPurchase" type="primary" size="small" @click.stop="purchaseCourse(course)">
                立即购买
              </el-button>
              <el-button size="small" @click.stop="viewCourseDetail(course.id)">
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="!loading && courses.length === 0" class="empty-container">
        <el-icon :size="60" color="#ccc"><Box /></el-icon>
        <p>暂无私教课程</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCourseList } from '@/api/course'
import { getCoachProfileList } from '@/api/coachProfile'
import { ElMessage } from 'element-plus'
import { Loading, Picture, Clock, Collection, Box } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const courses = ref([])
const coaches = ref([])

const roles = computed(() => userStore.roles)

const canPurchase = computed(() => {
  return roles.value.includes('user') || roles.value.includes('vip')
})

const fetchCourses = async () => {
  loading.value = true
  try {
    // 获取私教课程（type=1）
    const res = await getCourseList({ 
      type: 1, 
      status: 1, // 只显示上架的课程
      size: 100 
    })
    courses.value = res.data?.records || []
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCoaches = async () => {
  try {
    const res = await getCoachProfileList({ size: 100 })
    coaches.value = res.data?.records || []
  } catch (error) {
    console.error('获取教练列表失败:', error)
  }
}

const getCoachName = (coachUserId) => {
  const coach = coaches.value.find(c => c.userId === coachUserId)
  return coach?.realName || '未知教练'
}

const getCoachAvatar = (coachUserId) => {
  const coach = coaches.value.find(c => c.userId === coachUserId)
  return coach?.images?.[0] || ''
}

const viewCourseDetail = (courseId) => {
  router.push(`/user/private-courses/${courseId}`)
}

const purchaseCourse = (course) => {
  if (course.status !== 1) {
    ElMessage.warning('该课程已下架，无法购买')
    return
  }
  
  // 跳转到购买页面
  router.push({
    path: '/user/course-purchase',
    query: { courseId: course.id }
  })
}

onMounted(() => {
  fetchCoaches()
  fetchCourses()
})
</script>

<style scoped>
.private-courses-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 36px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.page-header p {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.courses-container {
  max-width: 1200px;
  margin: 0 auto;
}

.loading-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #666;
}

.loading-container p,
.empty-container p {
  margin-top: 20px;
  font-size: 16px;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
}

.course-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.course-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f7fa;
  color: #999;
}

.course-info {
  padding: 20px;
}

.course-name {
  font-size: 18px;
  color: #333;
  margin: 0 0 15px 0;
  font-weight: 600;
  line-height: 1.4;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.course-price {
  font-size: 20px;
  color: #e74c3c;
  font-weight: 700;
}

.course-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.course-coach {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
}

.coach-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.course-actions {
  display: flex;
  gap: 10px;
}

.course-actions .el-button {
  flex: 1;
}

@media (max-width: 768px) {
  .private-courses-page {
    padding: 10px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .courses-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .course-info {
    padding: 15px;
  }
}
</style>