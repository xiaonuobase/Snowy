# XnBatchButton 批量操作按钮

`XnBatchButton` 是一个用于表格批量操作的通用按钮组件。它集成了选中项校验、操作确认（可选）以及加载状态管理，简化了批量删除、批量更新等场景的实现。

## 功能特性

- **自动校验**：点击时自动检查 `selectedRowKeys` 是否为空，若为空则提示“请选择一条或多条数据”。
- **确认提示**：支持通过 `isPopconFirm` 开启或关闭二次确认气泡框（Popconfirm）。
- **参数封装**：回调时自动将选中的 ID 封装为对象数组 `[{ [idKey]: value }, ...]`。
- **样式定制**：支持自定义按钮名称、类型、图标、颜色、尺寸等。

## Props

| 属性名 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `buttonName` | `String` | `'批量操作'` | 按钮上显示的文字。 |
| `buttonType` | `String` | `undefined` | 按钮类型，参考 Ant Design Vue Button `type` (e.g., 'primary', 'default')。 |
| `buttonDanger` | `Boolean` | `false` | 是否为危险按钮（红色）。 |
| `icon` | `String` | `undefined` | 按钮图标组件名。 |
| `size` | `String` | `'middle'` | 按钮尺寸。 |
| `color` | `String` | `''` | 图标的颜色样式。 |
| `selectedRowKeys` | `Array` | `[]` | **[必填]** 当前选中的行 Key 数组。 |
| `idKey` | `String` | `'id'` | 构造回调参数时使用的 Key 名。 |
| `isPopconFirm` | `Boolean` | `true` | 是否开启点击后的二次确认气泡框。 |
| `loading` | `Boolean` | `false` | 按钮是否处于加载状态。 |

## Events

| 事件名 | 说明 | 回调参数 |
| --- | --- | --- |
| `batchCallBack` | 点击确认（或点击按钮）并通过校验后触发。 | `(params: Array<Object>)` <br> 例如：`[{ id: 1 }, { id: 2 }]` |

## 使用示例

```vue
<template>
  <div>
    <!-- 表格组件 -->
    <a-table
      :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
      :data-source="dataSource"
      :columns="columns"
      rowKey="id"
    />

    <!-- 批量删除按钮 -->
    <XnBatchButton
      buttonName="批量删除"
      icon="DeleteOutlined"
      buttonDanger
      :selectedRowKeys="selectedRowKeys"
      @batchCallBack="handleBatchDelete"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';

const selectedRowKeys = ref([]);

const onSelectChange = (keys) => {
  selectedRowKeys.value = keys;
};

const handleBatchDelete = (params) => {
  console.log('需要删除的参数：', params);
  // params 结构: [{ id: 'key1' }, { id: 'key2' }]
  // 这里调用 API 接口
};
</script>
```
