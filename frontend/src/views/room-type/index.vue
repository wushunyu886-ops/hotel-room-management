<template>
  <PageContainer title="房型管理">
    <template #extra>
      <el-button type="primary" :icon="Plus" @click="openForm()">新增房型</el-button>
    </template>

    <SearchForm :model="query" @search="load" @reset="load">
      <el-form-item label="朝向">
        <el-select v-model="query.orientation" clearable placeholder="全部" style="width: 140px">
          <el-option value="SOUTH" label="朝南" />
          <el-option value="NORTH" label="朝北" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型名称">
        <el-input v-model="query.typeName" placeholder="类型名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
          <el-option :value="1" label="启用" />
          <el-option :value="0" label="停用" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="朝向" width="90">
        <template #default="{ row }"><StatusTag type="orientation" :value="row.orientation" /></template>
      </el-table-column>
      <el-table-column prop="typeName" label="类型名称" />
      <el-table-column prop="bedCount" label="床位数" width="90" />
      <el-table-column prop="dailyPrice" label="每日住宿费" width="130">
        <template #default="{ row }">{{ formatMoney(row.dailyPrice) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <RoomTypeForm v-model:visible="formVisible" :row="editingRow" @saved="handleSaved" />
  </PageContainer>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import PageContainer from '@/components/PageContainer.vue'
import SearchForm from '@/components/SearchForm.vue'
import StatusTag from '@/components/StatusTag.vue'
import RoomTypeForm from '@/components/RoomTypeForm.vue'
import { createRoomType, deleteRoomType, pageRoomTypes, updateRoomType } from '@/api/roomType'
import type { RoomType, RoomTypeDTO, RoomTypeQuery } from '@/types/room'
import { formatMoney } from '@/utils/format'

/** 房型管理页 */
const loading = ref(false)
const list = ref<RoomType[]>([])
const total = ref(0)
const query = reactive<RoomTypeQuery>({ pageNum: 1, pageSize: 10 })

const formVisible = ref(false)
const editingRow = ref<RoomType | null>(null)

async function load() {
  loading.value = true
  try {
    const res = await pageRoomTypes(query)
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function openForm(row?: RoomType) {
  editingRow.value = row || null
  formVisible.value = true
}

async function handleSaved(dto: RoomTypeDTO) {
  if (editingRow.value) {
    await updateRoomType(editingRow.value.id, dto)
    ElMessage.success('修改成功')
  } else {
    await createRoomType(dto)
    ElMessage.success('新增成功')
  }
  formVisible.value = false
  load()
}

async function handleDelete(row: RoomType) {
  await ElMessageBox.confirm(`确定删除房型「${row.typeName}」吗？`, '提示', { type: 'warning' })
  await deleteRoomType(row.id)
  ElMessage.success('删除成功')
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
