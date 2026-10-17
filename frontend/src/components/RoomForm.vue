<template>
  <el-dialog v-model="visible" :title="title" width="520px" destroy-on-close @closed="resetForm">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="房间号" prop="roomNo">
        <el-input v-model="form.roomNo" placeholder="如：南101" maxlength="20" />
      </el-form-item>
      <el-form-item label="房间类型" prop="roomTypeId">
        <el-select v-model="form.roomTypeId" placeholder="请选择房型" style="width: 100%">
          <el-option
            v-for="t in roomTypes"
            :key="t.id"
            :value="t.id"
            :label="`${t.typeName}（${t.bedCount} 床，${formatMoney(t.dailyPrice)}/天）`"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="楼层" prop="floor">
        <el-input-number v-model="form.floor" :min="0" :max="30" />
      </el-form-item>
      <el-form-item v-if="isEdit" label="状态" prop="status">
        <el-select v-model="form.status" style="width: 100%">
          <el-option value="AVAILABLE" label="空闲" />
          <el-option value="MAINTENANCE" label="维修" />
        </el-select>
      </el-form-item>
      <el-alert v-if="isEdit" type="info" :closable="false"
        title="修改房型将自动同步床位数量；有旅客在住时不能停用房间" />
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { listAllRoomTypes } from '@/api/roomType'
import type { RoomType, RoomDTO, RoomVO } from '@/types/room'
import { formatMoney } from '@/utils/format'

/** 房间新增/编辑弹窗 */
const props = defineProps<{ visible: boolean; row?: RoomVO | null }>()
const emit = defineEmits<{ (e: 'update:visible', v: boolean): void; (e: 'saved', data: RoomDTO): void }>()

const visible = ref(props.visible)
const isEdit = !!props.row
const title = props.row ? '编辑房间' : '新增房间'
const roomTypes = ref<RoomType[]>([])
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive<RoomDTO>({
  roomNo: '',
  roomTypeId: undefined as unknown as number,
  floor: 1,
  status: 'AVAILABLE'
})

watch(
  () => props.visible,
  async (v) => {
    visible.value = v
    if (v) {
      if (!roomTypes.value.length) {
        roomTypes.value = await listAllRoomTypes()
      }
      if (props.row) {
        Object.assign(form, {
          roomNo: props.row.roomNo,
          roomTypeId: props.row.roomTypeId,
          floor: props.row.floor,
          status: props.row.status === 'MAINTENANCE' ? 'MAINTENANCE' : 'AVAILABLE'
        })
      } else {
        Object.assign(form, { roomNo: '', roomTypeId: undefined, floor: 1, status: 'AVAILABLE' })
      }
    }
  }
)
watch(visible, (v) => emit('update:visible', v))

const rules: FormRules = {
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  roomTypeId: [{ required: true, message: '请选择房间类型', trigger: 'change' }]
}

async function submit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    emit('saved', { ...form })
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  formRef.value?.resetFields()
}
</script>
