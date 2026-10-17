<template>
  <PageContainer title="退房管理">
    <SearchForm :model="query" @search="load" @reset="load">
      <el-form-item label="旅客姓名">
        <el-input v-model="query.guestName" placeholder="姓名" clearable style="width: 140px" />
      </el-form-item>
      <el-form-item label="房间号">
        <el-input v-model="query.roomNo" placeholder="如：南101" clearable style="width: 140px" />
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
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link type="danger" @click="openCheckOut(row)">办理退房</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pager"
      background
      layout="total, prev, pager, next"
      :total="total"
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      @change="load"
    />

    <CheckOutDialog v-model:visible="dialogVisible" :record="currentRecord" @success="load" />
  </PageContainer>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import PageContainer from '@/components/PageContainer.vue'
import SearchForm from '@/components/SearchForm.vue'
import StatusTag from '@/components/StatusTag.vue'
import CheckOutDialog from '@/components/CheckOutDialog.vue'
import { pageCheckIns } from '@/api/checkIn'
import type { CheckInQuery, CheckInRecordVO } from '@/types/checkIn'
import { formatMoney } from '@/utils/format'

/** 退房管理页：展示在住记录，办理退房并生成账单 */
const loading = ref(false)
const list = ref<CheckInRecordVO[]>([])
const total = ref(0)
const query = reactive<CheckInQuery>({ pageNum: 1, pageSize: 10, status: 'CHECKED_IN' })

const dialogVisible = ref(false)
const currentRecord = ref<CheckInRecordVO | null>(null)

function openCheckOut(row: CheckInRecordVO) {
  currentRecord.value = row
  dialogVisible.value = true
}

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
