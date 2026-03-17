import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/system/Users.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/system/Orders.vue'),
        meta: { title: '订单管理', requiresAuth: true }
      },
      {
        path: 'banners',
        name: 'Banners',
        component: () => import('@/views/system/Banners.vue'),
        meta: { title: '轮播图管理', requiresAuth: true }
      },
      {
        path: 'checkin',
        name: 'CheckIn',
        component: () => import('@/views/system/CheckIn.vue'),
        meta: { title: '入场管理', requiresAuth: true }
      },
      {
        path: 'member-cards',
        name: 'MemberCards',
        component: () => import('@/views/business/MemberCards.vue'),
        meta: { title: '会员卡管理', requiresAuth: true }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('@/views/business/Courses.vue'),
        meta: { title: '课程管理', requiresAuth: true }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/business/Equipment.vue'),
        meta: { title: '器材管理', requiresAuth: true }
      },
      {
        path: 'lockers',
        name: 'Lockers',
        component: () => import('@/views/business/Lockers.vue'),
        meta: { title: '储物柜管理', requiresAuth: true }
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
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/')
  } else {
    next()
  }
})

export default router