<template>
	<div class="search panel-item" @click="searchPanelOpen">
		<search-outlined />
	</div>
	<xn-form-container
		title="搜索"
		:visible="searchActive"
		:closable="false"
		:footer="null"
		:width="600"
		destroyOnClose
		dialogClass="searchModal"
		:bodyStyle="{ maxHeight: '520px', overflow: 'auto', padding: '14px' }"
		@close="searchPanelClose"
	>
		<div
			@keyup.up="handleKeyUp"
			@keyup.down="handleKeyDown"
			@keyup.enter="handleKeyEnter"
			@click.self="handlePanelClick"
		>
			<a-input
				ref="inputRef"
				v-model="searchText"
				class="search-box xn-wd"
				allowClear
				placeholder="搜索页面（支持拼音检索）"
				@change="querySearch"
			>
				<template #prefix>
					<search-outlined />
				</template>
			</a-input>
			<a-card
				:body-style="{ padding: '0 0' }"
				hoverable
				@mouseenter="onCardIn"
				@mouseleave="onCardOut"
				@keypress.up="handleKeyUp"
				@keypress.down="handleKeyDown"
				class="xn-mn10p0"

			>
				<div ref="cardListRef" class="search-card beauty-scroll">
					<a-list size="small" :data-source="resultsList">
						<template #renderItem="{ item, index }">
							<a-list-item
								@click="handleSelect(item.fullPath)"
								@mouseover="onCardItemHover(index)"
								:class="{ active: index === cardIndex },'xn-pr10'"
							>
								<template #actions>
									<a>
										<enter-outlined />
									</a>
								</template>
								<a-list-item-meta :description="item.fullName">
									<template #title>
										<a>{{ item.name }}</a>
									</template>
									<template #avatar>
										<a-avatar style="color: var(--text-color); background-color: transparent" :type="item.icon">
											<template #icon>
												<component :is="item.icon" />
											</template>
										</a-avatar>
									</template>
								</a-list-item-meta>
							</a-list-item>
						</template>
					</a-list>
				</div>
			</a-card>
			<div class="search-tips">
				<span class="key">S</span>
				<span class="tips">打开搜索面板</span>

				<span class="key">
					<arrow-up-outlined />
				</span>
				<span class="key">
					<arrow-down-outlined />
				</span>
				<span class="tips">选择</span>

				<span class="key">
					<enter-outlined />
				</span>
				<span class="tips">确认</span>

				<span class="key left">Esc</span>
				<span class="tips">关闭</span>
			</div>
		</div>
	</xn-form-container>
</template>

