<template>
	<div class="shortcut-item">
		<div class="icon-wrapper" :style="{ backgroundColor: getBgColor() }">
			<component :is="props.icon" :style="{ color: getColor() }" class="shortcut-icon" />
		</div>
		<span class="shortcut-label">{{ props.label }}</span>
	</div>
</template>

<script setup name="shortcut">
	const props = defineProps({
		icon: {
			type: String,
			default: () => 'menu-outlined',
			required: false
		},
		label: {
			type: String,
			default: () => '快捷方式',
			required: false
		},
		color: {
			type: String,
			default: () => '',
			required: false
		}
	})
	const colorList = [
		{ color: 'var(--blue-6)', bg: 'var(--blue-1)' },
		{ color: 'var(--green-6)', bg: 'var(--green-1)' },
		{ color: 'var(--orange-6)', bg: 'var(--orange-1)' },
		{ color: 'var(--red-5)', bg: 'var(--red-1)' },
		{ color: 'var(--purple-6)', bg: 'var(--purple-1)' },
		{ color: 'var(--cyan-6)', bg: 'var(--cyan-1)' }
	]

	let colorIndex = Math.floor(Math.random() * colorList.length)

	const getColor = () => props.color || colorList[colorIndex].color
	const getBgColor = () => colorList[colorIndex].bg
</script>

<style scoped lang="less">
	.shortcut-item {
		height: 80px;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		cursor: pointer;
		transition: all 0.25s ease;
		border-radius: 2px;
		background: var(--component-background);
		border: 1px solid var(--border-color-split);
		&:hover {
			transform: translateY(-2px);
			box-shadow: 0 4px 12px var(--shadow-color);
			border-color: var(--primary-color);
		}
		&:active {
			transform: translateY(0);
		}
	}
	.icon-wrapper {
		width: 40px;
		height: 40px;
		border-radius: 10px;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 6px;
	}
	.shortcut-icon {
		font-size: 20px;
	}
	.shortcut-label {
		font-size: 12px;
		color: var(--text-color);
		font-weight: 500;
		max-width: 90%;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
</style>
