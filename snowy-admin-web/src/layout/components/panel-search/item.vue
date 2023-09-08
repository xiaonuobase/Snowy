<template>
	<div class="d2-panel-search-item" :class="props.hoverMode ? 'can-hover' : ''" flex>
		<div class="d2-panel-search-item__icon" flex-box="0">
			<div class="d2-panel-search-item__icon-box" flex="main:center cross:center">
				<a-icon v-if="props.item.icon" :type="props.item.icon" />
				<a-icon v-else type="menu" />
			</div>
		</div>
		<div class="d2-panel-search-item__info" flex-box="1" flex="dir:top">
			<div class="d2-panel-search-item__info-title" flex-box="1" flex="cross:center">
				<span>{{ props.item.title }}</span>
			</div>
			<div class="d2-panel-search-item__info-fullTitle" flex-box="0">
				<span>{{ props.item.fullTitle }}</span>
			</div>
			<div class="d2-panel-search-item__info-path" flex-box="0">
				<span>{{ props.item.path }}</span>
			</div>
		</div>
	</div>
</template>

<script setup>
	const props = defineProps({
		item: {
			default: () => ({})
		},
		hoverMode: {
			default: false
		}
	})
</script>

<style lang="less" scoped>
	.d2-panel-search-item {
		height: 64px;
		margin: 0px -20px;
		&.can-hover {
			margin: 0px;
			&:hover {
				background-color: #f5f7fa;
				.d2-panel-search-item__icon {
					.d2-panel-search-item__icon-box {
						i {
							font-size: 24px;
							color: var(--primary-color);
						}
					}
				}
				.d2-panel-search-item__info {
					.d2-panel-search-item__info-title {
						//color: $color-text-main;
					}
					.d2-panel-search-item__info-fullTitle {
						//color: $color-text-normal;
					}
					.d2-panel-search-item__info-path {
						//color: $color-text-normal;
					}
				}
			}
		}
		.d2-panel-search-item__icon {
			width: 64px;
			.d2-panel-search-item__icon-box {
				height: 64px;
				width: 64px;
				border-right: 1px solid #ccc;
				i {
					font-size: 20px;
					//color: $color-text-sub;
				}
				svg {
					height: 20px;
					width: 20px;
				}
			}
		}
		.d2-panel-search-item__info {
			margin-left: 10px;
			.d2-panel-search-item__info-title {
				font-size: 16px;
				line-height: 16px;
				font-weight: bold;
				//color: $color-text-normal;
			}
			.d2-panel-search-item__info-fullTitle {
				font-size: 10px;
				line-height: 14px;
				color: grey;
			}
			.d2-panel-search-item__info-path {
				margin-bottom: 4px;
				font-size: 10px;
				line-height: 14px;
				color: grey;
			}
		}
	}
</style>
