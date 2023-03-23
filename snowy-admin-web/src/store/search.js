import '@/utils/objects'
import { defineStore } from 'pinia'

export const searchStore = defineStore({
	id: 'search',
	state: () => ({
		active: false,
		hotkey: {
			open: 's',
			close: 'esc'
		},
		pool: []
	}),
	getters: {},
	actions: {
		toggleActive() {
			this.active = !this.active
		},
		setActive(active) {
			this.active = active
		},
		init(menu) {
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
					if ('menu' === m.meta.type) {
						if (m.children) {
							push(m.children)
						} else if (m.children === null) {
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
			this.pool = pool
		}
	}
})
