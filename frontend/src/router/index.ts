import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/auth'
import Layout from '@/layout/index.vue'

/**
 * 路由表：主布局下挂载各业务页面
 */
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页仪表盘', icon: 'Odometer' }
      },
      {
        path: 'room-type',
        name: 'RoomType',
        component: () => import('@/views/room-type/index.vue'),
        meta: { title: '房型管理', icon: 'CollectionTag' }
      },
      {
        path: 'room',
        name: 'Room',
        component: () => import('@/views/room/index.vue'),
        meta: { title: '房间管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'bed',
        name: 'Bed',
        component: () => import('@/views/bed/index.vue'),
        meta: { title: '床位管理', icon: 'Sort' }
      },
      {
        path: 'guest',
        name: 'Guest',
        component: () => import('@/views/guest/index.vue'),
        meta: { title: '旅客管理', icon: 'User' }
      },
      {
        path: 'check-in',
        name: 'CheckIn',
        component: () => import('@/views/check-in/index.vue'),
        meta: { title: '入住管理', icon: 'CirclePlus' }
      },
      {
        path: 'check-out',
        name: 'CheckOut',
        component: () => import('@/views/check-out/index.vue'),
        meta: { title: '退房管理', icon: 'CircleCheck' }
      },
      {
        path: 'bill',
        name: 'Bill',
        component: () => import('@/views/bill/index.vue'),
        meta: { title: '账单管理', icon: 'Tickets' }
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('@/views/report/index.vue'),
        meta: { title: '统计报表', icon: 'DataAnalysis' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页，已登录访问登录页则跳回首页
router.beforeEach((to) => {
  const token = getToken()
  if (to.path === '/login') {
    return token ? '/' : true
  }
  if (!token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
