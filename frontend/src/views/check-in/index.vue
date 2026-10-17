<template>
  <PageContainer title="入住管理">
    <template #extra>
      <el-button type="primary" :icon="Plus" @click="checkInVisible = true">办理入住</el-button>
    </template>

    <SearchForm :model="query" @search="load" @reset="load">
      <el-form-item label="旅客姓名">
        <el-input v-model="query.guestName" placeholder="姓名" clearable style="width: 140px" />
      </el-form-item>
      <el-form-item label="房间号">
        <el-input v-model="query.roomNo" placeholder="如：南101" clearable style="width: 140px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 130px">
          <el-option value="CHECKED_IN" label="在住" />
          <el-option value="CHECKED_OUT" label="已退房" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column prop="guestName" label="旅客" width="100" />
      <el-table-column label="性别" width="70">
        <template #default="{ row }"><StatusTag type="gender" :value="row.gender" /></template>
      </el-table-column>
      <el-table-column prop="roomNo" label="房间" width="90" />
      <el-table-column prop="bedNo" label="床号" width="80">
        <template #default="{ row }">{{ row.bedNo }} 号</template>
      </el-table-column>
      <el-table-column prop="typeName" label="房型" />
      <el-table-column prop="checkInDate" label="入住日期" width="110" />
      <el-table-column prop="stayDays" label="已住天数" width="90" />
      <el-table-column prop="dailyPriceSnapshot" label="单价(元/天)" width="110">
        <template #default="{ row }">{{ formatMoney(row.dailyPriceSnapshot) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><StatusTag type="checkin" :value="row.status" /></template>
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

    <CheckInDialog v-model:visible="checkInVisible" @success="load" />
  </PageContainer>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import PageContainer from '@/components/PageContainer.vue'
import SearchForm from '@/components/SearchForm.vue'
import StatusTag from '@/components/StatusTag.vue'
import CheckInDialog from '@/components/CheckInDialog.vue'
import { pageCheckIns } from '@/api/checkIn'
import type { CheckInQuery, CheckInRecordVO } from '@/types/checkIn'
import type { CheckInStatus } from '@/types/common'
import { formatMoney } from '@/utils/format'

/** 入住管理页：在住/历史入住记录 */
const loading = ref(false)
const list = ref<CheckInRecordVO[]>([])
const total = ref(0)
const query = reactive<CheckInQuery>({ pageNum: 1, pageSize: 10, status: 'CHECKED_IN' })

const checkInVisible = ref(false)

async function load() {
  loading.value = true
  try {
    const res = await pageCheckIns(query)
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
</style>
