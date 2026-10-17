<template>
  <div class="sidebar">
    <div class="logo">
      <el-icon :size="22" color="#409eff"><House /></el-icon>
      <span v-show="!collapsed" class="logo-text">旅馆客房管理系统</span>
    </div>
    <el-menu
      :default-active="route.path"
      router
      background-color="#001529"
      text-color="rgba(255,255,255,0.7)"
      active-text-color="#fff"
      :collapse="collapsed"
    >
      <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
        <el-icon><component :is="item.icon" /></el-icon>
        <template #title>{{ item.title }}</template>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps<{ collapsed: boolean }>()

const route = useRoute()

/** 菜单项（与路由表保持一致） */
const menus = [
  { path: '/dashboard', title: '首页仪表盘', icon: 'Odometer' },
  { path: '/room-type', title: '房型管理', icon: 'CollectionTag' },
  { path: '/room', title: '房间管理', icon: 'OfficeBuilding' },
  { path: '/bed', title: '床位管理', icon: 'Sort' },
  { path: '/guest', title: '旅客管理', icon: 'User' },
  { path: '/check-in', title: '入住管理', icon: 'CirclePlus' },
  { path: '/check-out', title: '退房管理', icon: 'CircleCheck' },
  { path: '/bill', title: '账单管理', icon: 'Tickets' },
  { path: '/report', title: '统计报表', icon: 'DataAnalysis' }
]

const activePath = computed(() => route.path)
</script>

<style scoped>
.sidebar {
  height: 100%;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
}
.logo-text {
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
}
.sidebar :deep(.el-menu) {
  border-right: none;
}
.sidebar :deep(.el-menu-item.is-active) {
  background-color: #409eff;
}
</style>
