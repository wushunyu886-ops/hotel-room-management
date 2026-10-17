import request from './request'
import type { PageResult } from '@/types/common'
import type { BedStatus } from '@/types/common'
import type { BedVO } from '@/types/room'

/** 分页查询床位 */
export function pageBeds(params: { pageNum: number; pageSize: number; roomNo?: string; status?: BedStatus; roomId?: number }) {
  return request.get<any, PageResult<BedVO>>('/beds', { params })
}
