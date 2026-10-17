import { defineStore } from 'pinia'

/**
 * 应用状态：侧边栏折叠等 UI 状态
 */
export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarCollapsed: false
  }),
  actions: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    }
  }
})
