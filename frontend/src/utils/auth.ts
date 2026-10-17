/**
 * 登录态本地存取工具
 */
const TOKEN_KEY = 'hotel_token'
const USER_INFO_KEY = 'hotel_user_info'

export function getToken(): string {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_INFO_KEY)
}

export function getUserInfo<T>(): T | null {
  const raw = localStorage.getItem(USER_INFO_KEY)
  return raw ? (JSON.parse(raw) as T) : null
}

export function setUserInfo<T>(info: T): void {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
}
