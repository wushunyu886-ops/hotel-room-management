<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">{{ item.title }}</el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

/** 依据路由 meta.title 生成面包屑 */
const breadcrumbs = computed(() =>
  route.matched.filter((item) => item.meta?.title).map((item) => ({ path: item.path, title: item.meta.title as string }))
)
</script>
