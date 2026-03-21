/** @type {import('tailwindcss').Config} */
export default {
  // 配置需要扫描的文件
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  
  // 配置主题
  theme: {
    extend: {
      // Gemini风格的颜色
      colors: {
        'gemini': {
          'primary': '#1a73e8',      // Gemini主色
          'secondary': '#5f6368',     // 次要文字
          'hover': '#e8f0fe',        // 悬停背景
          'border': '#dadce0',       // 边框色
          'bg': '#f8f9fa',          // 背景色
          'ai-bg': '#f0f4ff',       // AI消息背景（浅蓝）
          'user-bg': '#ffffff',     // 用户消息背景
        },
      },
      
      // 自定义动画
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      },
      
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
      
      // 自定义字体
      fontFamily: {
        'sans': ['Google Sans', 'Roboto', 'Arial', 'sans-serif'],
      },
    },
  },
  
  // 插件
  plugins: [],
  
  // 防止与Ant Design Vue冲突
  corePlugins: {
    preflight: false, // 禁用Tailwind的默认样式重置
  },
}





