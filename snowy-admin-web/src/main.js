import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { message, notification } from 'ant-design-vue'

import './style/index.less'
import snowy from './snowy'
import i18n from './locales'
import router from './router'
import App from './App.vue'
import './tailwind.css'

// 全局配置 message 和 notification 的 z-index，确保在锁屏等高 z-index 组件之上
message.config({
	zIndex: 10000
})
notification.config({
	zIndex: 10000
})

const app = createApp(App)
app.use(createPinia())
app.use(i18n)
app.use(snowy)
app.use(router)

// 挂载app
app.mount('#app')
