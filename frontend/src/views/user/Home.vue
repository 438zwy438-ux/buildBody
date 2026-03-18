<template>
  <div class="home-page">
    <div class="banner-section">
      <el-carousel :interval="4000" height="400px" arrow="always" indicator-position="outside">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${banner.imageUrl})` }">
            <div class="banner-overlay">
              <h1>{{ banner.title }}</h1>
              <p>{{ banner.description }}</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="equipment-section">
      <div class="section-header">
        <h2>健身器材</h2>
        <p>专业设备，助力您的健身之旅</p>
      </div>
      
      <div class="equipment-grid">
        <div 
          v-for="equipment in equipmentList" 
          :key="equipment.id" 
          class="equipment-card"
          @click="goToDetail(equipment.id)"
        >
          <div class="equipment-images">
            <el-carousel 
              :interval="0" 
              height="200px" 
              arrow="hover" 
              indicator-position="none"
              :autoplay="false"
            >
              <el-carousel-item v-for="(image, index) in equipment.images" :key="index">
                <div class="equipment-image" :style="{ backgroundImage: `url(${image})` }"></div>
              </el-carousel-item>
              <el-carousel-item v-if="equipment.images.length === 0">
                <div class="equipment-image no-image">
                  <el-icon :size="60"><Picture /></el-icon>
                  <p>暂无图片</p>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
          
          <div class="equipment-info">
            <h3>{{ equipment.name }}</h3>
            <p class="equipment-code">编号: {{ equipment.code }}</p>
            <p class="equipment-location">位置: {{ equipment.location }}</p>
            <el-tag :type="equipment.status === 1 ? 'success' : 'danger'" size="small">
              {{ equipment.status === 1 ? '正常' : '维护中' }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <div class="features-section">
      <div class="feature-item">
        <el-icon :size="40" color="#667eea"><Trophy /></el-icon>
        <h3>专业教练</h3>
        <p>资深教练团队，为您提供专业指导</p>
      </div>
      <div class="feature-item">
        <el-icon :size="40" color="#667eea"><Star /></el-icon>
        <h3>优质服务</h3>
        <p>贴心服务，让您享受健身乐趣</p>
      </div>
      <div class="feature-item">
        <el-icon :size="40" color="#667eea"><Clock /></el-icon>
        <h3>灵活时间</h3>
        <p>24小时营业，随时随地健身</p>
      </div>
      <div class="feature-item">
        <el-icon :size="40" color="#667eea"><Medal /></el-icon>
        <h3>先进设备</h3>
        <p>国际一流品牌，保障训练效果</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBannerList } from '@/api/banner'
import { getEquipmentList } from '@/api/equipment'
import { Trophy, Star, Clock, Medal, Picture } from '@element-plus/icons-vue'

const router = useRouter()
const banners = ref([])
const equipmentList = ref([])

const fetchBanners = async () => {
  try {
    const res = await getBannerList({
      current: 1,
      size: 10,
      status: 1
    })
    if (res.data.records) {
      banners.value = res.data.records.map(banner => ({
        ...banner,
        imageUrl: banner.imageUrl || 'https://via.placeholder.com/1200x400?text=Banner'
      }))
    }
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

const fetchEquipment = async () => {
  try {
    const res = await getEquipmentList({
      current: 1,
      size: 20,
      status: 1
    })
    if (res.data.records) {
      equipmentList.value = res.data.records.map(equipment => ({
        ...equipment,
        images: equipment.images || []
      }))
    }
  } catch (error) {
    console.error('获取器材列表失败:', error)
  }
}

const goToDetail = (equipmentId) => {
  router.push(`/user/equipment/${equipmentId}`)
}

onMounted(() => {
  fetchBanners()
  fetchEquipment()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}

.banner-section {
  width: 100%;
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-overlay {
  background: rgba(0, 0, 0, 0.4);
  padding: 40px 60px;
  border-radius: 12px;
  text-align: center;
  color: white;
  backdrop-filter: blur(10px);
}

.banner-overlay h1 {
  font-size: 48px;
  margin: 0 0 16px 0;
  font-weight: 700;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.banner-overlay p {
  font-size: 20px;
  margin: 0;
  opacity: 0.95;
}

.equipment-section {
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

.equipment-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.equipment-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.equipment-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.equipment-images {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.equipment-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: transform 0.3s ease;
}

.equipment-image.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #999;
}

.equipment-image.no-image .el-icon {
  margin-bottom: 10px;
  color: #ccc;
}

.equipment-image.no-image p {
  font-size: 14px;
  margin: 0;
}

.equipment-card:hover .equipment-image {
  transform: scale(1.05);
}

.equipment-info {
  padding: 20px;
}

.equipment-info h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 12px 0;
  font-weight: 600;
}

.equipment-code,
.equipment-location {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.equipment-info .el-tag {
  margin-top: 8px;
}

.features-section {
  max-width: 1400px;
  margin: 60px auto 40px;
  padding: 60px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
}

.feature-item {
  text-align: center;
  padding: 20px;
}

.feature-item h3 {
  font-size: 20px;
  color: #333;
  margin: 16px 0 8px 0;
  font-weight: 600;
}

.feature-item p {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.6;
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
  background: rgba(255, 255, 255, 0.5);
}

:deep(.el-carousel__indicator.is-active) {
  background: #667eea;
}

@media (max-width: 1200px) {
  .equipment-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .features-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .equipment-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .features-section {
    grid-template-columns: 1fr;
  }
  
  .banner-overlay h1 {
    font-size: 32px;
  }
  
  .banner-overlay p {
    font-size: 16px;
  }
}
</style>