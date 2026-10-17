import request from './request'
import type { PageResult } from '@/types/common'
import type { BillQuery, BillVO } from '@/types/checkIn'

/** 分页查询账单 */
export function pageBills(params: BillQuery) {
  return request.get<any, PageResult<BillVO>>('/bills', { params })
}

/** 账单详情 */
export function getBill(id: number) {
  return request.get<any, BillVO>(`/bills/${id}`)
}

/** 账单支付 */
export function payBill(id: number) {
  return request.post<any, BillVO>(`/bills/${id}/pay`)
}
