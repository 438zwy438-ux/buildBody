<template>
  <div class="coach-page">
    <div class="coach-section">
      <div class="section-header">
        <h2>专业教练</h2>
        <p>资深教练团队，为您提供专业指导</p>
      </div>
      
      <div class="coach-grid">
        <div 
          v-for="coach in coachList" 
          :key="coach.id" 
          class="coach-card"
          @click="goToDetail(coach.id)"
        >
          <div class="coach-images">
            <el-carousel 
              :interval="0" 
              height="300px" 
              arrow="hover" 
              indicator-position="none"
              :autoplay="false"
            >
              <el-carousel-item v-for="(image, index) in coach.images" :key="index">
                <div class="coach-image" :style="{ backgroundImage: `url(${image})` }"></div>
              </el-carousel-item>
              <el-carousel-item v-if="coach.images.length === 0">
                <div class="coach-image no-image">
                  <el-icon :size="80"><User /></el-icon>
                  <p>暂无照片</p>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
          
          <div class="coach-info">
            <h3>{{ coach.realName }}</h3>
            <div class="coach-tags">
              <el-tag 
                v-for="(tag, index) in specialtyTags(coach.specialty)" 
                :key="index" 
                type="primary"
                size="small"
              >
                {{ tag }}
              </el-tag>
            </div>
            <p class="coach-intro">{{ coach.intro || '暂无简介' }}</p>
            <div class="coach-meta">
              <span class="entry-date">入职日期: {{ formatDate(coach.entryDate) }}</span>
              <el-tag :type="coach.status === 1 ? 'success' : 'info'" size="small">
                {{ coach.status === 1 ? '在职' : '离职' }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCoachList } from '@/api/coach'
import { User } from '@element-plus/icons-vue'

const router = useRouter()
const coachList = ref([])

const fetchCoachList = async () => {
  try {
    const res = await getCoachList({
      current: 1,
      size: 20,
      status: 1
    })
    if (res.data.records) {
      coachList.value = res.data.records
    }
  } catch (error) {
    console.error('获取教练列表失败:', error)
  }
}

const specialtyTags = (specialty) => {
  if (!specialty) return []
  return specialty.split(',').filter(tag => tag.trim())
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleDateString('zh-CN')
}

const goToDetail = (coachId) => {
  router.push(`/user/coach/${coachId}`)
}

onMounted(() => {
  fetchCoachList()
})
</script>

<style scoped>
.coach-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}

.coach-section {
  max-width: 1400px;
  margin: 40px auto;
  padding: 0 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-header h2 {
  font-size: 36px;
  color: #333;
  margin: 0 0 12px 0;
  font-weight: 700;
}

.section-header p {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.coach-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.coach-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.coach-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.coach-images {
  width: 100%;
  height: 300px;
  overflow: hidden;
}

.coach-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: transform 0.3s ease;
}

.coach-card:hover .coach-image {
  transform: scale(1.05);
}

.coach-image.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #999;
}

.coach-image.no-image .el-icon {
  margin-bottom: 15px;
  color: #ccc;
}

.coach-image.no-image p {
  font-size: 16px;
  margin: 0;
}

.coach-info {
  padding: 24px;
}

.coach-info h3 {
  font-size: 24px;
  color: #333;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.coach-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.coach-intro {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.coach-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.entry-date {
  font-size: 14px;
  color: #999;
}

:deep(.el-carousel__arrow) {
  background: rgba(255, 255, 255, 0.8);
  color: #333;
}

:deep(.el-carousel__arrow:hover) {
  background: white;
  color: #667eea;
}

@media (max-width: 1200px) {
  .coach-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .coach-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .coach-section {
    padding: 0 10px;
  }
  
  .section-header h2 {
    font-size: 28px;
  }
}
</style>