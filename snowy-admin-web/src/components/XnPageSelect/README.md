XnPageSelect 分页下拉选择器
====


封装说明
----

>  基础的使用方式与 API 与 [官方版(Select)](https://www.antdv.com/components/select-cn/) 本一致，在其基础上，封装了加载数据的方法。（调用后端分页接口，懒加载的效果查询下拉数据）
>
> @zhanggengbi在此基础上完善了功能：下拉查询同时支持搜索框搜索--调用后端数据搜索。

例子1
----
（基础使用）

```vue

<template>
<a-col :span="8">
    <a-form-item label="选择组织：" name="orgId">
        <a-tree-select
                       v-model:value="formData.orgId"
                       style="width: 100%"
                       :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                       placeholder="请选择组织"
                       allow-clear
                       tree-default-expand-all
                       :tree-data="treeData"
                       :tree-default-expanded-keys="treeDefaultExpandedKeys"
                       :field-names="{
                                     children: 'children',
                                     label: 'name',
                                     value: 'id'
                                     }"
                       @change="selePositionData(formData.orgId, 0)"
                       ></a-tree-select>
    </a-form-item>
    </a-col>
<a-col :span="8">
    <a-form-item label="选择职位：" name="positionId">
        <xn-page-select
                        ref="xnPositionPageSelectRef"
                        v-model:value="formData.positionId"
                        placeholder="请选择职位"
                        allow-clear
                        :page-function="selectApiFunction.positionSelector"
                        />
    </a-form-item>
    </a-col>
</template>

<script setup>
import XnPageSelect from '@/components/XnPageSelect/'
import bizUserApi from '@/api/biz/bizUserApi'

// 表单数据
const formData = ref({})
const xnPositionPageSelectRef = ref()

// 机构选择后查询对应的职位
const selePositionData = (orgId, type) => {
    if (orgId) {
        const xnPositionPageSelectParam = {
            orgId: orgId
        }
        xnPositionPageSelectRef.value.onPage(xnPositionPageSelectParam)
        xnUserPageSelectRef.value.onPage()
        // 此类型代表选择的时候重置后面的职位
        if (type === 0) {
            formData.value.positionId = undefined
            formData.value.directorId = undefined
        }
    } else {
        formData.value.positionId = undefined
        formData.value.directorId = undefined
    }
}

// 传递选择组件需要的API
const selectApiFunction = {
    positionSelector: (param) => {
        return bizUserApi.userPositionSelector(param).then((data) => {
            return Promise.resolve(data)
        })
    },
}

</script>
```



例子2
----

（支持搜索框搜索--调用后端数据搜索）

```vue
<template>
  <xn-page-select
                  ref="xnCustomerPageSelectRef"
                  v-model:value="searchFormState.customerInfoId"
                  placeholder="请选择客户名称"
                  searchKeyName="customerName"
                  @change="tableRef.refresh(true);"
                  allow-clear
                  show-search
                  :page-function="selectApiFunction.customerSelector"
                  />
</template>

<script setup>
import XnPageSelect from '@/components/XnPageSelect/'
import customerInfoApi from "@/api/eden/customerInfoApi";

const xnCustomerPageSelectRef = ref();
const searchFormState = ref();
nextTick(() => {
	xnCustomerPageSelectRef.value.onPage({current: 1});
});
const selectApiFunction = {
	customerSelector: (param) => {
		return customerInfoApi.customerPage(Object.assign(param, {envFlag: searchFormState.value.envFlag})).then((data) => {
			if (data.records){
				data.records = data.records.map((item) => {
					return {
						name: item.customerName,
						id: item.customerInfoId,
					};
				});
			}
			return Promise.resolve(data)
		})
	}
}
</script>
```

![分页下拉查询展示](.\README.assets\分页下拉查询展示.gif)

内置方法
----


| 方法   | 说明         | 参数类型 | 参数示例     |
| ------ | ------------ | -------- | ------------ |
| onPage | 请求分页数据 | Object   | {current: 1} |


内置属性
----
> 除去 `a-select` 自带属性外，还而外提供了一些额外属性属性  


| 属性          | 说明                                        | 类型    | 默认值 |
| ------------- | ------------------------------------------- | ------- | ------ |
| pageFunction  | 分页函数 必须为 `Promise` 对象 **必须绑定** | Promise | -      |
| pageSize      | 分页条数                                    | Number  | 10     |
| showSearch    | 配置是否可搜索                              | Boolean | false  |
| searchKeyName | 搜索字段属性名                              | String  | ''     |

注意事项
----

> 分页函数，返回的Promise对象的data数据是分页格式的数据
>
> data.records格式如下：{ name: name, id: id }   id最后是你的value，name是你的显示名称 

如果返回的数据data.records格式命名不对，可以重新封装一下

```javascript
// 示例重新封装一下data.records
const selectApiFunction = {
	customerSelector: (param) => {
		return customerInfoApi.customerPage(Object.assign(param, {envFlag: searchFormState.value.envFlag})).then((data) => {
			if (data.records){
				data.records = data.records.map((item) => {
					return {
						name: item.customerName,
						id: item.customerInfoId,
					};
				});
			}
			return Promise.resolve(data)
		})
	}
}
```
更新时间
----

该文档最后更新于： 2024-06-28 PM 16:45
