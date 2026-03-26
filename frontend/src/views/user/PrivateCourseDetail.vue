<template>
  <div class="private-course-detail-page">
    <el-button @click="goBack" class="back-button">
      <el-icon><ArrowLeft /></el-icon>
      返回
    </el-button>
    
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <p>加载中...</p>
    </div>
    
    <div v-else-if="course" class="detail-container">
      <div class="image-section">
        <div class="detail-image" :style="{ backgroundImage: `url(${course.coverImg})` }">
          <div v-if="!course.coverImg" class="no-image">
            <el-icon :size="100"><Picture /></el-icon>
            <p>暂无图片</p>
          </div>
        </div>
      </div>
      
      <div class="info-section">
        <h1 class="course-name">{{ course.name }}</h1>
        
        <div class="course-meta">
          <el-tag :type="course.status === 1 ? 'success' : 'info'" size="large">
            {{ course.status === 1 ? '上架' : '下架' }}
          </el-tag>
          <span class="course-price">¥{{ course.price }}</span>
        </div>
        
        <div class="course-info-grid">
          <div class="info-item">
            <span class="label">单次时长</span>
            <span class="value">{{ course.duration }}分钟</span>
          </div>
          <div class="info-item">
            <span class="label">服务次数</span>
            <span class="value">{{ course.courseTimes }}次</span>
          </div>
          <div class="info-item">
            <span class="label">课程类型</span>
            <span class="value">{{ course.type === 1 ? '私教' : '团课' }}</span>
          </div>
          <div class="info-item">
            <span class="label">总时长</span>
            <span class="value">{{ course.duration * course.courseTimes }}分钟</span>
          </div>
        </div>
        
        <div class="coach-section">
          <h2>授课教练</h2>
          <div class="coach-info">
            <el-avatar :size="80" :src="coachAvatar" />
            <div class="coach-details">
              <h3>{{ coachName }}</h3>
              <p class="coach-specialty">{{ coachSpecialty }}</p>
              <p class="coach-intro">{{ coachIntro }}</p>
            </div>
          </div>
        </div>
        
        <div class="description-section">
          <h2>课程描述</h2>
          <p class="course-description">{{ course.description || '暂无描述' }}</p>
        </div>
        
        <div class="action-section">
          <el-button 
            type="primary" 
            size="large" 
            :disabled="course.status !== 1"
            @click="purchaseCourse"
            class="purchase-btn"
          >
            <el-icon><ShoppingCart /></el-icon>
            立即购买
          </el-button>
          
          <div v-if="course.status !== 1" class="status-tip">
            <el-alert type="warning" :closable="false" show-icon>
              该课程已下架，暂时无法购买
            </el-alert>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="error-container">
      <el-icon :size="60" color="#f56c6c"><Warning /></el-icon>
      <p>课程信息加载失败</p>
      <el-button type="primary" @click="goBack">返回</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourseList } from '@/api/course'
import { getCoachProfileList } from '@/api/coachProfile'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Loading, Warning, Picture, ShoppingCart } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const course = ref(null)
const coaches = ref([])
const loading = ref(true)

const coachInfo = computed(() => {
  if (!course.value || !coaches.value.length) return null
  return coaches.value.find(c => c.userId === course.value.coachUserId)
})

const coachName = computed(() => {
  return coachInfo.value?.realName || '未知教练'
})

const coachAvatar = computed(() => {
  return coachInfo.value?.images?.[0] || ''
})

const coachSpecialty = computed(() => {
  return coachInfo.value?.specialty || '暂无专长信息'
})

const coachIntro = computed(() => {
  return coachInfo.value?.intro || '暂无简介'
})

const fetchCourseDetail = async () => {
  try {
    loading.value = true
    const courseId = route.params.id
    
    // 获取课程详情
    const res = await getCourseList({ size: 100 })
    const courses = res.data?.records || []
    course.value = courses.find(c => c.id == courseId)
    
    if (!course.value) {
      ElMessage.error('课程不存在')
      return
    }
  } catch (error) {
    console.error('获取课程详情失败:', error)
    ElMessage.error('获取课程详情失败')
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

const purchaseCourse = () => {
  if (course.value.status !== 1) {
    ElMessage.warning('该课程已下架，无法购买')
    return
  }
  
  // 跳转到购买页面
  router.push({
    path: '/user/course-purchase',
    query: { courseId: course.value.id }
  })
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchCoaches()
  fetchCourseDetail()
})
</script>

<style scoped>
.private-course-detail-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  padding: 20px;
}

.back-button {
  margin-bottom: 20px;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #666;
}

.loading-container p,
.error-container p {
  margin-top: 20px;
  font-size: 16px;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.image-section {
  width: 100%;
  height: 400px;
}

.detail-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.detail-image.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #999;
}

.detail-image.no-image .el-icon {
  margin-bottom: 20px;
  color: #ccc;
}

.detail-image.no-image p {
  font-size: 18px;
  margin: 0;
}

.info-section {
  padding: 40px;
}

.course-name {
  font-size: 36px;
  color: #333;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.course-price {
  font-size: 32px;
  color: #e74c3c;
  font-weight: 700;
}

.course-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.info-item {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
}

.info-item .label {
  display: block;
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.info-item .value {
  display: block;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.coach-section {
  margin-bottom: 30px;
}

.coach-section h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 20px 0;
  font-weight: 600;
}

.coach-info {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
}

.coach-details h3 {
  font-size: 20px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.coach-specialty {
  font-size: 16px;
  color: #666;
  margin: 0 0 10px 0;
}

.coach-intro {
  font-size: 14px;
  color: #888;
  line-height: 1.6;
  margin: 0;
}

.description-section {
  margin-bottom: 30px;
}

.description-section h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.course-description {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin: 0;
}

.action-section {
  text-align: center;
}

.purchase-btn {
  width: 200px;
  height: 50px;
  font-size: 18px;
}

.status-tip {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .private-course-detail-page {
    padding: 10px;
  }
  
  .info-section {
    padding: 20px;
  }
  
  .course-name {
    font-size: 24px;
  }
  
  .course-price {
    font-size: 24px;
  }
  
  .course-info-grid {
    grid-template-columns: 1fr;
  }
  
  .coach-info {
    flex-direction: column;
    text-align: center;
  }
  
  .purchase-btn {
    width: 100%;
  }
}
</style>