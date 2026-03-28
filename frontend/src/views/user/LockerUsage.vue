<template>
  <div class="locker-usage-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>储物柜使用</span>
        </div>
      </template>

      <div class="locker-container">
        <div class="gender-selection">
          <el-button 
            :type="selectedGender === 'man' ? 'primary' : 'default'" 
            size="large"
            @click="handleGenderSelect('man')"
          >
            男子更衣室
          </el-button>
          <el-button 
            :type="selectedGender === 'woman' ? 'primary' : 'default'" 
            size="large"
            @click="handleGenderSelect('woman')"
          >
            女子更衣室
          </el-button>
        </div>

        <div v-if="!userInfo" class="phone-input-section">
          <el-form :model="phoneForm" :rules="phoneRules" ref="phoneFormRef" label-width="80px">
            <el-form-item label="手机号" prop="phone">
              <el-input 
                v-model="phoneForm.phone" 
                placeholder="请输入手机号验证会员身份" 
                clearable
                style="width: 300px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleVerifyPhone">验证会员</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-else class="user-info-section">
          <div class="user-info">
            <el-avatar :size="60" :src="userInfo.faceImgUrl">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="user-details">
              <h3>{{ userInfo.realName }}</h3>
              <p>{{ userInfo.phone }}</p>
              <el-tag v-if="userInfo.isVip === 1" type="success">VIP会员</el-tag>
              <el-tag v-else type="info">普通会员</el-tag>
            </div>
          </div>
          <el-button type="danger" @click="handleLogout">退出登录</el-button>
        </div>

        <div v-if="userInfo && !myLocker" class="available-lockers-section">
          <el-divider>可用储物柜</el-divider>
          <div v-if="availableLockers.length === 0" class="empty-state">
            <el-empty description="暂无可用储物柜" />
          </div>
          <div v-else class="lockers-grid">
            <div 
              v-for="locker in availableLockers" 
              :key="locker.id" 
              class="locker-item"
              @click="handleSelectLocker(locker)"
            >
              <div class="locker-icon">
                <el-icon :size="40"><Box /></el-icon>
              </div>
              <div class="locker-info">
                <h4>{{ locker.boxNo }}号柜</h4>
                <el-tag type="success">空闲</el-tag>
              </div>
            </div>
          </div>
        </div>

        <div v-if="myLocker" class="my-locker-section">
          <el-divider>我的储物柜</el-divider>
          <div class="my-locker-card">
            <div class="locker-icon large">
              <el-icon :size="60"><Box /></el-icon>
            </div>
            <div class="locker-details">
              <h3>{{ myLocker.boxNo }}号柜</h3>
              <p>位置: {{ selectedGender === 'man' ? '男子更衣室' : '女子更衣室' }}</p>
              <el-tag :type="myLocker.isLocker === 1 ? 'warning' : 'success'">
                {{ myLocker.isLocker === 1 ? '已上锁' : '已开锁' }}
              </el-tag>
            </div>
          </div>
          <div class="action-buttons">
            <el-button type="warning" size="large" @click="handleTempOpen">
              临时开柜
            </el-button>
            <el-button type="danger" size="large" @click="handleReturnLocker">
              还柜
            </el-button>
          </div>
          <div class="lock-button-section">
            <el-button 
              v-if="myLocker.isLocker === 0" 
              type="success" 
              size="large" 
              @click="handleLockDoor"
              class="lock-door-btn"
            >
              推门锁上
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  verifyMemberByPhone,
  getAvailableLockers,
  useLocker,
  tempOpenLocker,
  returnLocker,
  lockLocker,
  getMyLocker
} from '@/api/locker'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Box } from '@element-plus/icons-vue'

const selectedGender = ref('man')
const userInfo = ref(null)
const availableLockers = ref([])
const myLocker = ref(null)
const phoneFormRef = ref(null)

const phoneForm = reactive({
  phone: ''
})

const phoneRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const handleGenderSelect = (gender) => {
  selectedGender.value = gender
  if (userInfo.value) {
    fetchMyLocker()
    fetchAvailableLockers()
  }
}

