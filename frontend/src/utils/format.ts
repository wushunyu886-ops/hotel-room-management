import dayjs from 'dayjs'

/**
 * 通用格式化与字典工具
 */

/** 性别字典 */
export const genderMap: Record<string, string> = { MALE: '男', FEMALE: '女' }
/** 朝向字典 */
export const orientationMap: Record<string, string> = { SOUTH: '朝南', NORTH: '朝北' }
/** 房间状态字典 */
export const roomStatusMap: Record<string, string> = { AVAILABLE: '空闲', OCCUPIED: '已入住', MAINTENANCE: '维修' }
/** 床位状态字典 */
export const bedStatusMap: Record<string, string> = { FREE: '空闲', OCCUPIED: '已占用', MAINTENANCE: '维修' }
/** 入住状态字典 */
export const checkInStatusMap: Record<string, string> = { CHECKED_IN: '在住', CHECKED_OUT: '已退房' }
/** 支付状态字典 */
export const payStatusMap: Record<string, string> = { UNPAID: '未支付', PAID: '已支付' }
/** 角色字典 */
export const roleMap: Record<string, string> = { ADMIN: '管理员', RECEPTIONIST: '前台' }

/** 金额格式化：保留两位小数，加 ¥ 前缀 */
export function formatMoney(value?: number | string | null): string {
  const num = Number(value ?? 0)
  return `¥${num.toFixed(2)}`
}

/** 日期格式化：默认 yyyy-MM-dd */
export function formatDate(value?: string | null, fmt = 'YYYY-MM-DD'): string {
  return value ? dayjs(value).format(fmt) : '-'
}

/** 字典取值兜底 */
export function dictText(map: Record<string, string>, key?: string | null): string {
  return key ? map[key] || key : '-'
}
