<template>
  <div class="card-shop-page">
    <div class="banner">
      <h1>会员卡中心</h1>
      <p>选择适合您的会员卡，开启健身之旅</p>
    </div>

    <div class="user-status" v-if="hasAnyRole(['user'])">
      <el-card>
        <div class="status-content">
          <div class="status-info">
            <el-icon class="status-icon"><User /></el-icon>
            <div>
              <div class="status-title">当前会员状态</div>
              <div class="status-text" :class="{ expired: isExpired }">
                {{ memberCardStatus }}
              </div>
            </div>
          </div>
          <div class="status-action" v-if="isExpired">
            <el-button type="danger" size="small" @click="scrollToCards">立即续费</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div class="card-list" ref="cardListRef">
      <h2>会员卡类型</h2>
      <el-row :gutter="30">
        <el-col :span="8" v-for="template in cardTemplates" :key="template.id">
          <el-card class="card-item" :class="{ 'time-card': template.type === 1, 'count-card': template.type === 2 }">
            <div class="card-badge">
              <el-tag v-if="template.type === 1" type="primary">时间卡</el-tag>
              <el-tag v-else type="success">次卡</el-tag>
            </div>
            <div class="card-name">{{ template.name }}</div>
            <div class="card-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ template.price }}</span>
            </div>
            <div class="card-features">
              <div class="feature-item" v-if="template.type === 1">
                <el-icon><Clock /></el-icon>
                <span>有效期 {{ template.durationDays }} 天</span>
              </div>
              <div class="feature-item" v-else>
                <el-icon><Tickets /></el-icon>
                <span>包含 {{ template.times }} 次</span>
              </div>
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>全场通用</span>
              </div>
              <div class="feature-item">
                <el-icon><Check /></el-icon>
                <span>专业指导</span>
              </div>
            </div>
            <div class="card-description">{{ template.description }}</div>
            <el-button 
              type="primary" 
              size="large" 
              class="purchase-btn"
              @click="handlePurchase(template)"
              :disabled="template.status !== 1 || !hasAnyRole(['user'])"
            >
              {{ template.status === 1 ? (hasAnyRole(['user']) ? '立即购买' : '请先成为会员') : '已下架' }}
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-dialog v-model="purchaseDialogVisible" title="确认购买" width="500px">
      <div class="purchase-confirm">
        <div class="confirm-card">
          <div class="confirm-name">{{ selectedTemplate?.name }}</div>
          <div class="confirm-price">¥{{ selectedTemplate?.price }}</div>
        </div>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="卡种类型">
            <el-tag v-if="selectedTemplate?.type === 1" type="primary">时间卡</el-tag>
            <el-tag v-else type="success">次卡</el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedTemplate?.type === 1" label="有效期">
            {{ selectedTemplate?.durationDays }} 天
          </el-descriptions-item>
          <el-descriptions-item v-else label="包含次数">
            {{ selectedTemplate?.times }} 次
          </el-descriptions-item>
          <el-descriptions-item label="支付金额">
            <span class="confirm-amount">¥{{ selectedTemplate?.price }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="purchaseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPurchase" :loading="purchasing">
          确认支付
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="loginDialogVisible" title="请先登录" width="400px">
      <div class="login-tip">
        <el-icon class="login-icon"><Warning /></el-icon>
        <p>购买会员卡需要先登录</p>
      </div>
      <template #footer>
        <el-button @click="loginDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="goToLogin">去登录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCardTemplateList } from '@/api/cardTemplate'
import { getMyCards, purchaseMemberCard } from '@/api/memberCard'
import { ElMessage } from 'element-plus'
import { User, Clock, Tickets, Check, Warning } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const hasAnyRole = (roleList) => {
  return roleList.some(role => userStore.roles.includes(role))
}

const cardTemplates = ref([])
const memberCardInfo = ref({})
const purchaseDialogVisible = ref(false)
const loginDialogVisible = ref(false)
const selectedTemplate = ref(null)
const purchasing = ref(false)
const cardListRef = ref(null)

const isExpired = computed(() => {
  if (!memberCardInfo.value.id) return false
  if (memberCardInfo.value.status !== 1) return true
  const expireTime = new Date(memberCardInfo.value.expireTime)
  return expireTime < new Date()
})

const memberCardStatus = computed(() => {
  if (!memberCardInfo.value.id) return '暂无会员卡'
  if (memberCardInfo.value.status === 2) return '已冻结'
  if (isExpired.value) return '已过期'
  return '正常'
})

const fetchCardTemplates = async () => {
  try {
    const res = await getCardTemplateList({ status: 1 })
    cardTemplates.value = res.data.records || []
  } catch (error) {
    console.error('获取会员卡模板失败:', error)
  }
}

const fetchMyCard = async () => {
  if (!userInfo.value) return
  try {
    const res = await getMyCards()
    if (res.data && res.data.length > 0) {
      memberCardInfo.value = res.data[0]
    }
  } catch (error) {
    console.error('获取会员卡信息失败:', error)
  }
}

const handlePurchase = (template) => {
  if (!userInfo.value) {
    loginDialogVisible.value = true
    selectedTemplate.value = template
    return
  }
  selectedTemplate.value = template
  purchaseDialogVisible.value = true
}

const confirmPurchase = async () => {
  if (!selectedTemplate.value) return
  
  purchasing.value = true
  try {
    await purchaseMemberCard({
      cardTemplateId: selectedTemplate.value.id,
      quantity: 1
    })
    ElMessage.success('购买成功！')
    purchaseDialogVisible.value = false
    selectedTemplate.value = null
    await fetchMyCard()
  } catch (error) {
    console.error('购买失败:', error)
    ElMessage.error(error.message || '购买失败')
  } finally {
    purchasing.value = false
  }
}

const goToLogin = () => {
  loginDialogVisible.value = false
  router.push('/login')
}

const scrollToCards = () => {
  cardListRef.value?.scrollIntoView({ behavior: 'smooth' })
}

onMounted(() => {
  fetchCardTemplates()
  if (hasAnyRole(['user'])) {
    fetchMyCard()
  }
})
</script>

<style scoped>
.card-shop-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.banner {
  text-align: center;
  padding: 60px 20px;
  color: white;
}

.banner h1 {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.banner p {
  font-size: 20px;
  opacity: 0.9;
}

.user-status {
  max-width: 1200px;
  margin: -30px auto 40px;
  padding: 0 20px;
}

.status-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.status-icon {
  font-size: 48px;
  color: #409eff;
}

.status-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.status-text {
  font-size: 20px;
  font-weight: bold;
  color: #67c23a;
}

.status-text.expired {
  color: #f56c6c;
}

.card-list {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.card-list h2 {
  font-size: 32px;
  color: white;
  margin-bottom: 40px;
  text-align: center;
}

.card-item {
  margin-bottom: 30px;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  border-radius: 20px;
  overflow: hidden;
}

.card-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.card-item.time-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-item.count-card {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-badge {
  text-align: center;
  margin-bottom: 20px;
}

.card-name {
  font-size: 28px;
  font-weight: bold;
  color: white;
  text-align: center;
  margin-bottom: 20px;
}

.card-price {
  text-align: center;
  margin-bottom: 30px;
}

.price-symbol {
  font-size: 24px;
  color: white;
}

.price-value {
  font-size: 48px;
  font-weight: bold;
  color: white;
}

.card-features {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  font-size: 16px;
  margin-bottom: 12px;
}

.feature-item:last-child {
  margin-bottom: 0;
}

.feature-item .el-icon {
  font-size: 20px;
}

.card-description {
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 20px;
  min-height: 42px;
}

.purchase-btn {
  width: 100%;
  font-size: 18px;
  font-weight: bold;
  border-radius: 25px;
  padding: 15px;
}

.purchase-confirm {
  padding: 20px 0;
}

.confirm-card {
  text-align: center;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 15px;
  margin-bottom: 20px;
  color: white;
}

.confirm-name {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.confirm-price {
  font-size: 36px;
  font-weight: bold;
}

.confirm-amount {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.login-tip {
  text-align: center;
  padding: 30px 0;
}

.login-icon {
  font-size: 64px;
  color: #e6a23c;
  margin-bottom: 20px;
}

.login-tip p {
  font-size: 18px;
  color: #666;
}
</style>