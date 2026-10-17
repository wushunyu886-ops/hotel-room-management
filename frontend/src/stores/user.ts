import { defineStore } from 'pinia'
import { login as loginApi } from '@/api/auth'
import { getToken, setToken, getUserInfo, setUserInfo, clearToken } from '@/utils/auth'
import type { LoginResult, UserInfo } from '@/types/common'

/**
 * 用户状态：登录态 + 用户信息
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: getUserInfo<UserInfo>()
  }),
  actions: {
    /** 登录：保存 token 与用户信息 */
    async login(username: string, password: string) {
      const res: LoginResult = await loginApi({ username, password })
      this.token = res.token
      this.userInfo = {
        userId: res.userId,
        username: res.username,
        realName: res.realName,
        role: res.role
      }
      setToken(res.token)
      setUserInfo(this.userInfo)
    },
    /** 退出登录：清理本地状态 */
    logout() {
      this.token = ''
      this.userInfo = null
      clearToken()
    }
  }
})
