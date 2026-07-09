/**
 * 格式化金额为 RMB 显示
 * @param {number|string} amount
 * @returns {string} 例: ¥1,234.56
 */
export const formatRMB = (amount) => {
  const num = Number(amount)
  if (isNaN(num)) return '¥0.00'
  return '¥' + num.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

/**
 * 格式化日期
 * @param {string} dateStr
 * @returns {string} 例: 2026-07-09
 */
export const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}
