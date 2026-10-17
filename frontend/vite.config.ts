import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// Vite 配置：@ 别名指向 src，开发端口 5173
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    open: true,
    proxy: {
      // 也可改用代理方式访问后端（二选一：代理 或 .env 中的 VITE_API_BASE_URL）
      // '/api': { target: 'http://localhost:8080', changeOrigin: true }
    }
  }
})
