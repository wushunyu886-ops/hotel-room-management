import request from './request'
import type { LoginParams, LoginResult } from '@/types/common'

/** 登录 */
export function login(data: LoginParams) {
  return request.post<any, LoginResult>('/auth/login', data)
}
