iconSelector
====

> 图标选择组件，常用于为某一个数据设定一个图标时使用
> eg: 设定菜单列表时，为每个菜单设定一个图标

该组件由 [小诺开源技术](https://www.xiaonuo.vip) 封装



### 使用方式

```vue
<template>
	<div>
			<a-button type="primary" @click="openIcon(iconValue)">选择</a-button>
		  <icon-selector ref="iconselector" @iconCallBack="iconCallBack" />
    </div>
</template>

<script setup>
	const iconselector = ref()  //  绑定ref="iconselector"

  // 打开icon选择器
	const openIcon = (iconValue) => {
		iconselector.value.showIconModal(iconValue)
	}
  	// 选择后回调
	const iconCallBack = (value) => {
      console.log('iconCallBack Icon', value)
	}

</script>
```



### 事件

| 名称         | 说明                       | 类型   | 默认值 |
| ------------ | -------------------------- | ------ | ------ |
| iconCallBack | 当改变了 `icon` 选中项触发 | String | -      |

### 方法

| 名称      | 说明                                    | 类型   | 默认值 |
| --------- | --------------------------------------- | ------ | ------ |
| showIconModal | 打开选择器Model触发，携带图标时默认选中 | String | -      |
