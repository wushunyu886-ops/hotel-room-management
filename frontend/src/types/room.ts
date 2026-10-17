import type { BedStatus, Gender, Orientation, RoomStatus } from './common'

/** 房间类型 */
export interface RoomType {
  id: number
  orientation: Orientation
  typeName: string
  bedCount: number
  dailyPrice: number
  status: number
  createTime?: string
}

/** 房间类型查询参数 */
export interface RoomTypeQuery {
  pageNum: number
  pageSize: number
  orientation?: Orientation
  typeName?: string
  status?: number
}

/** 房间类型保存参数 */
export interface RoomTypeDTO {
  orientation: Orientation
  typeName: string
  bedCount: number
  dailyPrice: number
  status: number
}

/** 房间（列表 VO） */
export interface RoomVO {
  id: number
  roomNo: string
  roomTypeId: number
  orientation: Orientation
  floor: number
  status: RoomStatus
  currentGender: Gender | null
  version: number
  typeName: string
  bedCount: number
  occupiedBeds: number
}

/** 房间查询参数 */
export interface RoomQuery {
  pageNum: number
  pageSize: number
  roomNo?: string
  orientation?: Orientation
  roomTypeId?: number
  status?: RoomStatus
}

/** 房间保存参数 */
export interface RoomDTO {
  roomNo: string
  roomTypeId: number
  floor?: number
  status?: RoomStatus
}

/** 床位（列表 VO） */
export interface BedVO {
  bedId: number
  bedNo: number
  bedStatus: BedStatus
  roomId: number
  roomNo: string
  orientation: Orientation
  typeName: string
  currentGender: Gender | null
  guestName: string | null
}

/** 房间床位状态 */
export interface RoomBedVO {
  bedId: number
  bedNo: number
  bedStatus: BedStatus
  guestName: string | null
  checkInDate: string | null
}

/** 可用床位 */
export interface AvailableBed {
  bedId: number
  bedNo: number
  roomId: number
  roomNo: string
  floor: number
  orientation: Orientation
  typeName: string
  dailyPrice: number
  currentGender: Gender | null
  bedStatus: BedStatus
}
