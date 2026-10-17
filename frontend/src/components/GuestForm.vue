<template>
  <el-dialog v-model="visible" :title="title" width="560px" destroy-on-close @closed="resetForm">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" maxlength="50" />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender">
          <el-radio value="MALE">男</el-radio>
          <el-radio value="FEMALE">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="身份证号" prop="idCard">
        <el-input v-model="form.idCard" placeholder="18 位身份证号" maxlength="18" />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" placeholder="11 位手机号" maxlength="11" />
      </el-form-item>
      <el-form-item label="住址" prop="address">
        <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入住址" maxlength="255" />
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
import type { FormInstance, FormRules } from 'element-plus'
import type { Guest, GuestDTO } from '@/types/guest'
import { idCardRule, phoneRule } from '@/utils/validate'

/** 旅客新增/编辑弹窗（身份证 18 位、手机号 11 位校验与后端一致） */
const props = defineProps<{ visible: boolean; row?: Guest | null }>()
const emit = defineEmits<{ (e: 'update:visible', v: boolean): void; (e: 'saved', data: GuestDTO): void }>()

const visible = ref(props.visible)
const title = props.row ? '编辑旅客' : '旅客登记'
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive<GuestDTO>({
  name: '',
  gender: 'MALE',
  idCard: '',
  phone: '',
  address: ''
})

watch(
  () => props.visible,
  (v) => {
    visible.value = v
    if (v) {
      Object.assign(form, {
        name: props.row?.name || '',
        gender: props.row?.gender || 'MALE',
        idCard: props.row?.idCard || '',
        phone: props.row?.phone || '',
        address: props.row?.address || ''
      })
    }
  }
)
watch(visible, (v) => emit('update:visible', v))

const rules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idCard: [idCardRule(true)],
  phone: [phoneRule()]
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