<script setup>
	import Fuse from 'fuse.js'
	import { searchStore } from '@/store'
	import { useRouter, useRoute } from 'vue-router'
	import hotkeys from 'hotkeys-js'

	const route = useRoute()
	const router = useRouter()
	const searchText = ref('')
	const cardIndex = ref(0)
	const results = ref([])
	const search = searchStore()
	const inputRef = ref()
	const cardListRef = ref()
	const setActive = search.setActive
	const toggleActive = search.toggleActive
	const pool = computed(() => {
		return search.pool
	})
	const searchActive = computed(() => {
		return search.active
	})
	const searchHotkey = computed(() => {
		return search.hotkey
	})
	const mixinSearch = computed(() => {
		return mixinSearch
	})
	// 这份数据是展示在搜索面板下面的
	const resultsList = computed(() => {
		return results.value.length === 0 || searchText.value === '' ? pool.value : results.value
	})
	// 根据 pool 更新 fuse 实例
	const fuse = computed(() => {
		return new Fuse(pool.value, {
			shouldSort: true, // 按分数对结果列表进行排序
			threshold: 0.6, // 什么时候放弃
			location: 0, // 大致位置
			distance: 100, // 接近程度
			minMatchCharLength: 1, // 匹配长度
			keys: ['name', 'namePinyin', 'namePinyinFirst']
		})
	})
	// 过滤选项 这个方法在每次输入框的值发生变化时会触发
	const querySearch = (e) => {
		let queryString = e.target.value || ''
		const result = queryString && fuse.value.search(queryString).map((e) => e.item)
		searchText.value = queryString
		results.value = result
	}
	// 聚焦输入框
	const focus = () => {
		searchText.value = ''
		setTimeout(() => {
			if (inputRef.value) {
				inputRef.value.focus()
			}
			// 还原
			searchText.value = ''
			results.value = []
		}, 300)
	}
	const handleKeyEnter = () => {
		let idx = cardIndex.value
		if (resultsList.value[idx]) {
			handleSelect(resultsList.value[idx].fullPath)
		}
	}
	const handleKeyUp = () => {
		handleKeyUpOrDown(true)
	}
	const handleKeyDown = () => {
		handleKeyUpOrDown(false)
	}
	const handleKeyUpOrDown = (up) => {
		let len = resultsList.value.length - 1
		let idx = cardIndex.value
		if (up) {
			// 上
			if (idx > 0) {
				idx--
			} else {
				idx = len
			}
		} else {
			// 下
			if (idx < len) {
				idx++
			} else {
				idx = 0
			}
		}
		cardIndex.value = idx
		if (cardListRef.value.getElementsByClassName('ant-list-item')[idx]) {
			cardListRef.value.scrollTop = cardListRef.value.getElementsByClassName('ant-list-item')[idx].offsetTop
		} else {
			cardListRef.value.scrollTop = 0
		}
	}
	const onCardIn = () => {
		inputRef.value.activated = false
		inputRef.value.blur()
	}
	const onCardOut = () => {
		cardIndex.value = -1
	}
	const onCardItemHover = (index) => {
		cardIndex.value = index
	}
	// 接收用户在下拉菜单中选中事件
	const handleSelect = (path) => {
		// 如果用户选择的就是当前页面 就直接关闭搜索面板
		if (path === route.path) {
			searchPanelClose()
			return
		}
		router.push({ path })
		searchPanelClose()
	}
	// 接收用户点击空白区域的关闭
	const handlePanelClick = (e) => {
		if ('INPUT' !== e.target.tagName) {
			searchPanelClose()
		}
	}
	// 打开搜索面板
	const searchPanelOpen = () => {
		if (!searchActive.value) {
			setActive(true)
			setTimeout(() => {
				if (inputRef.value) {
					inputRef.value.focus()
				}
			}, 300)
		}
	}
	// 关闭搜索面板
	const searchPanelClose = () => {
		if (!searchActive.value) {
			return false
		}
		setActive(false)
		results.value = []
		if (inputRef.value.activated) {
			inputRef.value.activated = false
		}
	}
	onMounted(() => {
		// 绑定搜索功能快捷键 [ 打开 ]
		hotkeys(searchHotkey.value.open, (event) => {
			event.preventDefault()
			searchPanelOpen()
		})
		// 绑定搜索功能快捷键 [ 关闭 ]
		hotkeys(searchHotkey.value.close, (event) => {
			event.preventDefault()
			searchPanelClose()
		})
	})
	// 挂载
	hotkeys.unbind(searchHotkey.value.open)
	hotkeys.unbind(searchHotkey.value.close)
</script>

<style lang="less" scoped>
	:deep(.ant-input) {
		height: 35px;
	}
	:deep(.ant-input:not(:first-child)) {
		padding-left: 10px;
	}
	:deep(.ant-input-prefix) {
		font-size: 20px;
	}
	:deep(.ant-list-sm .ant-list-item) {
		padding: 4px 16px;
	}
	:deep(.ant-list-item-meta) {
		align-items: center;
	}
	:deep(.ant-list-item.active) {
		background-color: var(--primary-1);
	}
	.xn-mn10p0 {
		margin: 10px 0;
	}
	.xn-pr10 {
		padding-right: 10px;
	}
	.search-box {
		width: 100%;
	}
	.beauty-scroll {
		scrollbar-color: var(--primary-color) var(--primary-2);
		scrollbar-width: thin;
		-ms-overflow-style: none;
		position: relative;
		&::-webkit-scrollbar {
			width: 3px;
			height: 1px;
		}
		&::-webkit-scrollbar-thumb {
			border-radius: 3px;
			background: var(--primary-color);
		}
		&::-webkit-scrollbar-track {
			-webkit-box-shadow: inset 0 0 1px rgba(0, 0, 0, 0);
			border-radius: 3px;
			background: var(--primary-3);
		}
	}
	.search-card {
		height: 380px;
		overflow: hidden;
		overflow-y: scroll;
	}
	.search-tips {
		display: flex;
		border-top: 1px solid var(--component-background);
		padding-top: 10px;
		.tips {
			margin-right: 10px;
		}
		.key {
			width: 30px;
			height: 20px;
			line-height: 20px;
			text-align: center;
			padding-bottom: 2px;
			margin: 0px 4px;
			border-radius: 2px;
			box-shadow:
				inset 0 -2px #cdcde6,
				inset 0 0 1px 1px #fff,
				0 1px 2px 1px #1e235a66;
			font-weight: bold;
		}
	}
</style>
