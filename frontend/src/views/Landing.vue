<template>
  <div class="landing-page">
    <div class="landing-content">
      <div class="logo-section">
        <el-icon class="logo-icon"><Trophy /></el-icon>
        <h1>智能健身系统管理系统</h1>
        <p>专业健身服务，助您达成健身目标</p>
      </div>
      
      <div class="entry-section">
        <el-card class="entry-card" shadow="hover" @click="goToUser">
          <div class="entry-content">
            <el-icon class="entry-icon" color="#409eff"><User /></el-icon>
            <h3>会员端</h3>
            <p>会员登录、课程预约、入场记录</p>
            <el-button type="primary" size="large">进入会员端</el-button>
          </div>
        </el-card>
        
        <el-card class="entry-card" shadow="hover" @click="goToAdmin">
          <div class="entry-content">
            <el-icon class="entry-icon" color="#67c23a"><Setting /></el-icon>
            <h3>管理端</h3>
            <p>用户管理、课程管理、业务管理</p>
            <el-button type="success" size="large">进入管理端</el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToUser = () => {
  router.push('/user/login')
}

const goToAdmin = () => {
  router.push('/admin/login')
}
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.landing-content {
  text-align: center;
  max-width: 1000px;
  width: 100%;
}

.logo-section {
  margin-bottom: 60px;
}

.logo-icon {
  font-size: 80px;
  color: #fff;
  margin-bottom: 20px;
}

.logo-section h1 {
  color: #fff;
  font-size: 48px;
  margin: 0 0 15px 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.logo-section p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 20px;
  margin: 0;
}

.entry-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
}

.entry-card {
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.entry-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.entry-content {
  padding: 40px 20px;
}

.entry-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.entry-content h3 {
  font-size: 28px;
  color: #333;
  margin: 0 0 10px 0;
}

.entry-content p {
  color: #666;
  font-size: 16px;
  margin: 0 0 30px 0;
}

.entry-content .el-button {
  width: 100%;
  height: 50px;
  font-size: 18px;
}

@media (max-width: 768px) {
  .entry-section {
    grid-template-columns: 1fr;
  }
  
  .logo-section h1 {
    font-size: 32px;
  }
  
  .logo-section p {
    font-size: 16px;
  }
}
</style>