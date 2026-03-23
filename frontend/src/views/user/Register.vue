<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <el-icon class="logo-icon"><Trophy /></el-icon>
        <h2>健身俱乐部</h2>
        <p>会员注册</p>
      </div>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio :label="0">男</el-radio>
            <el-radio :label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="registerForm.age" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="会员卡" prop="cardTemplateId">
          <el-select v-model="registerForm.cardTemplateId" placeholder="请选择会员卡" style="width: 100%" :loading="cardTemplatesLoading">
            <el-option 
              v-for="template in cardTemplates" 
              :key="template.id" 
              :label="`${template.name} - ¥${template.price}`" 
              :value="template.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="selectedTemplate">
          <el-alert :title="`会员卡详情：${selectedTemplate.description || '暂无描述'}`" type="info" :closable="false" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">注册并办卡</el-button>
        </el-form-item>
        <el-form-item>
          <div class="register-footer">
            <span>已有账号?</span>
            <el-link type="primary" @click="goToLogin">立即登录</el-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { getCardTemplateList } from '@/api/cardTemplate'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const cardTemplates = ref([])
const cardTemplatesLoading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  realName: '',
  gender: 0,
  age: 20,
  cardTemplateId: null
})

const selectedTemplate = computed(() => {
  if (!registerForm.cardTemplateId) return null
  return cardTemplates.value.find(t => t.id === registerForm.cardTemplateId)
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  cardTemplateId: [{ required: true, message: '请选择会员卡', trigger: 'change' }]
}

const fetchCardTemplates = async () => {
  cardTemplatesLoading.value = true
  try {
    const res = await getCardTemplateList({
      current: 1,
      size: 100,
      status: 1
    })
    if (res.data.records) {
      cardTemplates.value = res.data.records
    }
  } catch (error) {
    console.error('获取会员卡模板失败:', error)
    ElMessage.error('获取会员卡模板失败')
  } finally {
    cardTemplatesLoading.value = false
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register(registerForm)
        ElMessage.success('注册成功,请登录')
        router.push('/user/login')
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push('/user/login')
}

onMounted(() => {
  fetchCardTemplates()
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 500px;
  max-width: 100%;
  padding: 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 10px;
}

.register-header h2 {
  margin: 10px 0;
  color: #333;
  font-size: 24px;
}

.register-header p {
  color: #999;
  margin: 0;
}

.register-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  width: 100%;
}
</style>