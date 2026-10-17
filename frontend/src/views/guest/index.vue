<template>
  <PageContainer title="旅客管理">
    <template #extra>
      <el-button type="primary" :icon="Plus" @click="openForm()">旅客登记</el-button>
    </template>

    <SearchForm :model="query" @search="load" @reset="load">
      <el-form-item label="姓名">
        <el-input v-model="query.name" placeholder="姓名" clearable style="width: 140px" />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="query.idCard" placeholder="身份证号" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="query.phone" placeholder="手机号" clearable style="width: 150px" />
      </el-form-item>
    </SearchForm>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column label="性别" width="80">
        <template #default="{ row }"><StatusTag type="gender" :value="row.gender" /></template>
      </el-table-column>
      <el-table-column prop="idCard" label="身份证号" width="190" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="address" label="住址" show-overflow-tooltip />
      <el-table-column prop="createTime" label="登记时间" width="170" />
      <el-table-column label="操作" width="230" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm(row)">编辑</el-button>
          <el-button link type="info" @click="openDetail(row)">详情</el-button>
          <el-button link type="success" @click="openCheckIn(row)">办理入住</el-button>
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

    <GuestForm v-model:visible="formVisible" :row="editingRow" @saved="handleSaved" />
    <CheckInDialog v-model:visible="checkInVisible" :preset-guest-id="presetGuestId" @success="load" />

    <!-- 旅客详情 -->
    <el-dialog v-model="detailVisible" title="旅客详情" width="520px">
      <el-descriptions v-if="detailRow" :column="1" border>
        <el-descriptions-item label="姓名">{{ detailRow.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <StatusTag type="gender" :value="detailRow.gender" />
        </el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailRow.idCard }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailRow.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="住址">{{ detailRow.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="登记时间">{{ detailRow.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </PageContainer>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import PageContainer from '@/components/PageContainer.vue'
import SearchForm from '@/components/SearchForm.vue'
import StatusTag from '@/components/StatusTag.vue'
import GuestForm from '@/components/GuestForm.vue'
import CheckInDialog from '@/components/CheckInDialog.vue'
import { createGuest, pageGuests, updateGuest } from '@/api/guest'
import type { Guest, GuestDTO, GuestQuery } from '@/types/guest'

/** 旅客管理页 */
const loading = ref(false)
const list = ref<Guest[]>([])
const total = ref(0)
const query = reactive<GuestQuery>({ pageNum: 1, pageSize: 10 })

const formVisible = ref(false)
const editingRow = ref<Guest | null>(null)
const detailVisible = ref(false)
const detailRow = ref<Guest | null>(null)
const checkInVisible = ref(false)
const presetGuestId = ref<number>()

async function load() {
  loading.value = true
  try {
    const res = await pageGuests(query)
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function openForm(row?: Guest) {
  editingRow.value = row || null
  formVisible.value = true
}

function openDetail(row: Guest) {
  detailRow.value = row
  detailVisible.value = true
}

function openCheckIn(row: Guest) {
  presetGuestId.value = row.id
  checkInVisible.value = true
}

async function handleSaved(dto: GuestDTO) {
  if (editingRow.value) {
    await updateGuest(editingRow.value.id, dto)
    ElMessage.success('修改成功')
  } else {
    await createGuest(dto)
    ElMessage.success('登记成功')
  }
  formVisible.value = false
  load()
}

onMounted(load)
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
