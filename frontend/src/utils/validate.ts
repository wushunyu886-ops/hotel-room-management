/**
 * 表单校验工具：身份证号（18 位 + 校验位）、手机号（11 位）
 */

const WEIGHT = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
const CHECK_CODE = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']

/** 身份证号校验（18 位，含加权校验位算法） */
export function isValidIdCard(value: string): boolean {
  const card = (value || '').trim().toUpperCase()
  if (!/^\d{17}[\dX]$/.test(card)) {
    return false
  }
  let sum = 0
  for (let i = 0; i < 17; i++) {
    sum += Number(card[i]) * WEIGHT[i]
  }
  return CHECK_CODE[sum % 11] === card[17]
}

/** 手机号校验（11 位，1 开头） */
export function isValidPhone(value: string): boolean {
  return /^1\d{10}$/.test(value || '')
}

/** Element Plus 身份证校验规则（可为空则通过） */
export function idCardRule(required = true) {
  return {
    required,
    message: '请输入正确的 18 位身份证号',
    trigger: 'blur',
    validator: (_rule: unknown, value: string, callback: (err?: Error) => void) => {
      if (!value) {
        return required ? callback(new Error('请输入身份证号')) : callback()
      }
      if (!isValidIdCard(value)) {
        return callback(new Error('身份证号格式不正确'))
      }
      callback()
    }
  }
}

/** Element Plus 手机号校验规则（非必填，填了必须合法） */
export function phoneRule() {
  return {
    message: '请输入正确的 11 位手机号',
    trigger: 'blur',
    validator: (_rule: unknown, value: string, callback: (err?: Error) => void) => {
      if (!value) {
        return callback()
      }
      if (!isValidPhone(value)) {
        return callback(new Error('手机号格式不正确（11 位）'))
      }
      callback()
    }
  }
}
