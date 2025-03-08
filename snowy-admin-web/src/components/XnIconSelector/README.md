## 小诺图标选择器

### 说明

改组件为小诺人员选择器，可返回id用逗号隔离的字符串或id数组类型的数据格式

@author yubaoshan

@data 2024年4月13日23:59:23

### props定义

| 序号  | 编码                | 类型            | 说明                  | 默认    |
|-----|-------------------|---------------|---------------------|-------|
| 1   | value             | String        | 值，跟v-model绑定        | ''    |
| 2   | formRef           | -             | 表单dom，为了取消绑定的验证     | -     |

### 示例

```vue
<template>
	<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
		<a-form-item label="图标选择：" name="icon">
			<xn-icon-selector
				v-model:value="formData.icon"
				v-model:formRef="formRef"
				placeholder="请选择图标"
				allow-clear
			/>
		</a-form-item>
	</a-form>
</template>
<script setup>
	import { required } from '@/utils/formRules'
	// 定义表单的dom
	const formRef = ref()
	// 表单数据
	const formData = ref({
		icon: ''
	})
	// 默认要校验的
	const formRules = {
		icon: [required('请选择图标')]
	}
</script>
```
