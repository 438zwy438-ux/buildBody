import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/user/home'
  },
  {
    path: '/landing',
    name: 'Landing',
    component: () => import('@/views/Landing.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/user/login',
    name: 'UserLogin',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: '会员登录' }
  },
  {
    path: '/user/register',
    name: 'UserRegister',
    component: () => import('@/views/user/Register.vue'),
    meta: { title: '会员注册' }
  },
  {
    path: '/user',
    component: () => import('@/layout/UserLayout.vue'),
    redirect: '/user/home',
    children: [
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('@/views/user/Home.vue'),
        meta: { title: '首页', requiresAuth: false }
      },
      {
        path: 'coach',
        name: 'CoachList',
        component: () => import('@/views/user/CoachList.vue'),
        meta: { title: '专业教练', requiresAuth: false }
      },
      {
        path: 'coach/:id',
        name: 'CoachDetail',
        component: () => import('@/views/user/CoachDetail.vue'),
        meta: { title: '教练详情', requiresAuth: false }
      },
      {
        path: 'equipment/:id',
        name: 'EquipmentDetail',
        component: () => import('@/views/user/EquipmentDetail.vue'),
        meta: { title: '设备详情', requiresAuth: false }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'member-card',
        name: 'UserMemberCard',
        component: () => import('@/views/user/MemberCard.vue'),
        meta: { title: '我的会员卡', requiresAuth: true, requiredRoles: ['MEMBER', 'VIP'] }
      },
      {
        path: 'courses',
        name: 'UserCourses',
        component: () => import('@/views/user/Courses.vue'),
        meta: { title: '我的课程', requiresAuth: true, requiredRoles: ['MEMBER', 'VIP'] }
      },
      {
        path: 'course-booking',
        name: 'UserCourseBooking',
        component: () => import('@/views/user/CourseBooking.vue'),
        meta: { title: '课程预约', requiresAuth: true, requiredRoles: ['MEMBER', 'VIP'] }
      },
      {
        path: 'entry-records',
        name: 'UserEntryRecords',
        component: () => import('@/views/user/EntryRecords.vue'),
        meta: { title: '入场记录', requiresAuth: true, requiredRoles: ['MEMBER', 'VIP'] }
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/user/Orders.vue'),
        meta: { title: '我的订单', requiresAuth: true, requiredRoles: ['MEMBER', 'VIP'] }
      }
    ]
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/Login.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/admin/register',
    name: 'AdminRegister',
    component: () => import('@/views/Register.vue'),
    meta: { title: '管理员注册' }
  },
  {
    path: '/admin',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/system/Users.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/system/Orders.vue'),
        meta: { title: '订单管理', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'banners',
        name: 'Banners',
        component: () => import('@/views/system/Banners.vue'),
        meta: { title: '轮播图管理', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'checkin',
        name: 'CheckIn',
        component: () => import('@/views/system/CheckIn.vue'),
        meta: { title: '入场管理', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'member-cards',
        name: 'MemberCards',
        component: () => import('@/views/business/MemberCards.vue'),
        meta: { title: '会员卡管理', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'card-templates',
        name: 'CardTemplates',
        component: () => import('@/views/business/CardTemplates.vue'),
        meta: { title: '会员卡模板', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('@/views/business/Courses.vue'),
        meta: { title: '课程管理', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'coach-profiles',
        name: 'CoachProfiles',
        component: () => import('@/views/business/CoachProfiles.vue'),
        meta: { title: '教练档案', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'member-profiles',
        name: 'MemberProfiles',
        component: () => import('@/views/business/MemberProfiles.vue'),
        meta: { title: '会员档案', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/business/Equipment.vue'),
        meta: { title: '器材管理', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'lockers',
        name: 'Lockers',
        component: () => import('@/views/business/Lockers.vue'),
        meta: { title: '储物柜管理', requiresAuth: true, requiredRoles: ['ADMIN'] }
      },
      {
        path: 'fix-logs',
        name: 'FixLogs',
        component: () => import('@/views/business/FixLogs.vue'),
        meta: { title: '维修记录', requiresAuth: true, requiredRoles: ['ADMIN'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const roles = JSON.parse(localStorage.getItem('roles') || '[]')
  
  if (to.meta.requiresAuth && !token) {
    if (to.path.startsWith('/user')) {
      next('/user/login')
    } else {
      next('/admin/login')
    }
  } else if (to.meta.requiredRoles && token) {
    const requiredRoles = to.meta.requiredRoles
    const hasPermission = requiredRoles.some(role => roles.includes(role))
    
    if (!hasPermission) {
      if (roles.includes('ADMIN')) {
        next('/admin/dashboard')
      } else {
        next('/user/home')
      }
    } else {
      next()
    }
  } else if ((to.path === '/user/login' || to.path === '/user/register') && token) {
    next('/user/home')
  } else if ((to.path === '/admin/login' || to.path === '/admin/register') && token) {
    if (roles.includes('ADMIN')) {
      next('/admin/dashboard')
    } else {
      next('/user/home')
    }
  } else {
    next()
  }
})

export default router