import '@/utils/objects'

export default {
	namespaced: true,
	state: {
		// 搜索面板激活状态
		active: false,
		// 快捷键
		hotkey: {
			open: 's',
			close: 'esc'
		},
		// 所有可以搜索的页面
		pool: []
	},
	mutations: {
		/**
		 * @description 切换激活状态
		 * @param {Object} state state
		 */
		toggle(state) {
			state.active = !state.active
		},
		/**
		 * @description 设置激活模式
		 * @param {Object} state state
		 * @param {Boolean} active active
		 */
		set(state, active) {
			state.active = active
		},
		/**
		 * @description 初始化
		 * @param {Object} state state
		 * @param {Array} menu menu
		 */
		init(state, menu) {
			const pool = []
			const getFullName = function (meta) {
				if (meta.breadcrumb) {
					let list = []
					meta.breadcrumb.forEach((item) => {
						list.push(item.meta.title)
					})
					return list.join(' / ')
				}
				return meta.title
			}
			const push = function (menu) {
				menu.forEach((m) => {
					if ('menu' == m.meta.type) {
						if (m.children) {
							push(m.children)
						} else {
							pool.push({
								icon: m.meta.icon,
								path: m.path,
								fullPath: m.path,
								name: m.meta.title,
								fullName: getFullName(m.meta),
								namePinyin: m.meta.title.toPinyin(),
								namePinyinFirst: m.meta.title.toPinyin(true)
							})
						}
					}
				})
			}
			push(menu)
			state.pool = pool
		}
	}
}
