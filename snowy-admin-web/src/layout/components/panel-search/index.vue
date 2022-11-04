<template>
	<div @keyup.up="handleKeyUp" @keyup.down="handleKeyDown" @keyup.enter="handleKeyEnter" @click.self="handlePanelClick">
		<a-input
			ref="input"
			v-model="searchText"
			class="search-box"
			style="width: 100%"
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
			style="margin: 10px 0"
		>
			<div ref="cardList" class="search-card beauty-scroll">
				<a-list size="small" :data-source="resultsList">
					<template #renderItem="{ item, index }">
						<a-list-item
							@click="handleSelect(item.fullPath)"
							@mouseover="onCardItemHover(index)"
							:class="{ active: index === cardIndex }"
							style="padding-right: 10px"
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
</template>

<script>
	import Fuse from 'fuse.js'
	import { mapState } from 'vuex'

	export default {
		data() {
			return {
				searchText: '',
				cardIndex: 0,
				results: []
			}
		},
		computed: {
			...mapState('search', ['pool']),
			// 这份数据是展示在搜索面板下面的
			resultsList() {
				return this.results.length === 0 || this.searchText === '' ? this.pool : this.results
			},
			// 根据 pool 更新 fuse 实例
			fuse() {
				return new Fuse(this.pool, {
					shouldSort: true, // 按分数对结果列表进行排序
					threshold: 0.6, // 什么时候放弃
					location: 0, // 大致位置
					distance: 100, // 接近程度
					minMatchCharLength: 1, // 匹配长度
					keys: ['name', 'namePinyin', 'namePinyinFirst']
				})
			}
		},
		methods: {
			// 过滤选项 这个方法在每次输入框的值发生变化时会触发
			querySearch(e) {
				let queryString = e.target.value || ''
				const results = queryString && this.fuse.search(queryString).map((e) => e.item)
				this.searchText = queryString
				this.results = results
			},
			// 聚焦输入框
			focus() {
				this.searchText = ''
				setTimeout(() => {
					if (this.$refs.input) {
						this.$refs.input.focus()
					}
					// 还原
					this.searchText = ''
					this.results = []
				}, 300)
			},
			handleKeyEnter() {
				let idx = this.cardIndex
				if (this.resultsList[idx]) {
					this.handleSelect(this.resultsList[idx].fullPath)
				}
			},
			handleKeyUp() {
				this.handleKeyUpOrDown(true)
			},
			handleKeyDown() {
				this.handleKeyUpOrDown(false)
			},
			handleKeyUpOrDown(up) {
				let len = this.resultsList.length - 1
				let idx = this.cardIndex
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
				this.cardIndex = idx
				if (this.$refs.cardList.getElementsByClassName('ant-list-item')[idx]) {
					this.$refs.cardList.scrollTop = this.$refs.cardList.getElementsByClassName('ant-list-item')[idx].offsetTop
				} else {
					this.$refs.cardList.scrollTop = 0
				}
			},
			onCardIn() {
				this.$refs.input.activated = false
				this.$refs.input.blur()
			},
			onCardOut() {
				this.cardIndex = -1
			},
			onCardItemHover(index) {
				this.cardIndex = index
			},
			// 接收用户在下拉菜单中选中事件
			handleSelect(path) {
				// 如果用户选择的就是当前页面 就直接关闭搜索面板
				if (path === this.$route.path) {
					this.handleEsc()
					return
				}
				this.$router.push({ path })
				this.handleEsc()
			},
			// 关闭输入框的下拉菜单
			closeSuggestion() {
				if (this.$refs.input.activated) {
					this.results = []
					this.$refs.input.activated = false
				}
			},
			// 接收用户点击空白区域的关闭
			handlePanelClick(e) {
				if ('INPUT' !== e.target.tagName) {
					this.handleEsc()
				}
			},
			// 接收用户触发的关闭
			async handleEsc() {
				this.closeSuggestion()
				await this.$nextTick()
				this.$emit('close')
			}
		}
	}
</script>

<style lang="less" scoped>
	:deep(.ant-input){
		height: 35px;
	}
	:deep(.ant-input:not(:first-child)){
		padding-left: 10px;
	}
	:deep(.ant-input-prefix){
		font-size: 20px;
	}
	:deep(.ant-list-sm .ant-list-item){
		padding: 4px 16px;
	}
	:deep(.ant-list-item-meta){
		align-items: center;
	}
	:deep(.ant-list-item.active){
		background-color: var(--primary-1);
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
		height: 220px;
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
			box-shadow: inset 0 -2px #cdcde6, inset 0 0 1px 1px #fff, 0 1px 2px 1px #1e235a66;
			font-weight: bold;
		}
	}
</style>
