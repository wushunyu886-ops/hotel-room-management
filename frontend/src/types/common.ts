/**
 * 通用类型定义
 */

/** 分页结果 */
export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
}

/** 性别 */
export type Gender = 'MALE' | 'FEMALE'
/** 房间朝向 */
export type Orientation = 'SOUTH' | 'NORTH'
/** 房间状态 */
export type RoomStatus = 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE'
/** 床位状态 */
export type BedStatus = 'FREE' | 'OCCUPIED' | 'MAINTENANCE'
/** 入住状态 */
export type CheckInStatus = 'CHECKED_IN' | 'CHECKED_OUT'
/** 支付状态 */
export type PayStatus = 'UNPAID' | 'PAID'
/** 用户角色 */
export type UserRole = 'ADMIN' | 'RECEPTIONIST'

/** 登录参数 */
export interface LoginParams {
  username: string
  password: string
}

/** 登录结果 */
export interface LoginResult {
  token: string
  userId: number
  username: string
  realName: string
  role: UserRole
}

/** 当前用户信息 */
export interface UserInfo {
  userId: number
  username: string
  realName: string
  role: UserRole
}
