<template>
  <div class="home-page">
    <div class="banner-section">
      <el-carousel :interval="4000" height="700px" arrow="always" indicator-position="outside">
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

    <div class="gym-info-section">
      <div class="section-header">
        <h2>健身房介绍</h2>
        <p>专业健身环境，为您提供优质服务</p>
      </div>
      
      <div class="gym-info-content">
        <div class="gym-info-left">
          <div class="gym-intro">
            <h3>关于我们</h3>
            <p>{{ gymInfo.introduction }}</p>
          </div>
          
          <div class="gym-details">
            <div class="detail-item">
              <el-icon :size="24" color="#667eea"><Location /></el-icon>
              <div class="detail-content">
                <span class="detail-label">地址</span>
                <span class="detail-value">{{ gymInfo.address }}</span>
              </div>
            </div>
            
            <div class="detail-item">
              <el-icon :size="24" color="#667eea"><Phone /></el-icon>
              <div class="detail-content">
                <span class="detail-label">联系电话</span>
                <span class="detail-value">{{ gymInfo.phone }}</span>
              </div>
            </div>
            
            <div class="detail-item">
              <el-icon :size="24" color="#667eea"><Grid /></el-icon>
              <div class="detail-content">
                <span class="detail-label">占地面积</span>
                <span class="detail-value">{{ gymInfo.area }}</span>
              </div>
            </div>
            
            <div class="detail-item">
              <el-icon :size="24" color="#667eea"><Clock /></el-icon>
              <div class="detail-content">
                <span class="detail-label">经营时间</span>
                <span class="detail-value">{{ gymInfo.businessHours }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="gym-info-right">
          <div class="gym-features">
            <h3>我们的特色</h3>
            <ul>
              <li v-for="(feature, index) in gymInfo.features" :key="index">
                <el-icon color="#667eea"><Check /></el-icon>
                <span>{{ feature }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- 健身器材部分已移至独立页面 -->
    <!-- <div class="equipment-section">
      <div class="section-header">
        <h2>健身器材</h2>
        <p>专业设备，助力您的健身之旅</p>
      </div>
      
      <div class="equipment-grid">
        <div 
          v-for="equipment in equipmentList.slice(0, 4)" 
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
            <div class="equipment-header">
              <h3>{{ equipment.name }}</h3>
              <el-tag type="info" size="small" class="count-tag">数量: {{ equipment.count }}</el-tag>
            </div>
            <p class="equipment-code">编号: {{ equipment.code }}</p>
            <p class="equipment-location">位置: {{ equipment.location }}</p>
            <el-tag :type="equipment.status === 1 ? 'success' : 'danger'" size="small">
              {{ equipment.status === 1 ? '正常' : '维护中' }}
            </el-tag>
          </div>
        </div>
      </div>
      
      <div class="view-all-button">
        <el-button type="primary" size="large" @click="goToEquipmentList">
          查看所有器材
          <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-button>
      </div>
    </div> -->

    <!-- 专业教练部分已移至独立页面 -->
    <!-- <div class="coach-section">
      <div class="section-header">
        <h2>专业教练</h2>
        <p>资深教练团队，为您提供专业指导</p>
      </div>
      
      <div class="coach-grid">
        <div 
          v-for="coach in coachList" 
          :key="coach.id" 
          class="coach-card"
          @click="goToCoachDetail(coach.id)"
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
          </div>
        </div>
      </div>
      
      <div class="view-all-button">
        <el-button type="primary" size="large" @click="goToCoachList">
          查看所有教练
          <el-icon class="el-icon--right"><ArrowRight /></el-icon>
        </el-button>
      </div>
    </div> -->

    <div class="features-section">
      <div class="feature-item" @click="goToEquipmentList" style="cursor: pointer">
        <el-icon :size="40" color="#667eea"><Grid /></el-icon>
        <h3>健身器材</h3>
        <p>专业设备，助力您的健身之旅</p>
      </div>
      <div class="feature-item" @click="goToCoachList" style="cursor: pointer">
        <el-icon :size="40" color="#667eea"><Trophy /></el-icon>
        <h3>专业教练</h3>
        <p>资深教练团队，为您提供专业指导</p>
      </div>
      <div class="feature-item" @click="goToCardShop" style="cursor: pointer">
        <el-icon :size="40" color="#667eea"><CreditCard /></el-icon>
        <h3>会员卡</h3>
        <p>多种会员卡，满足不同需求</p>
      </div>
      <div class="feature-item">
        <el-icon :size="40" color="#667eea"><Star /></el-icon>
        <h3>优质服务</h3>
        <p>贴心服务，让您享受健身乐趣</p>
      </div>
    </div>

    <div class="card-promo-section">
      <div class="promo-content">
        <div class="promo-left">
          <h2>会员卡中心</h2>
          <p>选择适合您的会员卡，开启健身之旅</p>
          <el-button type="primary" size="large" @click="goToCardShop">
            立即查看
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
        <div class="promo-right">
          <el-icon :size="120" color="#667eea"><CreditCard /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBannerList } from '@/api/banner'
import { getEquipmentList } from '@/api/equipment'
import { getCoachList } from '@/api/coach'
import { Trophy, Star, Clock, Medal, Picture, User, ArrowRight, CreditCard, Location, Phone, Grid, Check } from '@element-plus/icons-vue'

const router = useRouter()
const banners = ref([])
const equipmentList = ref([])
const coachList = ref([])
const gymInfo = ref({
  introduction: '我们是一家专业的高端智能健身系统，致力于为每一位会员提供最优质的健身体验。拥有国际一流的健身器材、专业的教练团队和舒适的运动环境，帮助您实现健康目标。',
  address: '北京市朝阳区建国路88号SOHO现代城A座3层',
  phone: '010-88888888',
  area: '2000平方米',
  businessHours: '周一至周日 06:00-23:00',
  features: [
    '国际一流品牌健身器材',
    '国家级认证专业教练团队',
    '24小时智能健身区域',
    '专业私教一对一指导',
    '多样化团课课程',
    '舒适更衣室及淋浴设施',
    '营养餐吧服务',
    '会员专属休息区'
  ]
})

const fetchBanners = async () => {
    try {
    const res = await getBannerList({
      current:1,
      size: 10,
      status: 1
    })
    if (res.data.records) {
      banners.value = res.data.records.map(banner => ({
        ...banner,
        imageUrl: banner.imgUrl || 'https://via.placeholder.com/1200x400?text=Banner'
      }))
    }
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

const fetchEquipment = async () => {
  try {
    const res = await getEquipmentList({
      current:1,
      size: 100,
      status: 1
    })
    if (res.data.records) {
      const allEquipment = res.data.records.map(equipment => ({
        ...equipment,
        images: equipment.images || []
      }))
      
      // 按器材名称分组，每种只展示一个，并统计数量
      const equipmentMap = new Map()
      allEquipment.forEach(equipment => {
        const name = equipment.name
        if (!equipmentMap.has(name)) {
          equipmentMap.set(name, {
            ...equipment,
            count: 1
          })
        } else {
          equipmentMap.get(name).count++
        }
      })
      
      equipmentList.value = Array.from(equipmentMap.values())
    }
  } catch (error) {
    console.error('获取器材列表失败:', error)
  }
}

const fetchCoach = async () => {
  try {
    const res = await getCoachList({
      current:1,
      size: 4,
      status: 1
    })
    if (res.data.records) {
      coachList.value = res.data.records
    }
  } catch (error) {
    console.error('获取教练列表失败:', error)
  }
}

const goToDetail = (equipmentId) => {
  router.push(`/user/equipment/${equipmentId}`)
}

const goToEquipmentList = () => {
  router.push('/user/equipment')
}

const goToCoachDetail = (coachId) => {
  router.push(`/user/coach/${coachId}`)
}

const goToCoachList = () => {
  router.push('/user/coach')
}

const goToCardShop = () => {
  router.push('/user/card-shop')
}

const specialtyTags = (specialty) => {
  if (!specialty) return []
  return specialty.split(',').filter(tag => tag.trim())
}

onMounted(() => {
  fetchBanners()
  fetchEquipment()
  fetchCoach()
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
  background: transparent;
  padding: 40px 60px;
  text-align: center;
  color: white;
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

.gym-info-section {
  max-width: 1400px;
  margin: 40px auto;
  padding: 0 20px;
}

.gym-info-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.gym-info-left {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.gym-intro h3 {
  font-size: 24px;
  color: #333;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.gym-intro p {
  font-size: 15px;
  color: #666;
  line-height: 1.8;
  margin: 0;
}

.gym-details {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.detail-item:hover {
  background: #f0f2f5;
  transform: translateX(4px);
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

.detail-value {
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.gym-info-right {
  display: flex;
  flex-direction: column;
}

.gym-features h3 {
  font-size: 24px;
  color: #333;
  margin: 0 0 24px 0;
  font-weight: 600;
}

.gym-features ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.gym-features li {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 10px;
  font-size: 15px;
  color: #333;
  transition: all 0.3s ease;
}

.gym-features li:hover {
  background: #e8f0fe;
  transform: translateX(4px);
}

.private-courses-section {
  max-width: 1400px;
  margin: 60px auto;
  padding: 0 20px;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 30px;
}

.course-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.course-image {
  width: 100%;
  height: 180px;
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
  margin: 0 0 12px 0;
  font-weight: 600;
  line-height: 1.4;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.course-price {
  font-size: 20px;
  color: #e74c3c;
  font-weight: 700;
}

.course-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.course-coach {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
}

.coach-name {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}

.loading-container,
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: #666;
}

.loading-container p,
.empty-container p {
  margin-top: 16px;
  font-size: 16px;
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

.equipment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.equipment-header h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
  font-weight: 600;
  flex: 1;
}

.count-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
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

.coach-section {
  max-width: 1400px;
  margin: 60px auto;
  padding: 0 20px;
}

.coach-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 30px;
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
  font-size: 20px;
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
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.view-all-button {
  text-align: center;
  margin-top: 30px;
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

.card-promo-section {
  max-width: 1400px;
  margin: 40px auto 60px;
  padding: 0 20px;
}

.promo-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.3);
}

.promo-left {
  flex: 1;
}

.promo-left h2 {
  font-size: 42px;
  color: white;
  margin: 0 0 16px 0;
  font-weight: 700;
}

.promo-left p {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 30px 0;
}

.promo-right {
  flex: 1;
  text-align: center;
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
  
  .coach-grid {
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
  
  .coach-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .courses-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .features-section {
    grid-template-columns: 1fr;
  }
  
  .gym-info-content {
    grid-template-columns: 1fr;
    padding: 24px;
  }
  
  .banner-overlay h1 {
    font-size: 32px;
  }
  
  .banner-overlay p {
    font-size: 16px;
  }
}
</style>