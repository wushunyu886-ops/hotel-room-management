<template>
  <el-container class="layout">
    <!-- 左侧菜单 -->
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="layout-aside">
      <Sidebar :collapsed="appStore.sidebarCollapsed" />
    </el-aside>
    <el-container>
      <!-- 顶部 -->
      <el-header class="layout-header">
        <Header @toggle-sidebar="appStore.toggleSidebar()" />
      </el-header>
      <!-- 内容区 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()
</script>

<style scoped>
.layout {
  height: 100%;
}
.layout-aside {
  background-color: #001529;
  transition: width 0.2s;
  overflow: hidden;
}
.layout-header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  padding: 0 16px;
}
.layout-main {
  background: #f0f2f5;
  padding: 16px;
  overflow-y: auto;
}
</style>
