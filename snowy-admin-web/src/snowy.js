import * as antdvIcons from '@ant-design/icons-vue'
import config from './config'
import tool from './utils/tool'
import { hasPerm } from './utils/permission/index'
import errorHandler from './utils/errorHandler'
import customIcons from './assets/icons/index.js'
import 'highlight.js/styles/atom-one-dark.css'
import 'highlight.js/lib/common'
import hljsVuePlugin from '@highlightjs/vue-plugin'
import STable from './components/Table/index.vue'
import Ellipsis from './components/Ellipsis/index.vue'

export default {
	install(app) {
		// 挂载全局对象
		app.config.globalProperties.$CONFIG = config
		app.config.globalProperties.$TOOL = tool
		app.config.globalProperties.hasPerm = hasPerm

		// 注册常用组件
		app.component('STable', STable)
		app.component('Ellipsis', Ellipsis)

		// 统一注册antdv图标
		for (const icon in antdvIcons) {
			app.component(icon, antdvIcons[icon])
		}
		// 统一注册自定义全局图标
		app.use(customIcons)
		// 注册代码高亮组件 （博客：https://blog.csdn.net/weixin_41897680/article/details/124925222）
		app.use(hljsVuePlugin)

		// 全局代码错误捕捉
		app.config.errorHandler = errorHandler
	}
}
