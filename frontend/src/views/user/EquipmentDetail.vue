<template>
  <div class="equipment-detail-page">
    <el-button @click="goBack" class="back-button">
      <el-icon><ArrowLeft /></el-icon>
      返回
    </el-button>
    
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <p>加载中...</p>
    </div>
    
    <div v-else-if="equipment" class="detail-container">
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
              <el-icon :size="100"><Picture /></el-icon>
              <p>暂无图片</p>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      
      <div class="info-section">
        <h1 class="equipment-name">{{ equipment.name }}</h1>
        
        <div class="equipment-meta">
          <el-tag :type="equipment.status === 1 ? 'success' : 'danger'" size="large">
            {{ equipment.status === 1 ? '正常' : '维护中' }}
          </el-tag>
          <span class="equipment-code">编号: {{ equipment.code }}</span>
          <span class="equipment-location">位置: {{ equipment.location }}</span>
        </div>
        
        <div class="equipment-description">
          <h2>设备描述</h2>
          <p>{{ equipment.detailDesc || '暂无描述' }}</p>
        </div>
        
        <div class="equipment-info-grid">
          <div class="info-item">
            <span class="label">购买日期</span>
            <span class="value">{{ formatDate(equipment.buyDate) }}</span>
          </div>
          <div class="info-item">
            <span class="label">设备状态</span>
            <span class="value">{{ getStatusText(equipment.status) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="error-container">
      <el-icon :size="60" color="#f56c6c"><Warning /></el-icon>
      <p>设备信息加载失败</p>
      <el-button type="primary" @click="goBack">返回</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getEquipmentDetail } from '@/api/equipment'
import { ArrowLeft, Loading, Warning, Picture } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const equipment = ref(null)
const images = ref([])
const loading = ref(true)

const fetchEquipmentDetail = async () => {
  try {
    loading.value = true
    const equipmentId = route.params.id
    const res = await getEquipmentDetail(equipmentId)
    
    if (res.data) {
      equipment.value = res.data.equipment
      images.value = res.data.images || []
    }
  } catch (error) {
    console.error('获取设备详情失败:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (date) => {
  if (!date) return '未知'
  return new Date(date).toLocaleDateString('zh-CN')
}

const getStatusText = (status) => {
  const statusMap = {
    1: '正常',
    2: '维修中',
    3: '已报废'
  }
  return statusMap[status] || '未知'
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchEquipmentDetail()
})
</script>

<style scoped>
.equipment-detail-page {
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
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
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

.equipment-name {
  font-size: 36px;
  color: #333;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.equipment-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.equipment-code,
.equipment-location {
  font-size: 16px;
  color: #666;
}

.equipment-description {
  margin-bottom: 30px;
}

.equipment-description h2 {
  font-size: 24px;
  color: #333;
  margin: 0 0 16px 0;
  font-weight: 600;
}

.equipment-description p {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin: 0;
}

.equipment-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
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
  .equipment-detail-page {
    padding: 10px;
  }
  
  .info-section {
    padding: 20px;
  }
  
  .equipment-name {
    font-size: 24px;
  }
  
  .equipment-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .equipment-info-grid {
    grid-template-columns: 1fr;
  }
}
</style>