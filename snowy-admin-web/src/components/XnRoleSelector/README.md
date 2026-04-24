## 小诺角色选择器

### 说明

该组件为小诺角色选择器，可返回id用逗号隔离的字符串或id数组类型的数据格式。使用异步懒加载树模式。

@author yubaoshan

@data 2024年7月5日23:59:23

### props定义

| 序号  | 编码                  | 类型            | 说明                                          | 默认     |
|-----|---------------------|---------------|---------------------------------------------|--------|
| 1   | radioModel          | Boolean       | 是否单选；与addShow隐藏同时可用                         | false  |
| 2   | dataIsConverterFlw  | Boolean       | 是否为工作流格式                                    | false  |
| 3   | orgTreeApi          | Function      | 机构树懒加载接口                                    | -      |
| 4   | rolePageApi         | Function      | 角色分页接口                                      | -      |
| 5   | roleListByIdListApi | Function      | 通过id数组查询list数据接口                            | 已配置    |
| 6   | value               | Object或String | 通过v-model:value绑定数据                         | -      |
| 7   | dataType            | String        | 数据类型array或string                            | array  |
| 8   | show                | Boolean       | 是否显示已选择角色（非表单内、单纯的选择角色需要隐藏）                 | true   |
| 9   | addShow             | Boolean       | 是否默认的增加人员按钮，与radioModel为或的关系                | true   |
| 10  | roleGlobal          | Boolean       | 是否展示"全局"节点                                  | true   |

### emits定义

| 序号 | 方法名    | 参数类型           | 说明                              |
|----|--------|----------------|---------------------------------|
| 1  | value  | 根据 dataType 而定         | 当选择角色后通过v-model:value绑定到组件上     |
| 2  | onBack | 根据 dataType 而定 | 通过@onBack 方法返回选中的数据，触发点为选中或删除角色 |

### slot定义

| 序号 | 插槽名    | 用途                | 用途                |
|----|--------|-------------------|-------------------|
| 1  | button | 在角色新增按钮后可以插入自定义按钮 | 不满足新增角色按钮样式，可以自定义 |
