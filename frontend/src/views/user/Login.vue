<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <el-icon class="logo-icon"><Trophy /></el-icon>
        <h2>健身俱乐部</h2>
        <p>会员登录</p>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名" 
            prefix-icon="User" 
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="Lock" 
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" size="large" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
        <el-form-item>
          <div class="login-footer">
            <el-link type="info" @click="goToAdminLogin">管理员登录</el-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await userStore.loginAction(loginForm)
        
        const allowedRoles = ['user', 'vip', 'coach']
        const userRoles = res.data.roles || []
        const hasAllowedRole = userRoles.some(role => allowedRoles.includes(role))
        
        if (!hasAllowedRole) {
          ElMessage.error('该账号不是会员或教练账号，请使用管理员登录')
          userStore.logout()
          return
        }
        
        ElMessage.success('登录成功')
        router.push('/user/home')
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const goToAdminLogin = () => {
  router.push('/admin/login')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 10px;
}

.login-header h2 {
  margin: 10px 0;
  color: #333;
  font-size: 24px;
}

.login-header p {
  color: #999;
  margin: 0;
}

.login-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  width: 100%;
}
</style>