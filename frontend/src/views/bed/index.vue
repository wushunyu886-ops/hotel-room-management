<template>
  <PageContainer title="床位管理">
    <SearchForm :model="query" @search="load" @reset="load">
      <el-form-item label="房间号">
        <el-input v-model="query.roomNo" placeholder="如：南101" clearable style="width: 160px" />
      </el-form-item>
      <el-form-item label="床位状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 140px">
          <el-option value="FREE" label="空闲" />
          <el-option value="OCCUPIED" label="已占用" />
          <el-option value="MAINTENANCE" label="维修" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column prop="roomNo" label="房间号" width="100" />
      <el-table-column prop="bedNo" label="床号" width="100">
        <template #default="{ row }">{{ row.bedNo }} 号床</template>
      </el-table-column>
      <el-table-column label="朝向" width="80">
        <template #default="{ row }"><StatusTag type="orientation" :value="row.orientation" /></template>
      </el-table-column>
      <el-table-column prop="typeName" label="房型" />
      <el-table-column label="床位状态" width="110">
        <template #default="{ row }"><StatusTag type="bed" :value="row.bedStatus" /></template>
      </el-table-column>
      <el-table-column label="房间性别限制" width="120">
        <template #default="{ row }">
          <StatusTag v-if="row.currentGender" type="gender" :value="row.currentGender" />
          <span v-else class="none">不限</span>
        </template>
      </el-table-column>
      <el-table-column prop="guestName" label="占用旅客" width="120">
        <template #default="{ row }">{{ row.guestName || '-' }}</template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pager"
      background
      layout="total, sizes, prev, pager, next"
      :total="total"
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :page-sizes="[10, 20, 50]"
      @change="load"
    />
  </PageContainer>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import PageContainer from '@/components/PageContainer.vue'
import SearchForm from '@/components/SearchForm.vue'
import StatusTag from '@/components/StatusTag.vue'
import { pageBeds } from '@/api/bed'
import type { BedVO } from '@/types/room'
import type { BedStatus } from '@/types/common'

/** 床位管理页 */
const loading = ref(false)
const list = ref<BedVO[]>([])
const total = ref(0)
const query = reactive<{ pageNum: number; pageSize: number; roomNo?: string; status?: BedStatus }>({
  pageNum: 1,
  pageSize: 10
})

async function load() {
  loading.value = true
  try {
    const res = await pageBeds(query)
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.none {
  color: #909399;
}
</style>
