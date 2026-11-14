<template>
  <div v-loading="loading">
    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.label" :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="16" class="mt16">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>房型入住率</template>
          <div ref="pieRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>近 7 日收入趋势</template>
          <div ref="lineRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>房间状态分布</template>
          <div ref="barRef" class="chart" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 列表区 -->
    <el-row :gutter="16" class="mt16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>今日待退房（在住满 1 天）</template>
          <el-table :data="stats?.dueCheckOutList || []" size="small">
            <el-table-column prop="guestName" label="旅客" />
            <el-table-column prop="roomNo" label="房间" width="80" />
            <el-table-column prop="bedNo" label="床号" width="80">
              <template #default="{ row }">{{ row.bedNo }} 号</template>
            </el-table-column>
            <el-table-column prop="stayDays" label="已住天数" width="90" />
            <el-table-column label="性别" width="70">
              <template #default="{ row }"><StatusTag type="gender" :value="row.gender" /></template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>空房列表</template>
          <el-table :data="stats?.availableRoomList || []" size="small">
            <el-table-column prop="roomNo" label="房间号" width="90" />
            <el-table-column label="朝向" width="80">
              <template #default="{ row }"><StatusTag type="orientation" :value="row.orientation" /></template>
            </el-table-column>
            <el-table-column prop="typeName" label="房型" />
            <el-table-column prop="floor" label="楼层" width="70" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats, getIncome, getOccupancy } from '@/api/stats'
import type { DashboardStats } from '@/types/checkIn'
import { formatMoney } from '@/utils/format'
import StatusTag from '@/components/StatusTag.vue'

/** 首页仪表盘：统计卡片 + 3 个 ECharts 图表 + 待退房/空房列表 */
const loading = ref(true)
const stats = ref<DashboardStats | null>(null)

const cards = ref<{ label: string; value: string; color: string }[]>([])

const pieRef = ref<HTMLElement>()
const lineRef = ref<HTMLElement>()
const barRef = ref<HTMLElement>()
let pieChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

onMounted(async () => {
  try {
    const [dash, occ, inc] = await Promise.all([getDashboardStats(), getOccupancy(), getIncome()])
    stats.value = dash
    buildCards(dash)
    buildPie(occ.byRoomType)
    buildLine(inc)
    buildBar(dash)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  pieChart?.dispose()
  lineChart?.dispose()
  barChart?.dispose()
})

function buildCards(d: DashboardStats) {
  cards.value = [
    { label: '今日入住', value: String(d.todayCheckIns), color: '#409eff' },
    { label: '今日退房', value: String(d.todayCheckOuts), color: '#e6a23c' },
    { label: '今日收入', value: formatMoney(d.todayIncome), color: '#67c23a' },
    { label: '在住旅客', value: String(d.inStayGuests), color: '#f56c6c' },
    { label: '空房数', value: `${d.availableRooms}/${d.totalRooms}`, color: '#909399' },
    { label: '入住率', value: `${d.occupancyRate}%`, color: '#409eff' }
  ]
}

/** 房型入住率饼图（图例竖排居右，避免长名称横向重叠） */
function buildPie(byRoomType: { typeName: string; occupiedRooms: number; totalRooms: number }[]) {
  pieChart = echarts.init(pieRef.value as HTMLElement)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}<br/>已入住 {c} 间' },
    legend: {
      orient: 'vertical',
      right: 8,
      top: 'middle',
      itemWidth: 10,
      itemHeight: 10,
      itemGap: 14,
      textStyle: { fontSize: 12, color: '#606266' }
    },
    series: [
      {
        type: 'pie',
        radius: ['42%', '62%'],
        center: ['34%', '50%'], // 饼图左移，给右侧图例留出空间
        avoidLabelOverlap: true,
        label: { show: false }, // 扇区外标签关闭，名称统一由图例展示，避免越界截断
        labelLine: { show: false },
        data: byRoomType.map((t) => ({
          // 名称压缩为「房型 已住/总数」，图例一行放得下
          name: `${t.typeName} ${t.occupiedRooms}/${t.totalRooms}`,
          value: t.occupiedRooms
        }))
      }
    ]
  })
}

/** 近 7 日收入折线图 */
function buildLine(inc: { date: string; income: number }[]) {
  lineChart = echarts.init(lineRef.value as HTMLElement)
  lineChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 16, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: inc.map((i) => i.date.slice(5)) },
    yAxis: { type: 'value' },
    series: [
      {
        name: '收入(元)',
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.15 },
        data: inc.map((i) => i.income)
      }
    ]
  })
}

/** 房间状态柱状图 */
function buildBar(d: DashboardStats) {
  barChart = echarts.init(barRef.value as HTMLElement)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 16, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: ['空闲', '已入住', '维修'] },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        barWidth: 40,
        itemStyle: { color: (p: { dataIndex: number }) => ['#67c23a', '#e6a23c', '#f56c6c'][p.dataIndex] },
        data: [d.availableRooms, d.occupiedRooms, d.maintenanceRooms]
      }
    ]
  })
}
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 24px;
  font-weight: 700;
}
.stat-label {
  color: #909399;
  margin-top: 6px;
  font-size: 13px;
}
.chart {
  height: 280px;
}
.mt16 {
  margin-top: 16px;
}
</style>
