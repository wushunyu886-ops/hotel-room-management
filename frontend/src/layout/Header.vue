<template>
  <div class="header">
    <div class="header-left">
      <el-icon class="collapse-btn" @click="$emit('toggle-sidebar')">
        <Expand v-if="collapsed" />
        <Fold v-else />
      </el-icon>
      <Breadcrumb />
    </div>
    <el-dropdown @command="handleCommand">
      <span class="user-info">
        <el-icon><UserFilled /></el-icon>
        <span class="name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
        <el-tag size="small" type="info" effect="plain">{{ roleText }}</el-tag>
        <el-icon><ArrowDown /></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import Breadcrumb from './Breadcrumb.vue'
import { useUserStore } from '@/stores/user'
import { dictText, roleMap } from '@/utils/format'

defineProps<{ collapsed?: boolean }>()
defineEmits(['toggle-sidebar'])

const router = useRouter()
const userStore = useUserStore()

const roleText = computed(() => dictText(roleMap, userStore.userInfo?.role))

/** 退出登录：确认后清理登录态并跳转登录页 */
async function handleCommand(command: string) {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  outline: none;
}
.name {
  font-size: 14px;
}
</style>
