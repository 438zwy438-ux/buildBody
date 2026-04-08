<template>
  <div class="user-layout">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <div class="logo">
            <el-icon><Trophy /></el-icon>
            <span>健身俱乐部</span>
          </div>
        </div>
        <div class="header-right">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            router
            background-color="transparent"
            text-color="#fff"
            active-text-color="#409eff"
          >
            <el-menu-item index="/user/home">首页</el-menu-item>
            <el-menu-item index="/user/card-shop">会员卡中心</el-menu-item>
            <el-menu-item index="/user/private-courses">私教课程</el-menu-item>
            <el-menu-item index="/user/courses" v-if="hasAnyRole(['user', 'vip'])">课程预约</el-menu-item>
            <el-menu-item index="/user/bookings" v-if="hasAnyRole(['user', 'vip'])">我的预约</el-menu-item>
            <el-menu-item index="/user/coach-course-check" v-if="hasAnyRole(['coach'])">课程核销</el-menu-item>
            <el-menu-item index="/user/coach-courses" v-if="hasAnyRole(['coach'])">我的课程</el-menu-item>
            <el-menu-item index="/user/locker-usage" v-if="hasAnyRole(['user', 'vip'])">储物柜使用</el-menu-item>
            <el-menu-item index="/user/member-card" v-if="hasAnyRole(['user', 'vip'])">我的会员卡</el-menu-item>
            <el-menu-item index="/user/entry-records" v-if="hasAnyRole(['user', 'vip'])">入场记录</el-menu-item>
            <el-menu-item index="/user/orders" v-if="hasAnyRole(['user', 'vip'])">我的订单</el-menu-item>
            <el-menu-item index="/user/profile" v-if="isAuthenticated">个人中心</el-menu-item>
          </el-menu>
          <template v-if="isAuthenticated">
            <el-dropdown @command="handleCommand" class="user-dropdown">
              <span class="user-info">
                <el-avatar :size="32" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
                <span>{{ userInfo.nickname || userInfo.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="router.push('/user/login')">登录</el-button>
          </template>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
      <el-footer class="footer">
        <p>© 2024 健身俱乐部 - 专业健身服务</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import { getMyCards } from '@/api/memberCard'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const userInfo = computed(() => userStore.userInfo)
const isAuthenticated = computed(() => !!localStorage.getItem('token'))
const roles = computed(() => userStore.roles)

const hasAnyRole = (roleList) => {
  return roleList.some(role => roles.value.includes(role))
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      userStore.logout()
      router.push('/user/login')
    } catch (error) {
      console.log('取消退出')
    }
  } else if (command === 'profile') {
    router.push('/user/profile')
  }
}

const fetchMyCards = async () => {
  try {
    const res = await getMyCards()
    console.log('我的会员卡列表：', res.data)
  } catch (error) {
    console.error('获取会员卡失败：', error)
  }
}

watch(() => route.path, (newPath) => {
  if (newPath === '/user/member-card' && isAuthenticated.value) {
    fetchMyCards()
  }
})

onMounted(() => {
  if (route.path === '/user/member-card' && isAuthenticated.value) {
    fetchMyCards()
  }
})
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.header {
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 70px;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  font-size: 24px;
  font-weight: bold;
}

.logo .el-icon {
  font-size: 32px;
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.header-right .el-menu {
  border: none;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  padding: 5px 10px;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.main {
  padding: 20px;
  min-height: calc(100vh - 140px);
}

.footer {
  background-color: rgba(0, 0, 0, 0.8);
  color: #fff;
  text-align: center;
  padding: 20px;
  margin-top: auto;
}

.footer p {
  margin: 0;
  opacity: 0.8;
}
</style>