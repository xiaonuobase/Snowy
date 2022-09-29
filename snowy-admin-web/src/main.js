import { createApp } from 'vue'
import Antd from 'ant-design-vue'
import './style/index.less'
import snowy from './snowy'
import i18n from './locales'
import router from './router'
import store from './store'
import App from './App.vue'
import './tailwind.css'

const app = createApp(App)
app.use(store)
app.use(router)
app.use(Antd)
app.use(i18n)
app.use(snowy)

// 挂载app
app.mount('#app')
