import type { Gender } from './common'

/** 旅客 */
export interface Guest {
  id: number
  name: string
  idCard: string
  gender: Gender
  phone: string
  address: string
  createTime?: string
  updateTime?: string
}

/** 旅客查询参数 */
export interface GuestQuery {
  pageNum: number
  pageSize: number
  name?: string
  idCard?: string
  phone?: string
}

/** 旅客保存参数 */
export interface GuestDTO {
  name: string
  idCard: string
  gender: Gender
  phone?: string
  address?: string
}
