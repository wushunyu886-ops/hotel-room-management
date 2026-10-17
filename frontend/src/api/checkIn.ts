import request from './request'
import type { PageResult } from '@/types/common'
import type { AvailableBed } from '@/types/room'
import type { CheckInDTO, CheckInQuery, CheckInRecordVO } from '@/types/checkIn'
import type { BillVO } from '@/types/checkIn'
import type { Gender, Orientation } from '@/types/common'

/** 办理入住 */
export function createCheckIn(data: CheckInDTO) {
  return request.post<any, CheckInRecordVO>('/check-ins', data)
}

/** 分页查询入住记录 */
export function pageCheckIns(params: CheckInQuery) {
  return request.get<any, PageResult<CheckInRecordVO>>('/check-ins', { params })
}

/** 查询可用床位（入住推荐/选择） */
export function listAvailableBeds(params: { gender: Gender; orientation?: Orientation; roomTypeId?: number; roomId?: number }) {
  return request.get<any, AvailableBed[]>('/check-ins/available-beds', { params })
}

/** 办理退房，返回生成的账单 */
export function checkOut(id: number, data: { checkOutDate: string }) {
  return request.post<any, BillVO>(`/check-ins/${id}/check-out`, data)
}
