<template>
  <el-dialog v-model="visible" title="办理入住" width="640px" destroy-on-close @closed="resetForm">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="旅客" prop="guestId">
        <el-select
          v-model="form.guestId"
          filterable
          placeholder="输入姓名/身份证搜索旅客"
          style="width: 100%"
          @change="loadBeds"
        >
          <el-option v-for="g in guestOptions" :key="g.id" :value="g.id"
            :label="`${g.name}（${genderText(g.gender)} / ${g.idCard}）`" />
        </el-select>
      </el-form-item>
      <el-form-item label="入住日期" prop="checkInDate">
        <el-date-picker v-model="form.checkInDate" type="date" value-format="YYYY-MM-DD"
          :disabled-date="(d: Date) => dayjs(d).isAfter(dayjs(), 'day')" style="width: 100%" />
      </el-form-item>
      <el-form-item label="朝向筛选">
        <el-select v-model="form.orientation" clearable placeholder="不限朝向" style="width: 100%" @change="loadBeds">
          <el-option value="SOUTH" label="朝南" />
          <el-option value="NORTH" label="朝北" />
        </el-select>
      </el-form-item>
      <el-form-item label="房型筛选">
        <el-select v-model="form.roomTypeId" clearable placeholder="不限房型" style="width: 100%" @change="loadBeds">
          <el-option v-for="t in roomTypes" :key="t.id" :value="t.id"
            :label="`${t.typeName}（${formatMoney(t.dailyPrice)}/天）`" />
        </el-select>
      </el-form-item>
      <el-form-item label="选择床位" prop="bedId">
        <el-select v-model="form.bedId" clearable filterable placeholder="选择床位或使用自动分配" style="width: 100%">
          <el-option v-for="b in bedOptions" :key="b.bedId" :value="b.bedId"
            :label="`${b.roomNo}-${b.bedNo}号床（${b.typeName} / ${orientationText(b.orientation)} / ${formatMoney(b.dailyPrice)}天）`" />
        </el-select>
        <div class="tip">
          <el-alert v-if="!bedOptions.length" type="info" :closable="false"
            title="当前筛选条件下暂无可用床位，可点击下方“自动分配”或调整筛选" />
          <el-alert v-else type="success" :closable="false"
            :title="`共 ${bedOptions.length} 张可用床位（同性别房间优先，空房间不限性别）`" />
        </div>
      </el-form-item>
      <el-form-item v-if="selectedBed" label="预计金额">
        <span class="amount">{{ formatMoney(selectedBed.dailyPrice) }} / 天</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="success" :loading="submitting" @click="submit('auto')">自动分配</el-button>
      <el-button type="primary" :loading="submitting" @click="submit('manual')">确定入住</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import dayjs from 'dayjs'
import { pageGuests } from '@/api/guest'
import { listAllRoomTypes } from '@/api/roomType'
import { createCheckIn, listAvailableBeds } from '@/api/checkIn'
import type { Guest } from '@/types/guest'
import type { RoomType, AvailableBed } from '@/types/room'
import type { Gender, Orientation } from '@/types/common'
import { formatMoney, genderMap, orientationMap } from '@/utils/format'

/** 办理入住弹窗：选旅客 → 筛选 → 选床位 / 自动分配 */
const props = defineProps<{ visible: boolean; presetGuestId?: number }>()
const emit = defineEmits<{ (e: 'update:visible', v: boolean): void; (e: 'success'): void }>()

const visible = ref(props.visible)
watch(visible, (v) => emit('update:visible', v))

const formRef = ref<FormInstance>()
const submitting = ref(false)
const guestOptions = ref<Guest[]>([])
const roomTypes = ref<RoomType[]>([])
const bedOptions = ref<AvailableBed[]>([])

const form = reactive<{ guestId?: number; checkInDate: string; orientation?: Orientation; roomTypeId?: number; bedId?: number }>({
  guestId: undefined,
  checkInDate: dayjs().format('YYYY-MM-DD'),
  orientation: undefined,
  roomTypeId: undefined,
  bedId: undefined
})

const rules: FormRules = {
  guestId: [{ required: true, message: '请选择旅客', trigger: 'change' }],
  checkInDate: [{ required: true, message: '请选择入住日期', trigger: 'change' }]
}

const selectedBed = computed(() => bedOptions.value.find((b) => b.bedId === form.bedId) || null)

const genderText = (g: Gender) => genderMap[g]
const orientationText = (o: Orientation) => orientationMap[o]

watch(
  () => props.visible,
  async (v) => {
    visible.value = v
    if (v) {
      form.checkInDate = dayjs().format('YYYY-MM-DD')
      form.orientation = undefined
      form.roomTypeId = undefined
      form.bedId = undefined
      if (props.presetGuestId) {
        form.guestId = props.presetGuestId
      }
      if (!guestOptions.value.length) {
        const res = await pageGuests({ pageNum: 1, pageSize: 200 })
        guestOptions.value = res.list
      }
      if (!roomTypes.value.length) {
        roomTypes.value = await listAllRoomTypes()
      }
      loadBeds()
    }
  }
)

/** 依据旅客性别与筛选条件加载可用床位（空房间优先/同性别优先由后端排序） */
async function loadBeds() {
  bedOptions.value = []
  form.bedId = undefined
  if (!form.guestId) return
  const guest = guestOptions.value.find((g) => g.id === form.guestId)
  if (!guest) return
  bedOptions.value = await listAvailableBeds({
    gender: guest.gender,
    orientation: form.orientation,
    roomTypeId: form.roomTypeId
  })
}

async function submit(mode: 'auto' | 'manual') {
  await formRef.value?.validate()
  if (mode === 'manual' && !form.bedId) {
    ElMessage.warning('请选择床位，或点击“自动分配”由系统推荐')
    return
  }
  submitting.value = true
  try {
    await createCheckIn({
      guestId: form.guestId as number,
      bedId: mode === 'manual' ? form.bedId : undefined,
      orientation: form.orientation,
      roomTypeId: form.roomTypeId,
      checkInDate: form.checkInDate
    })
    ElMessage.success('入住成功')
    emit('success')
    visible.value = false
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  formRef.value?.resetFields()
}
</script>

<style scoped>
.tip {
  margin-top: 6px;
  width: 100%;
}
.amount {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}
</style>
