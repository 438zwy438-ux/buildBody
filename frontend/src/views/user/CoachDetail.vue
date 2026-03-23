<template>
  <div class="coach-detail-page">
    <el-button @click="goBack" class="back-button">
      <el-icon><ArrowLeft /></el-icon>
      返回
    </el-button>
    
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <p>加载中...</p>
    </div>
    
    <div v-else-if="coach" class="detail-container">
      <div class="image-section">
        <el-carousel 
          :interval="0" 
          height="500px" 
          arrow="hover" 
          indicator-position="outside"
          :autoplay="false"
        >
          <el-carousel-item v-for="(image, index) in images" :key="index">
            <div class="detail-image" :style="{ backgroundImage: `url(${image})` }"></div>
          </el-carousel-item>
          <el-carousel-item v-if="images.length === 0">
            <div class="detail-image no-image">
              <el-icon :size="100"><User /></el-icon>
              <p>暂无照片</p>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      
      <div class="info-section">
        <h1 class="coach-name">{{ coach.realName }}</h1>
        
        <div class="coach-meta">
          <el-tag :type="coach.status === 1 ? 'success' : 'info'" size="large">
            {{ coach.status === 1 ? '在职' : '离职' }}
          </el-tag>
          <span class="entry-date">入职日期: {{ formatDate(coach.entryDate) }}</span>
        </div>
        
        <div class="coach-tags">
          <h3>专业特长</h3>
          <div class="tags-container">
            <el-tag 
              v-for="(tag, index) in specialtyTags" 
              :key="index" 
              type="primary"
              size="large"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
        
        <div class="coach-intro">
          <h2>个人简介</h2>
          <div class="intro-content" v-html="coach.intro || '暂无简介'"></div>
        </div>
        
        <div class="coach-certificates" v-if="coach.certificates">
          <h2>资格证书</h2>
          <div class="certificates-grid">
            <div 
              v-for="(cert, index) in certificateList" 
              :key="index" 
              class="certificate-item"
            >
              <el-image 
                :src="cert" 
                fit="cover"
                style="width: 200px; height: 280px;"
                :preview-src-list="certificateList"
                :initial-index="index"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="error-container">
      <el-icon :size="60" color="#f56c6c"><Warning /></el-icon>
      <p>教练信息加载失败</p>
      <el-button type="primary" @click="goBack">返回</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCoachDetail } from '@/api/coach'
import { ArrowLeft, Loading, Warning, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const coach = ref(null)
const images = ref([])
const loading = ref(true)

const specialtyTags = computed(() => {
  if (!coach.value?.specialty) return []
  return coach.value.specialty.split(',').filter(tag => tag.trim())
})

const certificateList = computed(() => {
  if (!coach.value?.certificates) return []
  try {
    return JSON.parse(coach.value.certificates)
  } catch {
    return []
  }
})

const fetchCoachDetail = async () => {
  try {
    loading.value = true
    const coachId = route.params.id
    const res = await getCoachDetail(coachId)
    
    if (res.data) {
      coach.value = res.data.coach
      images.value = res.data.images || []
    }
  } catch (error) {
    console.error('获取教练详情失败:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleDateString('zh-CN')
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchCoachDetail()
})
</script>

<style scoped>
.coach-detail-page {
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
  max-width: 1400px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.image-section {
  width: 100%;
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

.coach-name {
  font-size: 36px;
  color: #333;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.coach-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.entry-date {
  font-size: 16px;
  color: #666;
}

.coach-tags {
  margin-bottom: 30px;
}

.coach-tags h3 {
  font-size: 24px;
  color: #333;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.coach-intro {
  margin-bottom: 30px;
}

.coach-intro h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.intro-content {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
}

.coach-certificates {
  margin-bottom: 30px;
}

.coach-certificates h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.certificates-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.certificate-item {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

:deep(.el-carousel__arrow) {
  background: rgba(255, 255, 255, 0.8);
  color: #333;
}

:deep(.el-carousel__arrow:hover) {
  background: white;
  color: #667eea;
}

:deep(.el-carousel__indicator) {
  background: rgba(0, 0, 0, 0.3);
}

:deep(.el-carousel__indicator.is-active) {
  background: #667eea;
}

@media (max-width: 768px) {
  .coach-detail-page {
    padding: 10px;
  }
  
  .info-section {
    padding: 20px;
  }
  
  .coach-name {
    font-size: 24px;
  }
  
  .coach-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .certificates-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>