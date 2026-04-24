<template>
  <div class="equipment-page">
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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getEquipmentList } from '@/api/equipment'
import { Picture } from '@element-plus/icons-vue'

const router = useRouter()
const equipmentList = ref([])

const fetchEquipmentList = async () => {
  try {
    const res = await getEquipmentList({
      current: 1,
      size: 100,
      status: 1
    })
    if (res.data.records) {
      const allEquipment = res.data.records.map(equipment => ({
        ...equipment,
        images: equipment.images || []
      }))
      
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

const goToDetail = (equipmentId) => {
  router.push(`/user/equipment/${equipmentId}`)
}

onMounted(() => {
  fetchEquipmentList()
})
</script>

<style scoped>
.equipment-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
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

:deep(.el-carousel__arrow) {
  background: rgba(255, 255, 255, 0.8);
  color: #333;
}

:deep(.el-carousel__arrow:hover) {
  background: white;
  color: #667eea;
}

@media (max-width: 1200px) {
  .equipment-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .equipment-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .equipment-section {
    padding: 0 10px;
  }
  
  .section-header h2 {
    font-size: 28px;
  }
}
</style>