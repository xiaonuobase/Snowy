<template>
	<div class="container">
		<a-tag :color="randomColor()" class="container-tag">
			<component :is="props.icon" class="container-tag-icon" />
		</a-tag>
		<span class="container-span">{{ props.label }}</span>
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
	// 颜色列表
	const colorList = ['#7265E6', '#FFBF00', '#00A2AE', '#F56A00', '#1677FF', '#606D80']
	// 获取随机颜色
	const randomColor = () => {
		if (props.color) {
			return props.color
		}
		return colorList[randomNum(0, colorList.length - 1)]
	}
	// 获取minNum到maxNum内的随机数
	const randomNum = (minNum, maxNum) => {
		switch (arguments.length) {
			case 1:
				return parseInt(Math.random() * minNum + 1, 10)
			case 2:
				return parseInt(Math.random() * (maxNum - minNum + 1) + minNum, 10)
			default:
				return 0
		}
	}
</script>

<style scoped>
	.container {
		height: 60px;
		/*border:1px solid var(--border-color-split);*/
		border-radius: 5px;
		display: flex;
		align-items: center;
		cursor: pointer;
		/*实现渐变（时间变化效果）*/
		-webkit-transition: all 0.5s;
		-moz-transition: all 0.5s;
		-ms-transition: all 0.5s;
		-o-transition: all 0.5s;
		transition: all 0.5s;
	}
	.container:hover {
		background: var(--border-color-split);
	}
	.container-tag {
		width: 42px;
		height: 42px;
		border-radius: 10px;
		display: flex;
		align-items: center;
		margin-left: 10px;
		justify-content: center;
	}
	.container-tag-icon {
		font-size: 25px;
	}
	.container-span {
		max-width: 60%;
		font-weight: 500;
	}
</style>
