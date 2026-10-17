import request from './request'
import type { PageResult } from '@/types/common'
import type { RoomType, RoomTypeDTO, RoomTypeQuery } from '@/types/room'

/** 分页查询房间类型 */
export function pageRoomTypes(params: RoomTypeQuery) {
  return request.get<any, PageResult<RoomType>>('/room-types', { params })
}

/** 查询全部启用房型（下拉框用） */
export function listAllRoomTypes() {
  return request.get<any, RoomType[]>('/room-types/all')
}

/** 新增房间类型 */
export function createRoomType(data: RoomTypeDTO) {
  return request.post<any, RoomType>('/room-types', data)
}

/** 修改房间类型 */
export function updateRoomType(id: number, data: RoomTypeDTO) {
  return request.put<any, RoomType>(`/room-types/${id}`, data)
}

/** 删除房间类型 */
export function deleteRoomType(id: number) {
  return request.delete(`/room-types/${id}`)
}
