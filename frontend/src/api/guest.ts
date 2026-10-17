import request from './request'
import type { PageResult } from '@/types/common'
import type { Guest, GuestDTO, GuestQuery } from '@/types/guest'

/** 分页查询旅客 */
export function pageGuests(params: GuestQuery) {
  return request.get<any, PageResult<Guest>>('/guests', { params })
}

/** 旅客登记 */
export function createGuest(data: GuestDTO) {
  return request.post<any, Guest>('/guests', data)
}

/** 修改旅客 */
export function updateGuest(id: number, data: GuestDTO) {
  return request.put<any, Guest>(`/guests/${id}`, data)
}

/** 旅客详情 */
export function getGuest(id: number) {
  return request.get<any, Guest>(`/guests/${id}`)
}
