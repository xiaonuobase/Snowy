## 小诺人员选择器

### 说明

该组件为小诺人员选择器，可返回id用逗号隔离的字符串或id数组类型的数据格式。支持同步全量树和异步懒加载树两种模式。

@author yubaoshan

@data 2024年4月13日23:59:23

### props定义

| 序号  | 编码                  | 类型            | 说明                                          | 默认     |
|-----|---------------------|---------------|---------------------------------------------|--------|
| 1   | radioModel          | Boolean       | 是否单选；与addShow隐藏同时可用                         | false  |
| 2   | dataIsConverterFlw  | Boolean       | 是否为工作流格式                                    | false  |
| 3   | orgTreeApi          | Function      | 机构树接口（同步全量加载）                               | -      |
| 4   | orgTreeLazyApi      | Function      | 机构树懒加载接口（配置后优先使用异步模式，不配则走同步全量）              | -      |
| 5   | userPageApi         | Function      | 用户分页接口                                      | -      |
| 6   | userListByIdListApi | Function      | 通过id数组查询list数据接口                            | 已配置    |
| 7   | value               | Object或String | 通过v-model:value绑定数据                         | -      |
| 8   | dataType            | String        | 数据类型array或string                            | string |
| 9   | userShow            | Boolean       | 是否显示已选择用户（非表单内、单纯的选择用户需要隐藏）                 | true   |
| 10  | addShow             | Boolean       | 是否默认的增加人员按钮，与radioModel为或的关系                | true   |

### emits定义

| 序号 | 方法名    | 参数类型           | 说明                              |
|----|--------|----------------|---------------------------------|
| 1  | value  | 根据 dataType 而定         | 当选择用户后通过v-model:value绑定到组件上     |
| 2  | onBack | 根据 dataType 而定 | 通过@onBack 方法返回选中的数据，触发点为选中或删除用户 |

### slot定义

| 序号 | 插槽名    | 用途                | 用途                |
|----|--------|-------------------|-------------------|
| 1  | button | 在人员新增按钮后可以插入自定义按钮 | 不满足新增人员按钮样式，可以自定义 |
