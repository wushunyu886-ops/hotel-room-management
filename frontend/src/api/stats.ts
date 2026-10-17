import request from './request'
import type { DashboardStats, IncomeTrend, OccupancyStats } from '@/types/checkIn'

/** 首页仪表盘统计 */
export function getDashboardStats() {
  return request.get<any, DashboardStats>('/dashboard/stats')
}

/** 入住率统计（按房型/性别） */
export function getOccupancy(params?: { startDate?: string; endDate?: string }) {
  return request.get<any, OccupancyStats>('/stats/occupancy', { params })
}

/** 每日收入趋势 */
export function getIncome(params?: { startDate?: string; endDate?: string }) {
  return request.get<any, IncomeTrend[]>('/stats/income', { params })
}
