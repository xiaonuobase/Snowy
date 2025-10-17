## 小诺字典选择器组件

字典选择器，支持下拉框和按钮组两种组件。



### 使用示例

```vue
<xn-dict-select v-model:value="formData.category" dict-type="ORG_CATEGORY" option-type="dropdown" allow-clear />
```



### API

#### props

| 参数           | 说明                                                    | 类型    | 默认值     |
| -------------- | ------------------------------------------------------- | ------- | ---------- |
| value(v-model) | 指定当前选中的条目                                      | string  | -          |
| dictType       | 字典名称                                                | string  | -          |
| optionType     | 组件形式：下拉框或按钮组('dropdown'\|'radio'\|'button') | string  | 'dropdown' |
| disabled       | 禁用组件                                                | boolean | false      |
| size           | 组件尺寸（'small'\|'middle'\|'large'）                  | string  | 'middle'   |
| allowClear     | 支持清除(仅支持'dropdown'组件)                          | boolean | false      |
| placeholder    | 选择框默认文字(仅支持'dropdown'组件)                    | string  | -          |

#### 事件

| 事件名称 | 说明                 | 回调参数        |
| -------- | -------------------- | --------------- |
| change   | 选中option调用此函数 | function(value) |

