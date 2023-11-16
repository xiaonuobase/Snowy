# 小诺卡片列表的组件

## 说明

### props定义

| 序号 | 编码         | 类型      | 说明     | 示例                     |
|----|------------|---------|--------|------------------------|
| 1  | grid       | Object  | grid布局 | 见：and-design定义（Grid栅格） |
| 2  | dataSource | Array   | 数据源    | 见：dataSource字段定义       |
| 3  | page       | Object  | 分页     | 见：page字段定义             |
| 4  | actions    | Array   | 操作数组   | 见：action字段定义           |
| 5  | loading    | Boolean | 加载中提示  | -                      |

> dataSource字段定义

| 序号 | 编码       | 类型     | 说明              | 示例            |
|----|----------|--------|-----------------|---------------|
| 1  | title    | String | 标题              | 设备编码          |
| 2  | subTitle | String | 副标题             | 设备名称          |
| 3  | img      | String | 图片              |               |
| 4  | contents | Array  | 内容              | 见：content字段定义 |
| 5  | badge    | Object | 徽标              | 见：badge字段定义   |
| 6  | record   | Object | 数据记录，emit触发回调参数 |               |

> content字段定义

| 序号 | 编码    | 类型     | 说明 | 示例   |
|----|-------|--------|----|------|
| 1  | label | String | 标签 | 所属产品 |
| 2  | value | Object | 值  | 透传   |

> badge字段定义

| 序号 | 编码    | 类型     | 说明 | 示例                     |
|----|-------|--------|----|------------------------|
| 1  | text  | String | 标签 | 所属产品                   |
| 2  | color | String | 颜色 | 见：ant-design定义（预设、自定义） |

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
| 1  | action      | Object | {key: 'edit', record:{...}} |
| 2  | page-change | Number | 1                           |
