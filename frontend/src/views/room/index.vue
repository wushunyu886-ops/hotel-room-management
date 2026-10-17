<template>
  <PageContainer title="房间管理">
    <template #extra>
      <el-button type="primary" :icon="Plus" @click="openForm()">新增房间</el-button>
    </template>

    <SearchForm :model="query" @search="load" @reset="load">
      <el-form-item label="房间号">
        <el-input v-model="query.roomNo" placeholder="如：南101" clearable style="width: 140px" />
      </el-form-item>
      <el-form-item label="朝向">
        <el-select v-model="query.orientation" clearable placeholder="全部" style="width: 120px">
          <el-option value="SOUTH" label="朝南" />
          <el-option value="NORTH" label="朝北" />
        </el-select>
      </el-form-item>
      <el-form-item label="房型">
        <el-select v-model="query.roomTypeId" clearable placeholder="全部" style="width: 160px">
          <el-option v-for="t in roomTypes" :key="t.id" :value="t.id" :label="t.typeName" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
          <el-option value="AVAILABLE" label="空闲" />
          <el-option value="OCCUPIED" label="已入住" />
          <el-option value="MAINTENANCE" label="维修" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column prop="roomNo" label="房间号" width="100" />
      <el-table-column label="朝向" width="80">
        <template #default="{ row }"><StatusTag type="orientation" :value="row.orientation" /></template>
      </el-table-column>
      <el-table-column prop="typeName" label="房型" />
      <el-table-column prop="floor" label="楼层" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }"><StatusTag type="room" :value="row.status" /></template>
      </el-table-column>
      <el-table-column label="房间性别" width="100">
        <template #default="{ row }">
          <StatusTag v-if="row.currentGender" type="gender" :value="row.currentGender" />
          <span v-else class="none">不限</span>
        </template>
      </el-table-column>
      <el-table-column label="入住情况" width="110">
        <template #default="{ row }">{{ row.occupiedBeds }}/{{ row.bedCount }} 人</template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm(row)">编辑</el-button>
          <el-button link type="info" @click="openBeds(row)">查看床位</el-button>
          <el-button link :type="row.status === 'MAINTENANCE' ? 'success' : 'warning'"
            @click="toggleStatus(row)">
            {{ row.status === 'MAINTENANCE' ? '启用' : '停用' }}
          </el-button>
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

    <!-- 新增/编辑弹窗 -->
    <RoomForm v-model:visible="formVisible" :row="editingRow" @saved="handleSaved" />
    <!-- 查看床位弹窗 -->
    <el-dialog v-model="bedsVisible" :title="`${bedsRoom?.roomNo} 床位状态`" width="560px" destroy-on-close>
      <BedTable v-if="bedsRoom" :room-id="bedsRoom.id" />
    </el-dialog>
  </PageContainer>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import PageContainer from '@/components/PageContainer.vue'
import SearchForm from '@/components/SearchForm.vue'
import StatusTag from '@/components/StatusTag.vue'
import RoomForm from '@/components/RoomForm.vue'
import BedTable from '@/components/BedTable.vue'
import { createRoom, deleteRoom, pageRooms, updateRoom } from '@/api/room'
import { listAllRoomTypes } from '@/api/roomType'
import type { RoomDTO, RoomQuery, RoomType, RoomVO } from '@/types/room'

/** 房间管理页 */
const loading = ref(false)
const list = ref<RoomVO[]>([])
const total = ref(0)
const roomTypes = ref<RoomType[]>([])
const query = reactive<RoomQuery>({ pageNum: 1, pageSize: 10 })

const formVisible = ref(false)
const editingRow = ref<RoomVO | null>(null)
const bedsVisible = ref(false)
const bedsRoom = ref<RoomVO | null>(null)

async function load() {
  loading.value = true
  try {
    const res = await pageRooms(query)
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function openForm(row?: RoomVO) {
  editingRow.value = row || null
  formVisible.value = true
}

function openBeds(row: RoomVO) {
  bedsRoom.value = row
  bedsVisible.value = true
}

async function handleSaved(dto: RoomDTO) {
  if (editingRow.value) {
    await updateRoom(editingRow.value.id, dto)
    ElMessage.success('修改成功')
  } else {
    await createRoom(dto)
    ElMessage.success('新增成功（床位已按房型自动生成）')
  }
  formVisible.value = false
  load()
}

/** 停用/启用房间 */
async function toggleStatus(row: RoomVO) {
  const maintenance = row.status !== 'MAINTENANCE'
  await ElMessageBox.confirm(
    maintenance ? `确定停用房间「${row.roomNo}」吗？` : `确定启用房间「${row.roomNo}」吗？`,
    '提示',
    { type: 'warning' }
  )
  await updateRoom(row.id, {
    roomNo: row.roomNo,
    roomTypeId: row.roomTypeId,
    floor: row.floor,
    status: maintenance ? 'MAINTENANCE' : 'AVAILABLE'
  })
  ElMessage.success(maintenance ? '已停用' : '已启用')
  load()
}

async function handleDelete(row: RoomVO) {
  await ElMessageBox.confirm(`确定删除房间「${row.roomNo}」及其床位吗？`, '提示', { type: 'warning' })
  await deleteRoom(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(async () => {
  roomTypes.value = await listAllRoomTypes()
  load()
})
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.none {
  color: #909399;
}
</style>
