<template>
	<div v-drag class="mobile-nav-button" draggable="false" @click="showMobileNav($event)">
		<appstore-outlined style="font-size: 20px; color: white" />
	</div>
	<a-drawer v-model:visible="visible" :width="210" :closable="false" placement="left">
		<header class="snowy-header-logo mobile-nav">
			<div class="snowy-header-left">
				<div class="logo-bar">
					<img class="logo" :src="sysBaseConfig.SNOWY_SYS_LOGO" />
					<span>{{ sysBaseConfig.SNOWY_SYS_NAME }}</span>
				</div>
			</div>
		</header>
		<a-menu style="width: 208px; margin-left: -24px" mode="inline" @select="onSelect">
			<NavMenu :nav-menus="menu"></NavMenu>
		</a-menu>
	</a-drawer>
</template>

<script>
	import NavMenu from './NavMenu.vue'
	import tool from '@/utils/tool'
	import store from '@/store'

	export default {
		components: {
			NavMenu
		},
		directives: {
			drag(el) {
				const oDiv = el // 当前元素
				let firstTime = ''
				let lastTime = ''
				// 禁止选择网页上的文字
				// document.onselectstart = function() {
				// 	return false;
				// };
				oDiv.onmousedown = function (e) {
					// 鼠标按下，计算当前元素距离可视区的距离
					const disX = e.clientX - oDiv.offsetLeft
					const disY = e.clientY - oDiv.offsetTop
					document.onmousemove = function (e) {
						oDiv.setAttribute('drag-flag', true)
						firstTime = new Date().getTime()
						// 通过事件委托，计算移动的距离
						const l = e.clientX - disX
						const t = e.clientY - disY
						// 移动当前元素
						if (t > 0 && t < document.body.clientHeight - 50) {
							oDiv.style.top = `${t}px`
						}
						if (l > 0 && l < document.body.clientWidth - 50) {
							oDiv.style.left = `${l}px`
						}
					}
					document.onmouseup = function () {
						lastTime = new Date().getTime()
						if (lastTime - firstTime > 200) {
							oDiv.setAttribute('drag-flag', false)
						}
						document.onmousemove = null
						document.onmouseup = null
					}
					// return false不加的话可能导致黏连，就是拖到一个地方时div粘在鼠标上不下来，相当于onmouseup失效
					return false
				}
			}
		},
		data() {
			return {
				visible: false,
				menu: [],
				sysBaseConfig: tool.data.get('SNOWY_SYS_BASE_CONFIG') || store.state.global.sysBaseConfig
			}
		},
		created() {
			const menu = this.$router.getMenu()
			this.menu = this.filterUrl(menu)
		},
		methods: {
			showMobileNav(e) {
				const isdrag = e.currentTarget.getAttribute('drag-flag')
				this.visible = true
				if (isdrag === 'true') {
					return false
				} else {
					this.visible = true
				}
			},
			// 当菜单被选中时
			onSelect(obj) {
				const pathLength = obj.keyPath.length
				const path = obj.keyPath[pathLength - 1]
				this.$router.push({ path })
				this.visible = false
			},
			// 转换外部链接的路由
			filterUrl(map) {
				map.forEach((item, index) => {
					item.meta = item.meta ? item.meta : {}
					// 处理隐藏
					if (item.meta.hidden) {
						map.splice(index, 1)
					}
					// 处理http
					// eslint-disable-next-line eqeqeq
					if (item.meta.type == 'iframe') {
						item.path = `/i/${item.name}`
					}
					// 递归循环
					if (item.children && item.children.length > 0) {
						item.children = this.filterUrl(item.children)
					}
				})
				return map
			}
		}
	}
</script>

<style lang="less" scoped>
	.mobile-nav {
		margin-top: -24px;
		margin-left: -24px;
		margin-right: -24px;
	}
	.mobile-nav-button {
		position: fixed;
		bottom: 10px;
		left: 10px;
		z-index: 10;
		width: 50px;
		height: 50px;
		background: var(--primary-color);
		box-shadow: 0 2px 12px 0 var(--primary-color);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}
</style>
