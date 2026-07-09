import { describe, it, expect } from 'vitest'
import { formatRMB, formatDate } from '../format'

describe('formatRMB', () => {
  it('应该正确格式化整数金额', () => {
    expect(formatRMB(100)).toBe('¥100.00')
  })

  it('应该正确格式化小数金额', () => {
    expect(formatRMB(35.5)).toBe('¥35.50')
  })

  it('应该正确格式化千分位金额', () => {
    expect(formatRMB(1234.56)).toBe('¥1,234.56')
  })

  it('应该正确格式化大额金额', () => {
    expect(formatRMB(1000000)).toBe('¥1,000,000.00')
  })

  it('字符串数字应该正常解析', () => {
    expect(formatRMB('99.9')).toBe('¥99.90')
  })

  it('非数字输入应返回 ¥0.00', () => {
    expect(formatRMB('abc')).toBe('¥0.00')
  })

  it('null 输入应返回 ¥0.00', () => {
    expect(formatRMB(null)).toBe('¥0.00')
  })

  it('undefined 输入应返回 ¥0.00', () => {
    expect(formatRMB(undefined)).toBe('¥0.00')
  })

  it('零应该格式化为 ¥0.00', () => {
    expect(formatRMB(0)).toBe('¥0.00')
  })

  it('负数金额应该能格式化', () => {
    expect(formatRMB(-50)).toBe('¥-50.00')
  })
})

describe('formatDate', () => {
  it('应该正确格式化日期字符串', () => {
    const result = formatDate('2026-07-09')
    expect(result).toBe('2026-07-09')
  })

  it('空字符串应返回空', () => {
    expect(formatDate('')).toBe('')
  })

  it('null 应返回空', () => {
    expect(formatDate(null)).toBe('')
  })

  it('undefined 应返回空', () => {
    expect(formatDate(undefined)).toBe('')
  })

  it('ISO 日期格式应正确解析', () => {
    const result = formatDate('2026-01-01T00:00:00Z')
    expect(result).toMatch(/^\d{4}-\d{2}-\d{2}$/)
  })
})
