<template>
	<a-modal
		v-model:visible="visible"
		title="图标选择"
		:mask-closable="false"
		:width="800"
		:destroy-on-close="true"
		:footer="null"
		@cancel="onCancel"
	>
		<a-tabs v-model:activeKey="activeKey" tab-position="left" size="small" @change="paneChange">
			<a-tab-pane v-for="item in iconData" :key="item.key" :tab="item.name">
				<div v-if="item.iconItem.length > 1" class="xn-icon-select-radio">
					<a-radio-group v-model:value="iconItemDefault" @change="radioGroupChange">
						<a-radio-button v-for="iconItem in item.iconItem" :key="iconItem.key" :value="iconItem.key">{{
							iconItem.name
						}}</a-radio-button>
					</a-radio-group>
				</div>

				<div :key="iconItemIns" v-for="iconItemIns in item.iconItem">
					<div v-if="iconItemIns.key === iconItemDefault" class="xn-icon-select-list">
						<ul>
							<li
								v-for="icon in iconItemIns.item"
								:key="icon"
								:class="icon === modelValue ? 'active' : ''"
								@click="selectIcon(icon)"
							>
								<component :is="icon" class="xn-icons" />
							</li>
						</ul>
					</div>
				</div>
			</a-tab-pane>
		</a-tabs>
	</a-modal>
</template>

<script>
	import config from '@/config/iconSelect'
	export default {
		data() {
			return {
				visible: false,
				iconData: [],
				modelValue: '',
				activeKey: 'default',
				iconItemDefault: 'default'
			}
		},
		mounted() {
			this.iconData.push(...config.icons)
		},
		methods: {
			// 打开
			showIconModal(value) {
				this.visible = true
				this.defaultSetting(value)
			},
			// 默认配置
			defaultSetting(value) {
				if (value) {
					this.modelValue = value
					// 判断展开哪个
					if (value.indexOf('-outlined') > -1 || value.indexOf('-filled') > -1 || value.indexOf('-two-tone') > -1) {
						this.activeKey = 'default'
						if (value.indexOf('-two-tone') > -1) {
							this.iconItemDefault = 'twotone'
						} else if (value.indexOf('-filled') > -1) {
							this.iconItemDefault = 'filled'
						}
					} else if (value.indexOf('-extend') > -1) {
						// 扩展列表
						this.activeKey = 'extend'
						// 如扩展其他顶部单选的情况，默认选中在这里配置,同时这里需要做判断
						// this.iconItemDefault = '您的json中配置的'
					}
				}
			},
			// 切换标签页，如果是切换到了没用额外的标签页的地方，我们将其置为默认
			paneChange(e) {
				if (e.indexOf('default') === -1) {
					this.iconItemDefault = 'default'
				}
			},
			// 切换icon风格
			radioGroupChange(e) {
				this.iconItemDefault = e.target.value
			},
			// 选择图标后关闭并返回
			selectIcon(value) {
				this.defaultValue = value
				this.visible = false
				// eslint-disable-next-line vue/require-explicit-emits
				this.$emit('iconCallBack', this.defaultValue)
			},
			onCancel() {
				this.visible = false
			}
		}
	}
</script>

<style lang="less" scoped>
	.xn-icon-select-radio {
		padding-left: 5px;
		padding-bottom: 10px;
	}
	.xn-icons {
		font-size: 26px;
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.xn-icon-select-list {
		height: 360px;
		overflow: auto;
	}
	.xn-icon-select-list ul {
		li {
			display: inline-block;
			width: 60px;
			height: 60px;
			padding: 18px;
			margin: 5px;
			border-radius: 2px;
			vertical-align: top;
			box-shadow: 0 0 0 1px var(--border-color-split);
			transition: all 0.1s;
			position: relative;

			&:hover,
			&.active {
				cursor: pointer;
				color: #ffffff;
				background-color: var(--primary-color);
			}
		}
	}
</style>
