# 小诺数据列表的组件

## 说明

### props定义

| 序号 | 编码         | 类型      | 说明    | 示例               |
|----|------------|---------|-------|------------------|
| 1  | dataSource | Array   | 数据源   | 见：dataSource字段定义 |
| 2  | page       | Object  | 分页    | 见：page字段定义       |
| 3  | actions    | Array   | 操作数组  | 见：action字段定义     |
| 4  | loading    | Boolean | 加载中提示 | -                |

> dataSource字段定义

| 序号 | 编码           | 类型     | 说明              | 示例                |
|----|--------------|--------|-----------------|-------------------|
| 1  | title        | String | 标题              | 设备编码              |
| 2  | descriptions | Array  | 描述              | 见：description字段定义 |
| 3  | contents     | Array  | 内容              | 见：content字段定义     |
| 4  | record       | Object | 数据记录，emit触发回调参数 |                   |

> description字段定义

| 序号 | 编码      | 类型     | 说明 | 示例   |
|----|---------|--------|----|------|
| 1  | title   | String | 标题 | 设备名称 |
| 2  | content | Object | 内容 | 测试设备 |

> content字段定义

| 序号 | 编码      | 类型     | 说明 | 示例                  |
|----|---------|--------|----|---------------------|
| 1  | title   | String | 标题 | 数据更新时间              |
| 2  | content | Object | 内容 | 2023-11-14 09:00:00 |

> action字段定义

| 序号 | 编码    | 类型     | 说明 | 示例      |
|----|-------|--------|----|---------|
| 1  | key   | String | 键  | setting |
| 2  | label | String | 标签 | 所属产品    |
| 3  | icon  | String | 图标 | setting |
| 4  | color | String | 颜色 | red     |

> page字段定义

| 序号 | 编码      | 类型     | 说明   | 示例 |
|----|---------|--------|------|----|
| 1  | current | Number | 当前页  | 1  |
| 2  | size    | Number | 每页大小 | 6  |
| 3  | total   | Number | 总数   | 0  |

### emits定义

| 序号 | 方法名         | 参数类型   | 参数示例                        |
|----|-------------|--------|-----------------------------|
| 1  | title       | Object | {record: {...}}             |
| 1  | action      | Object | {key: 'edit', record:{...}} |
| 2  | page-change | Number | 1                           |

### slots定义

| 序号 | 名称           | 说明     | 示例 |
|----|--------------|--------|----|
| 1  | title-prefix | 标题前缀插槽 | -  |
| 2  | title-suffix | 内容后缀插槽 | -  |
