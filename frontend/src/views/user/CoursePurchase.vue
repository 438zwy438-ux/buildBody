<template>
  <div class="course-purchase-page">
    <div class="purchase-container">
      <div class="purchase-header">
        <h1>购买私教课程</h1>
        <p>确认课程信息并完成购买</p>
      </div>
      
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="course" class="purchase-content">
        <div class="course-info-card">
          <div class="course-image">
            <el-image
              :src="course.coverImg"
              fit="cover"
              style="width: 120px; height: 120px; border-radius: 10px"
            >
              <template #error>
                <div class="image-error">
                  <el-icon :size="30"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          
          <div class="course-details">
            <h3>{{ course.name }}</h3>
            <div class="course-meta">
              <span class="coach">教练：{{ coachName }}</span>
              <span class="duration">{{ course.duration }}分钟/次</span>
              <span class="times">{{ course.courseTimes }}次服务</span>
            </div>
            <div class="price">¥{{ course.price }}</div>
          </div>
        </div>
        
        <div class="purchase-form">
          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="购买数量" prop="quantity">
              <el-input-number 
                v-model="form.quantity" 
                :min="1" 
                :max="10" 
                controls-position="right"
                style="width: 150px"
              />
              <span class="form-tip">最多可购买10份</span>
            </el-form-item>
            
            <el-form-item label="总金额">
              <span class="total-amount">¥{{ totalAmount }}</span>
            </el-form-item>
            
            <el-form-item label="支付方式" prop="paymentMethod">
              <el-radio-group v-model="form.paymentMethod">
                <el-radio label="wechat">
                  <el-icon><ChatDotRound /></el-icon>
                  微信支付
                </el-radio>
                <el-radio label="alipay">
                  <el-icon><Money /></el-icon>
                  支付宝
                </el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" size="large" @click="handlePurchase" :loading="purchasing">
                <el-icon><ShoppingCart /></el-icon>
                确认购买
              </el-button>
              <el-button size="large" @click="goBack">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <div class="purchase-tips">
          <el-alert type="info" :closable="false" show-icon>
            <template #title>
              <div class="tips-content">
                <p>购买说明：</p>
                <ul>
                  <li>购买后课程将添加到您的会员卡中</li>
                  <li>课程有效期为购买后30天内</li>
                  <li>如需退款，请联系客服处理</li>
                  <li>课程购买后不可转让</li>
                </ul>
              </div>
            </template>
          </el-alert>
        </div>
      </div>
      
      <div v-else class="error-container">
        <el-icon :size="60" color="#f56c6c"><Warning /></el-icon>
        <p>课程信息加载失败</p>
        <el-button type="primary" @click="goBack">返回</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourseList, purchaseCourse } from '@/api/course'
import { getCoachProfileList } from '@/api/coachProfile'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Loading, 
  Warning, 
  Picture, 
  ShoppingCart, 
  ChatDotRound, 
  Money 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const course = ref(null)
const coaches = ref([])
const loading = ref(true)
const purchasing = ref(false)
const formRef = ref(null)

const form = ref({
  quantity: 1,
  paymentMethod: 'wechat'
})

const rules = {
  quantity: [
    { required: true, message: '请选择购买数量', trigger: 'blur' }
  ],
  paymentMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}

const coachName = computed(() => {
  if (!course.value || !coaches.value.length) return '未知教练'
  const coach = coaches.value.find(c => c.userId === course.value.coachUserId)
  return coach?.realName || '未知教练'
})

const totalAmount = computed(() => {
  if (!course.value) return 0
  return (course.value.price * form.value.quantity).toFixed(2)
})

const fetchCourseDetail = async () => {
  try {
    loading.value = true
    const courseId = route.query.courseId
    
    if (!courseId) {
      ElMessage.error('缺少课程ID')
      router.back()
      return
    }
    
    // 获取课程详情
    const res = await getCourseList({ size: 100 })
    const courses = res.data?.records || []
    course.value = courses.find(c => c.id == courseId)
    
    if (!course.value) {
      ElMessage.error('课程不存在')
      router.back()
      return
    }
    
    if (course.value.status !== 1) {
      ElMessage.warning('该课程已下架，无法购买')
      router.back()
      return
    }
    
    if (course.value.type !== 1) {
      ElMessage.warning('只能购买私教课程')
      router.back()
      return
    }
    
  } catch (error) {
    console.error('获取课程详情失败:', error)
    ElMessage.error('获取课程详情失败')
    router.back()
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

const handlePurchase = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    const result = await ElMessageBox.confirm(
      `确认购买 ${form.value.quantity} 份 "${course.value.name}" 课程，总金额 ¥${totalAmount.value}？`,
      '确认购买',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (result) {
      purchasing.value = true
      
      // 调用课程购买API
      const purchaseData = {
        courseId: course.value.id, // 课程ID
        quantity: form.value.quantity // 购买数量
        // userId 会自动从token中获取
      }
      
      await purchaseCourse(purchaseData)
      
      ElMessage.success('购买成功！课程已添加到您的会员卡中')
      
      // 跳转到订单页面或会员卡页面
      setTimeout(() => {
        router.push('/user/orders')
      }, 1500)
    }
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('购买失败:', error)
      ElMessage.error('购买失败：' + (error.message || '未知错误'))
    }
  } finally {
    purchasing.value = false
  }
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
.course-purchase-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.purchase-container {
  width: 100%;
  max-width: 600px;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.purchase-header {
  text-align: center;
  margin-bottom: 30px;
}

.purchase-header h1 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.purchase-header p {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  color: #666;
}

.loading-container p,
.error-container p {
  margin-top: 20px;
  font-size: 16px;
}

.course-info-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
  margin-bottom: 30px;
}

.course-details h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.course-meta {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-bottom: 10px;
}

.course-meta span {
  font-size: 14px;
  color: #666;
}

.price {
  font-size: 24px;
  color: #e74c3c;
  font-weight: 700;
}

.purchase-form {
  margin-bottom: 30px;
}

.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #999;
}

.total-amount {
  font-size: 20px;
  color: #e74c3c;
  font-weight: 700;
}

:deep(.el-radio-group) {
  display: flex;
  gap: 20px;
}

:deep(.el-radio) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-radio.is-checked) {
  border-color: #409eff;
  background: #f0f7ff;
}

.purchase-form .el-button {
  width: 150px;
  margin-right: 20px;
}

.tips-content p {
  font-weight: 600;
  margin-bottom: 10px;
}

.tips-content ul {
  margin: 0;
  padding-left: 20px;
}

.tips-content li {
  margin-bottom: 5px;
  font-size: 14px;
}

@media (max-width: 768px) {
  .course-purchase-page {
    padding: 10px;
  }
  
  .purchase-container {
    padding: 20px;
  }
  
  .purchase-header h1 {
    font-size: 24px;
  }
  
  .course-info-card {
    flex-direction: column;
    text-align: center;
  }
  
  :deep(.el-radio-group) {
    flex-direction: column;
  }
  
  .purchase-form .el-button {
    width: 100%;
    margin-bottom: 10px;
    margin-right: 0;
  }
}
</style>