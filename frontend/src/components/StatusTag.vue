<template>
  <el-tag :type="tagType" effect="light">{{ text }}</el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { bedStatusMap, checkInStatusMap, genderMap, orientationMap, payStatusMap, roleMap, roomStatusMap } from '@/utils/format'

/**
 * 状态标签组件：根据 type + value 展示中文文案与 Tag 颜色
 * type: gender / orientation / room / bed / checkin / pay / role
 */
const props = defineProps<{ type: string; value?: string | number | null }>()

const dictMap: Record<string, Record<string, string>> = {
  gender: genderMap,
  orientation: orientationMap,
  room: roomStatusMap,
  bed: bedStatusMap,
  checkin: checkInStatusMap,
  pay: payStatusMap,
  role: roleMap
}

const text = computed(() => {
  const dict = dictMap[props.type]
  const key = String(props.value ?? '')
  return dict ? dict[key] || key || '-' : key || '-'
})

const tagType = computed(() => {
  const v = String(props.value ?? '')
  switch (props.type) {
    case 'gender':
      return v === 'MALE' ? 'primary' : 'danger'
    case 'orientation':
      return v === 'SOUTH' ? 'success' : 'warning'
    case 'room':
    case 'bed':
      // 空闲 success、已住/已占用 warning、维修 danger
      return v === 'AVAILABLE' || v === 'FREE' ? 'success' : v === 'MAINTENANCE' ? 'danger' : 'warning'
    case 'checkin':
      return v === 'CHECKED_IN' ? 'warning' : 'info'
    case 'pay':
      return v === 'PAID' ? 'success' : 'danger'
    case 'role':
      return v === 'ADMIN' ? 'danger' : 'primary'
    default:
      return 'info'
  }
})
</script>
