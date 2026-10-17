<template>
  <el-dialog v-model="visible" title="办理退房" width="560px" destroy-on-close>
    <el-descriptions v-if="record" :column="2" border>
      <el-descriptions-item label="旅客">{{ record.guestName }}（{{ genderMap[record.gender] }}）</el-descriptions-item>
      <el-descriptions-item label="房间/床位">{{ record.roomNo }} - {{ record.bedNo }} 号床</el-descriptions-item>
      <el-descriptions-item label="房型">{{ record.typeName }}</el-descriptions-item>
      <el-descriptions-item label="入住日期">{{ record.checkInDate }}</el-descriptions-item>
      <el-descriptions-item label="退房日期">
        <el-date-picker v-model="checkOutDate" type="date" value-format="YYYY-MM-DD"
          :disabled-date="(d: Date) => dayjs(d).isBefore(dayjs(record.checkInDate), 'day') || dayjs(d).isAfter(dayjs(), 'day')" />
      </el-descriptions-item>
      <el-descriptions-item label="居住天数">
        <span class="money">{{ days }} 天</span>
      </el-descriptions-item>
      <el-descriptions-item label="每日单价">{{ formatMoney(record.dailyPriceSnapshot) }}</el-descriptions-item>
      <el-descriptions-item label="总金额">
        <span class="money">{{ formatMoney(totalAmount) }}</span>
      </el-descriptions-item>
    </el-descriptions>
    <el-alert type="info" :closable="false" class="tip"
      title="结算规则：居住天数 = 退房日期 - 入住日期，住一晚算一天、不足一天按一天计算（最少 1 天）" />
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit">确认退房并生成账单</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { checkOut } from '@/api/checkIn'
import type { CheckInRecordVO } from '@/types/checkIn'
import { formatMoney, genderMap } from '@/utils/format'

/** 退房弹窗：展示结算明细，确认后生成账单 */
const props = defineProps<{ visible: boolean; record?: CheckInRecordVO | null }>()
const emit = defineEmits<{ (e: 'update:visible', v: boolean): void; (e: 'success'): void }>()

const visible = ref(props.visible)
watch(visible, (v) => emit('update:visible', v))

const submitting = ref(false)
const checkOutDate = ref(dayjs().format('YYYY-MM-DD'))

watch(
  () => props.visible,
  (v) => {
    visible.value = v
    if (v && props.record) {
      // 默认今天，且不早于入住日期
      const today = dayjs().format('YYYY-MM-DD')
      checkOutDate.value = dayjs(today).isBefore(dayjs(props.record.checkInDate)) ? props.record.checkInDate : today
    }
  }
)

/** 居住天数：不足一天按一天（最少 1 天） */
const days = computed(() => {
  if (!props.record) return 0
  const diff = dayjs(checkOutDate.value).diff(dayjs(props.record.checkInDate), 'day')
  return Math.max(diff, 1)
})

const totalAmount = computed(() => days.value * (props.record?.dailyPriceSnapshot || 0))

async function submit() {
  if (!props.record) return
  submitting.value = true
  try {
    const bill = await checkOut(props.record.recordId, { checkOutDate: checkOutDate.value })
    ElMessage.success(`退房成功，已生成账单 ${bill.billNo}，金额 ${formatMoney(bill.totalAmount)}`)
    emit('success')
    visible.value = false
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.money {
  font-weight: 600;
  color: #f56c6c;
}
.tip {
  margin-top: 12px;
}
</style>
