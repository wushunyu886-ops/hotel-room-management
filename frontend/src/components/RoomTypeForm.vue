<template>
  <el-dialog v-model="visible" :title="title" width="520px" destroy-on-close @closed="resetForm">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="朝向" prop="orientation">
        <el-radio-group v-model="form.orientation">
          <el-radio value="SOUTH">朝南</el-radio>
          <el-radio value="NORTH">朝北</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="类型名称" prop="typeName">
        <el-input v-model="form.typeName" placeholder="如：朝南两人间" maxlength="50" />
      </el-form-item>
      <el-form-item label="床位数" prop="bedCount">
        <el-input-number v-model="form.bedCount" :min="1" :max="10" />
      </el-form-item>
      <el-form-item label="每日住宿费" prop="dailyPrice">
        <el-input-number v-model="form.dailyPrice" :min="0.01" :precision="2" :step="10" />
        <span class="unit">元/天</span>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
      </el-form-item>
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
import type { RoomType, RoomTypeDTO } from '@/types/room'
import type { Orientation } from '@/types/common'

/** 房型新增/编辑弹窗 */
const props = defineProps<{ visible: boolean; row?: RoomType | null }>()
const emit = defineEmits<{ (e: 'update:visible', v: boolean): void; (e: 'saved', data: RoomTypeDTO): void }>()

const visible = ref(props.visible)
watch(
  () => props.visible,
  (v) => {
    visible.value = v
    if (v) {
      // 编辑回显 / 新增重置
      Object.assign(form, {
        orientation: (props.row?.orientation as Orientation) || 'SOUTH',
        typeName: props.row?.typeName || '',
        bedCount: props.row?.bedCount ?? 2,
        dailyPrice: props.row?.dailyPrice ?? 100,
        status: props.row?.status ?? 1
      })
    }
  }
)
watch(visible, (v) => emit('update:visible', v))

const title = props.row ? '编辑房型' : '新增房型'

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive<RoomTypeDTO>({
  orientation: 'SOUTH',
  typeName: '',
  bedCount: 2,
  dailyPrice: 100,
  status: 1
})

const rules: FormRules = {
  orientation: [{ required: true, message: '请选择朝向', trigger: 'change' }],
  typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
  bedCount: [{ required: true, message: '请输入床位数', trigger: 'blur' }],
  dailyPrice: [{ required: true, message: '请输入每日住宿费', trigger: 'blur' }]
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

<style scoped>
.unit {
  margin-left: 8px;
  color: #909399;
}
</style>
