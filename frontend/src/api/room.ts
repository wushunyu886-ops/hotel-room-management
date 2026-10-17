import request from './request'
import type { PageResult } from '@/types/common'
import type { RoomBedVO, RoomDTO, RoomQuery, RoomVO } from '@/types/room'

/** 分页查询房间 */
export function pageRooms(params: RoomQuery) {
  return request.get<any, PageResult<RoomVO>>('/rooms', { params })
}

/** 新增房间（自动生成床位） */
export function createRoom(data: RoomDTO) {
  return request.post<any, RoomVO>('/rooms', data)
}

/** 修改房间 */
export function updateRoom(id: number, data: RoomDTO) {
  return request.put<any, RoomVO>(`/rooms/${id}`, data)
}

/** 删除房间 */
export function deleteRoom(id: number) {
  return request.delete(`/rooms/${id}`)
}

/** 查看某房间所有床位状态 */
export function listRoomBeds(id: number) {
  return request.get<any, RoomBedVO[]>(`/rooms/${id}/beds`)
}
