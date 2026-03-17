<template>
  <div class="member-card-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的会员卡</span>
        </div>
      </template>
      
      <div v-if="memberCardInfo.id" class="card-detail">
        <div class="card-visual">
          <div class="card-front">
            <div class="card-chip"></div>
            <div class="card-number">{{ memberCardInfo.cardNo }}</div>
            <div class="card-info">
              <div class="card-holder">{{ memberCardInfo.cardName }}</div>
              <div class="card-balance">余额: ¥{{ memberCardInfo.balance }}</div>
            </div>
          </div>
        </div>
        <div class="card-info-detail">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="卡号">{{ memberCardInfo.cardNo }}</el-descriptions-item>
            <el-descriptions-item label="会员卡类型">{{ memberCardInfo.cardName }}</el-descriptions-item>
            <el-descriptions-item label="余额">¥{{ memberCardInfo.balance }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="memberCardInfo.status === 1 ? 'success' : 'danger'">
                {{ memberCardInfo.status === 1 ? '正常' : '已过期' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      
      <el-empty v-else description="暂无会员卡，请联系管理员办理" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getMemberCardList } from '@/api/memberCard'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const memberCardInfo = ref({})

const fetchMemberCard = async () => {
  try {
    const res = await getMemberCardList({
      current: 1,
      size: 1,
      userId: userInfo.value.id
    })
    if (res.data.records && res.data.records.length > 0) {
      memberCardInfo.value = res.data.records[0]
    }
  } catch (error) {
    console.error('获取会员卡信息失败:', error)
  }
}

onMounted(() => {
  fetchMemberCard()
})
</script>

<style scoped>
.member-card-page {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 20px 0;
}

.card-visual {
  display: flex;
  justify-content: center;
  align-items: center;
}

.card-front {
  width: 350px;
  height: 220px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 15px;
  padding: 30px;
  color: white;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
}

.card-front::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: shine 3s infinite;
}

@keyframes shine {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20%, 20%); }
}

.card-chip {
  width: 50px;
  height: 35px;
  background: linear-gradient(135deg, #ffd700 0%, #ffaa00 100%);
  border-radius: 5px;
  margin-bottom: 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.card-number {
  font-size: 20px;
  letter-spacing: 3px;
  margin-bottom: 30px;
  font-family: 'Courier New', monospace;
}

.card-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.card-holder {
  font-size: 16px;
  font-weight: bold;
}

.card-balance {
  font-size: 18px;
  font-weight: bold;
}

.card-info-detail {
  display: flex;
  align-items: center;
}
</style>