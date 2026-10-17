<template>
  <PageContainer title="账单管理">
    <SearchForm :model="query" @search="load" @reset="load">
      <el-form-item label="账单号">
        <el-input v-model="query.billNo" placeholder="账单号" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="旅客姓名">
        <el-input v-model="query.guestName" placeholder="姓名" clearable style="width: 140px" />
      </el-form-item>
      <el-form-item label="支付状态">
        <el-select v-model="query.payStatus" clearable placeholder="全部" style="width: 130px">
          <el-option value="UNPAID" label="未支付" />
          <el-option value="PAID" label="已支付" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column prop="billNo" label="账单号" width="200" />
      <el-table-column prop="guestName" label="旅客" width="100" />
      <el-table-column prop="roomNo" label="房间" width="90" />
      <el-table-column prop="bedNo" label="床号" width="80">
        <template #default="{ row }">{{ row.bedNo }} 号</template>
      </el-table-column>
      <el-table-column prop="days" label="天数" width="70" />
      <el-table-column prop="dailyPrice" label="单价" width="100">
        <template #default="{ row }">{{ formatMoney(row.dailyPrice) }}</template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template #default="{ row }"><span class="money">{{ formatMoney(row.totalAmount) }}</span></template>
      </el-table-column>
      <el-table-column label="支付状态" width="100">
        <template #default="{ row }"><StatusTag type="pay" :value="row.payStatus" /></template>
      </el-table-column>
      <el-table-column prop="createTime" label="账单时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">详情</el-button>
          <el-button v-if="row.payStatus === 'UNPAID'" link type="success" @click="handlePay(row)">支付</el-button>
        </template>
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

    <BillDetailDialog v-model:visible="detailVisible" :bill-id="detailBillId" />
  </PageContainer>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageContainer from '@/components/PageContainer.vue'
import SearchForm from '@/components/SearchForm.vue'
import StatusTag from '@/components/StatusTag.vue'
import BillDetailDialog from '@/components/BillDetailDialog.vue'
import { pageBills, payBill } from '@/api/bill'
import type { BillQuery, BillVO } from '@/types/checkIn'
import type { PayStatus } from '@/types/common'
import { formatMoney } from '@/utils/format'

/** 账单管理页 */
const loading = ref(false)
const list = ref<BillVO[]>([])
const total = ref(0)
const query = reactive<BillQuery>({ pageNum: 1, pageSize: 10 })

const detailVisible = ref(false)
const detailBillId = ref<number>()

function openDetail(row: BillVO) {
  detailBillId.value = row.id
  detailVisible.value = true
}

async function load() {
  loading.value = true
  try {
    const res = await pageBills(query)
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

async function handlePay(row: BillVO) {
  await ElMessageBox.confirm(
    `确认收取账单 ${row.billNo} 的住宿费 ${formatMoney(row.totalAmount)} 吗？`,
    '账单支付',
    { type: 'warning' }
  )
  await payBill(row.id)
  ElMessage.success('支付成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.money {
  font-weight: 600;
  color: #f56c6c;
}
</style>
