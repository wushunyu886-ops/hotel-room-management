<template>
  <el-card shadow="never" class="page-container">
    <template #header>
      <div class="page-header">
        <span class="page-title">{{ title }}</span>
        <div class="page-extra">
          <slot name="extra" />
        </div>
      </div>
    </template>
    <slot />
  </el-card>
</template>

<script setup lang="ts">
/** 页面容器：统一标题栏 + 内容区 */
defineProps<{ title: string }>()
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
