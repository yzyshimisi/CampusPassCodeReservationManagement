import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {  // 获取请求中带 /api 的请求
        target: 'http://localhost/',  // 后台服务器的域名
        changeOrigin: true,   // 修改源
        rewrite: (path) => path.replace(/^\/api/, "")
      }
    }
  }
})
