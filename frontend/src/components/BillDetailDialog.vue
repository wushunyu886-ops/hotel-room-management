<template>
  <el-dialog v-model="visible" title="账单详情" width="600px" destroy-on-close>
    <div v-loading="loading">
      <el-descriptions v-if="bill" :column="2" border>
        <el-descriptions-item label="账单号">{{ bill.billNo }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <StatusTag type="pay" :value="bill.payStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="旅客">{{ bill.guestName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ bill.idCard }}</el-descriptions-item>
        <el-descriptions-item label="房间/床位">{{ bill.roomNo }} - {{ bill.bedNo }} 号床</el-descriptions-item>
        <el-descriptions-item label="房型单价">{{ formatMoney(bill.dailyPrice) }} / 天</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ bill.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="退房日期">{{ bill.checkOutDate }}</el-descriptions-item>
        <el-descriptions-item label="居住天数">{{ bill.days }} 天</el-descriptions-item>
        <el-descriptions-item label="总金额">
          <span class="money">{{ formatMoney(bill.totalAmount) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ bill.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="账单时间">{{ bill.createTime }}</el-descriptions-item>
      </el-descriptions>
    </div>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { getBill } from '@/api/bill'
import type { BillVO } from '@/types/checkIn'
import { formatMoney } from '@/utils/format'
import StatusTag from './StatusTag.vue'

/** 账单详情弹窗 */
const props = defineProps<{ visible: boolean; billId?: number }>()
const emit = defineEmits<{ (e: 'update:visible', v: boolean): void }>()

const visible = ref(props.visible)
watch(visible, (v) => emit('update:visible', v))

const bill = ref<BillVO | null>(null)
const loading = ref(false)

watch(
  () => props.visible,
  async (v) => {
    visible.value = v
    if (v && props.billId) {
      loading.value = true
      try {
        bill.value = await getBill(props.billId)
      } finally {
        loading.value = false
      }
    }
  }
)
</script>

<style scoped>
.money {
  font-weight: 600;
  color: #f56c6c;
}
</style>
