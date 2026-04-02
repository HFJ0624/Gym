import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 前台部署在同域名子路径 /front/ 下
// 本地开发依旧用 http://localhost:3002/front/ 访问效果最接近线上
export default defineConfig({
  base: '/front/',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 3002,
    strictPort: true,
  },
})