const handleVerifyPhone = async () => {
  if (!phoneFormRef.value) return
  
  await phoneFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await verifyMemberByPhone({ 
          phone: phoneForm.phone,
          areaCode: selectedGender.value 
        })
        userInfo.value = res.data
        ElMessage.success('验证成功')
        
        await fetchMyLocker()
        // await fetchAvailableLockers()
      } catch (error) {
        console.error('验证失败:', error)
        ElMessage.error(error.response?.data?.msg || '验证失败，请检查手机号')
      }
    }
  })
}

const fetchMyLocker = async () => {
  try {
    const res = await getMyLocker({ areaCode: selectedGender.value })
    myLocker.value = res.data
  } catch (error) {
    console.error('获取我的储物柜失败:', error)
    myLocker.value = null
  }
}

const fetchAvailableLockers = async () => {
  try {
    const res = await getAvailableLockers({ areaCode: selectedGender.value })
    availableLockers.value = res.data || []
  } catch (error) {
    console.error('获取可用储物柜失败:', error)
    availableLockers.value = []
  }
}

const handleSelectLocker = async (locker) => {
  try {
    await ElMessageBox.confirm(`确定要使用${locker.boxNo}号柜吗?`, '确认使用', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await useLocker({ lockerId: locker.id })
    
    ElMessage.success('使用成功，储物柜已开锁')
    fetchMyLocker()
    fetchAvailableLockers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('使用储物柜失败:', error)
      ElMessage.error(error.response?.data?.msg || '使用失败')
    }
  }
}

const handleTempOpen = async () => {
  try {
    await tempOpenLocker({ lockerId: myLocker.value.id })
    ElMessage.success('临时开锁成功')
    fetchMyLocker()
  } catch (error) {
    console.error('临时开锁失败:', error)
    ElMessage.error(error.response?.data?.msg || '临时开锁失败')
  }
}

const handleReturnLocker = async () => {
  try {
    await ElMessageBox.confirm('确定要还柜吗? 还柜后将释放该储物柜。', '确认还柜', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    if (myLocker.value.isLocker === 1) {
      await tempOpenLocker({ lockerId: myLocker.value.id })
      ElMessage.success('已开锁，请取完物品后等待释放')
    }
    
    await returnLocker()
    
    ElMessage.success('还柜成功')
    myLocker.value = null
    fetchAvailableLockers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('还柜失败:', error)
      ElMessage.error(error.response?.data?.msg || '还柜失败')
    }
  }
}

const handleLogout = () => {
  userInfo.value = null
  myLocker.value = null
  availableLockers.value = []
  phoneForm.phone = ''
  ElMessage.info('已退出登录')
}

const handleLockDoor = async () => {
  try {
    await ElMessageBox.confirm('确定要推门锁上吗?', '确认上锁', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await lockLocker({ lockerId: myLocker.value.id })
    
    ElMessage.success('上锁成功')
    fetchMyLocker()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('上锁失败:', error)
      ElMessage.error(error.response?.data?.msg || '上锁失败')
    }
  }
}

onMounted(() => {
  
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.locker-container {
  max-width: 800px;
  margin: 0 auto;
}

.gender-selection {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 40px;
}

.phone-input-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
}

.user-info-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 30px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-details h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.user-details p {
  margin: 0 0 8px 0;
  color: #606266;
}

.available-lockers-section {
  margin-top: 30px;
}

.empty-state {
  padding: 40px 0;
}

.lockers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.locker-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.locker-item:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.locker-icon {
  color: #409eff;
  margin-bottom: 10px;
}

.locker-icon.large {
  color: #67c23a;
  margin-bottom: 20px;
}

.locker-info h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.my-locker-section {
  margin-top: 30px;
}

.my-locker-card {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 30px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 12px;
  margin-bottom: 30px;
}

.locker-details h3 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}

.locker-details p {
  margin: 0 0 10px 0;
  color: #606266;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px 0;
}

.action-buttons .el-button {
  min-width: 150px;
  height: 50px;
  font-size: 16px;
}

.lock-button-section {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}

.lock-door-btn {
  min-width: 200px;
  height: 50px;
  font-size: 16px;
  font-weight: bold;
}
</style>