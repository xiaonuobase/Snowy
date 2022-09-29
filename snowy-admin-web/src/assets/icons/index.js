/*
 ** 全局注册Icon组件
 * 推荐前往https://icones.js.org下载图标的Vue文件，然后放在src/assets/icons文件夹里面
 * 这个网址有118个图标集，包括antd、bootstrap、eleme等累计140456个图标
 */
import { defineAsyncComponent } from 'vue'
const components = import.meta.glob('./**/*.vue') // 异步方式
export default function install(app) {
	for (const [key, value] of Object.entries(components)) {
		const name = key.slice(key.lastIndexOf('/') + 1, key.lastIndexOf('.'))
		app.component(name, defineAsyncComponent(value))
	}
}
