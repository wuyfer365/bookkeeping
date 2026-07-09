import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    // jsdom 模拟浏览器环境
    environment: 'jsdom',
    // 测试文件匹配模式
    include: ['src/**/*.{test,spec}.{js,ts}'],
    // 全局变量
    globals: true,
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  }
})
