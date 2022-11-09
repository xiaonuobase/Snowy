import { mapState, mapMutations } from 'vuex'
import hotkeys from 'hotkeys-js'

export default {
	mounted() {
		// 绑定搜索功能快捷键 [ 打开 ]
		hotkeys(this.searchHotkey.open, (event) => {
			event.preventDefault()
			this.searchPanelOpen()
		})
		// 绑定搜索功能快捷键 [ 关闭 ]
		hotkeys(this.searchHotkey.close, (event) => {
			event.preventDefault()
			this.searchPanelClose()
		})
	},
	beforeDestroy() {
		hotkeys.unbind(this.searchHotkey.open)
		hotkeys.unbind(this.searchHotkey.close)
	},
	computed: {
		...mapState('search', {
			searchActive: (state) => state.active,
			searchHotkey: (state) => state.hotkey
		})
	},
	methods: {
		...mapMutations({
			searchToggle: 'search/toggle',
			searchSet: 'search/set'
		}),
		// 接收点击搜索按钮
		handleSearchClick() {
			this.searchToggle()
			if (this.searchActive) {
				setTimeout(() => {
					if (this.$refs.panelSearch) {
						this.$refs.panelSearch.focus()
					}
				}, 300)
			}
		},
		searchPanelOpen() {
			if (!this.searchActive) {
				this.searchSet(true)
				setTimeout(() => {
					if (this.$refs.panelSearch) {
						this.$refs.panelSearch.focus()
					}
				}, 300)
			}
		},
		// 关闭搜索面板
		searchPanelClose() {
			if (this.searchActive) {
				this.searchSet(false)
			}
		}
	}
}
