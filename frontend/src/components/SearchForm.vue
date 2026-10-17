<template>
  <el-form inline :model="model" @submit.prevent>
    <slot />
    <el-form-item>
      <el-button type="primary" :icon="Search" @click="$emit('search')">查询</el-button>
      <el-button :icon="Refresh" @click="handleReset">重置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { Search, Refresh } from '@element-plus/icons-vue'

/**
 * 通用查询表单容器：默认插槽放筛选字段，内置查询/重置按钮
 */
const props = defineProps<{ model: Record<string, unknown> }>()
const emit = defineEmits<{ (e: 'search'): void; (e: 'reset'): void }>()

/** 重置：清空所有筛选字段后触发查询 */
function handleReset() {
  Object.keys(props.model).forEach((key) => {
    props.model[key] = undefined
  })
  emit('reset')
}
</script>
