import { describe, it, expect, vi } from 'vitest'

// Mock element-plus 的 ElMessage（必须在最顶部，vitest 会 hoist）
vi.mock('element-plus', () => ({
  ElMessage: {
    error: vi.fn()
  }
}))

describe('request 拦截器', () => {
  it('模块应该可以被导入', async () => {
    // 验证 request 模块可以被正常 import（拦截器注册不会抛出异常）
    const mod = await import('../request')
    expect(mod).toBeDefined()
    expect(mod.default).toBeDefined()
  })

  it('应该导出 axios 实例', async () => {
    const mod = await import('../request')
    // 导出的是 axios 实例，应该有 get/post/put/delete 方法
    expect(typeof mod.default.get).toBe('function')
    expect(typeof mod.default.post).toBe('function')
    expect(typeof mod.default.put).toBe('function')
    expect(typeof mod.default.delete).toBe('function')
  })

  it('baseURL 应为 /api', async () => {
    const mod = await import('../request')
    expect(mod.default.defaults.baseURL).toBe('/api')
  })
})
