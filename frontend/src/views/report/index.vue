<template>
  <PageContainer title="统计报表">
    <el-form inline class="filter">
      <el-form-item label="统计区间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :clearable="false"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="load">查询</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" class="mt16">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ occupancy?.totalRooms || 0 }}</div>
          <div class="stat-label">房间总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #e6a23c">{{ occupancy?.occupiedRooms || 0 }}</div>
          <div class="stat-label">已入住房间</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #67c23a">{{ occupancy?.occupancyRate ?? 0 }}%</div>
          <div class="stat-label">入住率</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" style="color: #409eff">{{ totalIncome }}</div>
          <div class="stat-label">区间收入</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mt16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>按房型入住率与收入</template>
          <div ref="typeRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>每日收入趋势</template>
          <div ref="incomeRef" class="chart" />
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" class="mt16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>在住旅客性别分布</template>
          <div ref="genderRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>房间状态分布</template>
          <div ref="statusRef" class="chart" />
        </el-card>
      </el-col>
    </el-row>
  </PageContainer>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import { Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import PageContainer from '@/components/PageContainer.vue'
import { getIncome, getOccupancy } from '@/api/stats'
import type { IncomeTrend, OccupancyStats } from '@/types/checkIn'
import { formatMoney } from '@/utils/format'

/** 统计报表页：日期范围 + ECharts 多图表 */
const dateRange = ref<[string, string]>([dayjs().subtract(6, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')])

const occupancy = ref<OccupancyStats | null>(null)
const incomeList = ref<IncomeTrend[]>([])

const typeRef = ref<HTMLElement>()
const incomeRef = ref<HTMLElement>()
const genderRef = ref<HTMLElement>()
const statusRef = ref<HTMLElement>()

const charts: echarts.ECharts[] = []

const totalIncome = computed(() =>
  formatMoney(incomeList.value.reduce((sum, i) => sum + Number(i.income || 0), 0))
)

async function load() {
  const [startDate, endDate] = dateRange.value
  const [occ, inc] = await Promise.all([
    getOccupancy({ startDate, endDate }),
    getIncome({ startDate, endDate })
  ])
  occupancy.value = occ
  incomeList.value = inc
  render()
}

function render() {
  if (!occupancy.value) return
  const byType = occupancy.value.byRoomType || []
  const byGender = occupancy.value.byGender || []

  draw(typeRef.value, {
    tooltip: { trigger: 'axis' },
    legend: { data: ['入住率(%)', '收入(元)'] },
    grid: { left: 60, right: 60, top: 40, bottom: 40 },
    xAxis: { type: 'category', data: byType.map((t) => t.typeName) },
    yAxis: [
      { type: 'value', name: '入住率%', max: 100 },
      { type: 'value', name: '收入(元)' }
    ],
    series: [
      { name: '入住率(%)', type: 'bar', barWidth: 26, data: byType.map((t) => t.occupancyRate), itemStyle: { color: '#409eff' } },
      { name: '收入(元)', type: 'line', yAxisIndex: 1, smooth: true, data: byType.map((t) => t.income), itemStyle: { color: '#67c23a' } }
    ]
  })

  draw(incomeRef.value, {
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: incomeList.value.map((i) => i.date.slice(5)) },
    yAxis: { type: 'value' },
    series: [
      {
        name: '收入(元)',
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.15 },
        data: incomeList.value.map((i) => i.income)
      }
    ]
  })

  draw(genderRef.value, {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['40%', '65%'],
        data: byGender.map((g) => ({ name: g.gender === 'MALE' ? '男' : '女', value: g.count }))
      }
    ]
  })

  const occ = occupancy.value
  draw(statusRef.value, {
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: ['空闲', '已入住', '维修'] },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        barWidth: 40,
        itemStyle: { color: (p: { dataIndex: number }) => ['#67c23a', '#e6a23c', '#f56c6c'][p.dataIndex] },
        data: [occ.availableRooms, occ.occupiedRooms, occ.maintenanceRooms]
      }
    ]
  })
}

/** 通用绘图：先清理再渲染 */
function draw(el: HTMLElement | undefined, option: echarts.EChartsOption) {
  if (!el) return
  const chart = echarts.init(el)
  chart.setOption(option)
  charts.push(chart)
}

onMounted(load)
onUnmounted(() => charts.forEach((c) => c.dispose()))
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 26px;
  font-weight: 700;
}
.stat-label {
  color: #909399;
  margin-top: 6px;
  font-size: 13px;
}
.chart {
  height: 300px;
}
.mt16 {
  margin-top: 16px;
}
</style>
