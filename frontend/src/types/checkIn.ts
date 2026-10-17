import type {
  BedStatus,
  CheckInStatus,
  Gender,
  Orientation,
  PayStatus,
  RoomStatus
} from './common'

/** 入住记录 VO */
export interface CheckInRecordVO {
  recordId: number
  guestId: number
  guestName: string
  idCard: string
  gender: Gender
  roomId: number
  roomNo: string
  orientation: Orientation
  bedId: number
  bedNo: number
  typeName: string
  checkInDate: string
  checkOutDate: string | null
  status: CheckInStatus
  dailyPriceSnapshot: number
  stayDays: number
}

/** 入住记录查询参数 */
export interface CheckInQuery {
  pageNum: number
  pageSize: number
  status?: CheckInStatus
  guestName?: string
  roomNo?: string
  startDate?: string
  endDate?: string
}

/** 办理入住参数 */
export interface CheckInDTO {
  guestId: number
  bedId?: number
  roomId?: number
  orientation?: Orientation
  roomTypeId?: number
  checkInDate: string
}

/** 账单 VO */
export interface BillVO {
  id: number
  billNo: string
  checkInRecordId: number
  guestId: number
  guestName: string
  idCard: string
  roomNo: string
  bedNo: number
  days: number
  dailyPrice: number
  totalAmount: number
  payStatus: PayStatus
  payTime: string | null
  createTime: string
  checkInDate: string
  checkOutDate: string
}

/** 账单查询参数 */
export interface BillQuery {
  pageNum: number
  pageSize: number
  billNo?: string
  guestName?: string
  payStatus?: PayStatus
  startDate?: string
  endDate?: string
}

/** 仪表盘统计 */
export interface DashboardStats {
  todayCheckIns: number
  todayCheckOuts: number
  todayIncome: number
  totalRooms: number
  availableRooms: number
  occupiedRooms: number
  maintenanceRooms: number
  inStayGuests: number
  occupancyRate: number
  dueCheckOutList: StayGuestVO[]
  availableRoomList: AvailableRoomItem[]
}

/** 待退房旅客 */
export interface StayGuestVO {
  recordId: number
  guestId: number
  guestName: string
  idCard: string
  gender: Gender
  roomNo: string
  bedNo: number
  typeName: string
  checkInDate: string
  dailyPrice: number
  stayDays: number
}

/** 空房信息 */
export interface AvailableRoomItem {
  roomId: number
  roomNo: string
  orientation: Orientation
  floor: number
  typeName: string
  bedCount: number
  status: RoomStatus
}

/** 入住率统计 */
export interface OccupancyStats {
  totalRooms: number
  occupiedRooms: number
  availableRooms: number
  maintenanceRooms: number
  occupancyRate: number
  byRoomType: RoomTypeOccupancy[]
  byGender: GenderCount[]
}

/** 房型入住率 */
export interface RoomTypeOccupancy {
  roomTypeId: number
  typeName: string
  orientation: Orientation
  totalRooms: number
  occupiedRooms: number
  occupancyRate: number
  income: number
}

/** 性别统计 */
export interface GenderCount {
  gender: Gender
  count: number
}

/** 每日收入趋势 */
export interface IncomeTrend {
  date: string
  income: number
}
