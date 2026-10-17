<template>
  <div v-loading="loading">
    <el-table :data="beds" border size="small">
      <el-table-column prop="bedNo" label="床号" width="120">
        <template #default="{ row }">{{ row.bedNo }} 号床</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <StatusTag type="bed" :value="row.bedStatus" />
        </template>
      </el-table-column>
      <el-table-column prop="guestName" label="入住旅客" />
      <el-table-column prop="checkInDate" label="入住日期" width="140" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { listRoomBeds } from '@/api/room'
import type { RoomBedVO } from '@/types/room'
import StatusTag from './StatusTag.vue'

/** 房间床位状态表 */
const props = defineProps<{ roomId: number }>()

const beds = ref<RoomBedVO[]>([])
const loading = ref(false)

async function load() {
  if (!props.roomId) return
  loading.value = true
  try {
    beds.value = await listRoomBeds(props.roomId)
  } finally {
    loading.value = false
  }
}

onMounted(load)
watch(() => props.roomId, load)
</script>
