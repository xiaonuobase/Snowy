XnTreeSelect 懒加载树选择器
====


封装说明
----

>  基础的使用方式与 API 与 [官方版(TreeSelect)](https://www.antdv.com/components/tree-select-cn/) 基本一致，在其基础上，封装了懒加载和编辑回显的能力。
>
> 解决的核心痛点：编辑表单时，树选择器需要展开到已选节点并正确回显，同时保留懒加载能力让用户可以切换到其他分支。不再需要每个表单手写 `buildTreeWithAncestors`、`collectAncestorKeys`、`onLoadData` 等重复逻辑。

例子1
----
（基础使用 — 职位管理选择所属组织）

```vue
<template>
  <a-form-item label="所属组织：" name="orgId">
    <xn-tree-select
      ref="orgTreeSelectRef"
      v-model:value="formData.orgId"
      :tree-api="positionApi.positionOrgTreeSelector"
      :ancestor-api="positionApi.positionGetAncestorNodes"
      placeholder="请选择组织"
    />
  </a-form-item>
</template>

<script setup>
import positionApi from '@/api/sys/positionApi'

const formData = ref({})
const orgTreeSelectRef = ref()

// 新增时：初始化树
const onAdd = () => {
  nextTick(() => {
    orgTreeSelectRef.value.init()
  })
}

// 编辑时：回显并展开到已选节点
const onEdit = (record) => {
  formData.value = Object.assign({}, record)
  nextTick(() => {
    orgTreeSelectRef.value.echo([record.orgId])
  })
}
</script>
```


例子2
----

（带顶级虚拟节点 — 组织管理选择上级组织）

> 组织管理的"上级组织"允许选择"顶级"作为根节点，通过 `topNode` 属性在树顶部插入一个虚拟节点。

```vue
<template>
  <a-form-item label="上级组织：" name="parentId">
    <xn-tree-select
      ref="orgTreeSelectRef"
      v-model:value="formData.parentId"
      :tree-api="orgApi.orgTreeSelector"
      :ancestor-api="orgApi.orgGetAncestorNodes"
      :top-node="{ id: '0', parentId: '-1', name: '顶级' }"
      placeholder="请选择上级组织"
    />
  </a-form-item>
</template>

<script setup>
import orgApi from '@/api/sys/orgApi'

const formData = ref({})
const orgTreeSelectRef = ref()

const onOpen = (record) => {
  nextTick(() => {
    if (record) {
      // 编辑：回显
      orgApi.orgDetail({ id: record.id }).then((data) => {
        formData.value = Object.assign({}, data)
        const pid = data.parentId
        if (pid && pid !== '0') {
          orgTreeSelectRef.value.echo([pid])
        } else {
          orgTreeSelectRef.value.init()
        }
      })
    } else {
      // 新增
      orgTreeSelectRef.value.init()
    }
  })
}
</script>
```


例子3
----

（用户管理 — 编辑时回显多个组织）

> 用户有主组织和多个任职组织，调用 `echo` 时传入所有 orgId，组件会一次性展开所有路径。

```vue
<template>
  <a-form-item label="选择组织：" name="orgId">
    <xn-tree-select
      ref="orgTreeSelectRef"
      v-model:value="formData.orgId"
      :tree-api="userApi.userOrgTreeSelector"
      :ancestor-api="userApi.userGetAncestorNodes"
      placeholder="请选择组织"
      @change="onOrgChange"
    />
  </a-form-item>
</template>

<script setup>
import userApi from '@/api/sys/userApi'

const formData = ref({})
const orgTreeSelectRef = ref()

const onEdit = (record) => {
  userApi.userDetail({ id: record.id }).then((data) => {
    formData.value = Object.assign({}, data)
    // 收集所有需要回显的 orgId（主组织 + 任职组织）
    const allOrgIds = [data.orgId]
    if (data.positionJson) {
      JSON.parse(data.positionJson).forEach((item) => {
        if (item.orgId && !allOrgIds.includes(item.orgId)) {
          allOrgIds.push(item.orgId)
        }
      })
    }
    orgTreeSelectRef.value.echo(allOrgIds)
  })
}
</script>
```


内置方法
----


| 方法  | 说明                               | 参数类型       | 参数示例               |
| ----- | ---------------------------------- | -------------- | ---------------------- |
| init  | 初始化树（新增模式），加载根节点   | 无             | -                      |
| echo  | 回显模式，展开到指定节点           | Array\<string> | ['orgId1', 'orgId2']   |
| reset | 重置组件（清空树数据、展开、选中） | 无             | -                      |


内置属性
----
> 除去 `a-tree-select` 自带属性外（通过 `v-bind="$attrs"` 透传），还额外提供了以下属性

| 属性        | 说明                                                                        | 类型     | 默认值                                                    |
| ----------- | --------------------------------------------------------------------------- | -------- | --------------------------------------------------------- |
| treeApi     | 树数据加载函数，必须为 `Promise`，参数 `{ parentId? }` **必须绑定**         | Function | -                                                         |
| ancestorApi | 祖先节点加载函数，必须为 `Promise`，参数为 id 数组（编辑回显时需要）        | Function | -                                                         |
| topNode     | 顶部虚拟节点对象，如 `{ id: '0', name: '顶级' }`，树根部会包裹此节点       | Object   | -                                                         |
| fieldNames  | 字段名映射                                                                  | Object   | `{ children: 'children', label: 'name', key: 'id', value: 'id' }` |
| parentIdKey | 父节点 ID 字段名，用于构建树和回溯路径                                      | String   | `'parentId'`                                              |
| rootParentId| 根节点的 parentId 值                                                        | String   | `'0'`                                                     |
| placeholder | 占位文本                                                                    | String   | `'请选择'`                                                |
| allowClear  | 允许清除                                                                    | Boolean  | `true`                                                    |
| disabled    | 禁用                                                                        | Boolean  | `false`                                                   |
| multiple    | 多选                                                                        | Boolean  | `false`                                                   |


后端接口约定
----

> 组件对后端接口有以下约定，只要满足就能直接使用。

**treeApi 接口**（懒加载树节点）

- 不传 `parentId` 时返回根节点列表
- 传 `parentId` 时返回该节点的直接子节点列表
- 每个节点需包含 `id`、`parentId`、`name`、`isLeaf` 字段

**ancestorApi 接口**（祖先路径 + 同级兄弟）

- 参数：id 数组，如 `['orgId1', 'orgId2']`
- 返回：这些节点的所有祖先节点 + 每层祖先的同级兄弟节点（扁平数组）
- 每个节点同样需包含 `id`、`parentId`、`name`、`isLeaf` 字段

```
示例：组织结构为 根→[A公司, B公司]→A公司→[IT部, 销售部]→IT部→[前端组]
请求：ancestorApi(['前端组id'])
返回：[根, A公司, B公司, IT部, 销售部, 前端组]  ← 每层都含兄弟
```


注意事项
----

> 1. 组件使用 `destroy-on-close` 的表单时，每次打开需重新调用 `init()` 或 `echo()`
> 2. `topNode` 的 `id` 值不要和真实数据冲突，通常用 `'0'` 表示"顶级"
> 3. 如果 `a-tree-select` 被 `v-if` 控制显隐（如角色表单的"所属机构"），需要在 `nextTick` 后再调用 `echo`
> 4. `ancestorApi` 为可选属性，不传则不支持回显展开（纯懒加载模式）
