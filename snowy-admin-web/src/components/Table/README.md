Table 重封装组件说明
====


封装说明
----

>  基础的使用方式与 API 与 [官方版(Table)](https://vuecomponent.github.io/ant-design-vue/components/table-cn/) 本一致，在其基础上，封装了加载数据的方法。
>
> 你无需在你是用表格的页面进行分页逻辑处理，仅需向 Table 组件传递绑定 `:data="Promise"` 对象即可

该 `table` 由 [@Saraka](https://github.com/saraka-tsukai) 完成封装

由 `小诺技术团队` 完成Vue3升级并二次封装改进

例子1
----
（基础使用）

```vue

<template>
  <s-table
    ref="tableRef"
    :rowKey="(record) => record.data.id"
    :columns="columns"
    :data="loadData"
    :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
  >
  </s-table>
</template>

<script>
import STable from '@/components'

export default {
  components: {
    STable,
  },
  data() {
    return {
      columns: [
        {
          title: '规则编号',
          dataIndex: 'no',
        },
        {
          title: '描述',
          dataIndex: 'description',
        },
        {
          title: '服务调用次数',
          dataIndex: 'callNo',
          sorter: true,
          needTotal: true,
          customRender: text => `${text } 次`,
        },
        {
          title: '状态',
          dataIndex: 'status',
          needTotal: true,
        },
        {
          title: '更新时间',
          dataIndex: 'updatedAt',
          sorter: true,
        },
      ],
      // 查询条件参数
      queryParam: {},
      // 加载数据方法 必须为 Promise 对象
      loadData: (parameter) => {
        return this.$http.get('/service', {
          params: Object.assign(parameter, this.queryParam),
        }).then((res) => {
          return res.result
        })
      },
      selectedRowKeys: [],
      selectedRows: [],
    }
  },
  methods: {
    onSelectChange (selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },
  },
}
</script>
```



例子2
----

（简单的表格，最后一列是各种操作）

```vue
<template>
  <s-table ref="tableRef" :columns="columns" :data="loadData" :alert="false" bordered :row-key="(record) => record.id">
  <!-- #operator 插槽可以放入一些关于表格的操作，比如新增数据。 -->
  <template #operator class="table-operator">
      <a-space>
        <a-button type="primary" @click="">
          <template #icon><plus-outlined /></template>
          新增
        </a-button>
      </a-space>
    </template>
    <!-- #bodyCell 放入column表格列需要显示的数据，可以通过判断进行一个自定义显示 -->
   <template #bodyCell="{ column, record }">
			<template >
				<a-avatar class="xn-wh25" />
			</template>
			<template v-if="column.dataIndex === 'status'">
			  <!-- 进行自定义显示内容 -->
			</template>
      <!-- column.dataIndex === 'action' 时，可以进行编辑删除等关于这行数据的一个操作，操作内容可进行自定义 -->
			<template v-if="column.dataIndex === 'action'">
				<a @click="">编辑</a>
			</template>
		</template>
  </s-table>
</template>

<script setup>
import STable from '@/components/table/'
const tableRef = ref() //一定要进行一个表格的ref绑定
const columns = ref([
  {
    title: '规则编号',
    dataIndex: 'no',
  },
  {
    title: '描述',
    dataIndex: 'description',
  },
  {
    title: '服务调用次数',
    dataIndex: 'callNo',
  },
  {
    title: '状态',
    dataIndex: 'status',
  },
  {
    title: '更新时间',
    dataIndex: 'updatedAt',
  },
  {
    table: '操作',
    dataIndex: 'action',
    scopedSlots: { customRender: 'action' },
  },
])
const queryParam = ref({})
// 加载按钮数据
const loadData = (parameter) => {
    return this.$http.get('/service', {
      params: Object.assign(parameter, queryParam.value),
    }).then((res) => {
      return res.result
    })
}
const edit = (row) => {
    // axios 发送数据到后端 修改数据成功后
    // 调用 refresh() 重新加载列表数据
    // 这里 setTimeout 模拟发起请求的网络延迟..
    setTimeout(() => {
      tableRef.value.refresh() // refresh() 不传参默认值 false 不刷新到分页第一页
    }, 1500)

  }
</script>
```



内置方法
----

通过 `声明的ref去调用 ==> tableRef.value` 调用

`tableRef.value.refresh(true)` 刷新列表 (用户新增/修改数据后，重载列表数据)

> 注意：要调用 `refresh(bool)` 需要给表格组件设定 `ref` 值
>
> `refresh()` 方法可以传一个 `bool` 值，当有传值 或值为 `true` 时，则刷新时会强制刷新到第一页（常用户页面 搜索 按钮进行搜索时，结果从第一页开始分页）


内置属性
----
> 除去 `a-table` 自带属性外，还而外提供了一些额外属性属性  


| 属性           | 说明                                            | 类型              | 默认值 |
| -------------- | ----------------------------------------------- | ----------------- | ------ |
| alert          | 设置是否显示表格信息栏                          | [object, boolean] | null   |
| showPagination | 显示分页选择器，可传 'auto' \| boolean          | [string, boolean] | 'auto' |
| data           | 加载数据方法 必须为 `Promise` 对象 **必须绑定**  | Promise           | -      |
| lineSelection  | 是否开启点击行高亮显示并选中                     | Boolean           | 'false'      |


`alert` 属性对象：

```javascript
alert: {
  show: Boolean, 
  clear: [Function, Boolean]
}
```

注意事项
----

> 你可能需要为了与后端提供的接口返回结果一致而去修改以下代码：
> (需要注意的是，这里的修改是全局性的，意味着整个项目所有使用该 table 组件都需要遵守这个返回结果定义的字段。)
>
> 文档中的结构有可能由于组件 bug 进行修正而改动。实际修改请以当时最新版本为准

修改 `@/components/table/index.js`  第 348 行起



```javascript
const data = reactive({
		needTotalList: [],
		localLoading: false,
		localDataSource: [],
		localPagination: Object.assign({}, props.pagination),
		isFullscreen: false,
		customSize: props.compSize,
		columnsSetting: [],
		localSettings: {
			rowClassName: props.rowClassName,
			rowClassNameSwitch: Boolean(props.rowClassName)
		}
	})

// 这里的 data.xxx 是之前声明的
// 在 loadData() 方法中去获取后端数据，进行一个数据的加载更新
result.then((r) => {
				if (r == null) {
					data.localLoading = false
					return
				}
				// 获取分页数据及分页的显示内容
				data.localPagination =
					(props.showPagination &&
						Object.assign({}, data.localPagination, {
							current: r.current, // pageNo, // 返回结果中的当前分页数
							total: r.total, // totalRows, // 返回结果中的总记录数
							showSizeChanger: props.showSizeChanger,
							pageSizeOptions: props.pageSizeOptions,
							showTotal: (total, range) => {
								return `${range[0]}-${range[1]} 共 ${total} 条 `
							},
							pageSize: (pagination && pagination.pageSize) || data.localPagination.pageSize
						})) ||
					false

				// 后端数据records为null保存修复
				if (r.records == null) {
					r.records = []
				}

				// 为防止删除数据后导致页面当前页面数据长度为 0 ,自动翻页到上一页
				if (r.records.length === 0 && props.showPagination && data.localPagination.current > 1) {
					data.localPagination.current--
					loadData()
					return
				}
				
				try {
					// 当情况满足时，表示数据不满足分页大小，关闭 table 分页功能
					// 没有数据或只有一页数据时隐藏分页栏
					// if ((['auto', true].includes(props.showPagination) && r.total <= (r.pages * data.localPagination.pageSize))) {
					// 	data.localPagination.hideOnSinglePage = true
					// }
					if (!props.showPagination) {
						data.localPagination.hideOnSinglePage = true
					}
				} catch (e) {
					data.localPagination = false
				}

				// if (props.showPagination === false) {
				// 	// 既然配置了不分页，那么我们这里接收到肯定是数组
				// 	console.log(r);
				// 	data.localDataSource = []
				// 	if (r instanceof Array) {
				// 		data.localDataSource = r
				// 	}
				// } else {
				// 	data.localDataSource = r.records
				// }

				// 返回结果中的数组数据
				data.localDataSource = r.records
				data.localLoading = false
				getTableProps()
			})
```
返回 JSON 例子：
```json
{
  "message": "",
  "result": {
    "data": [{
        id: 1,
        cover: 'https://gw.alipayobjects.com/zos/rmsportal/WdGqmHpayyMjiEhcKoVE.png',
        title: 'Alipay',
        description: '那是一种内在的东西， 他们到达不了，也无法触及的',
        status: 1,
        updatedAt: '2018-07-26 00:00:00'
      },
      {
        id: 2,
        cover: 'https://gw.alipayobjects.com/zos/rmsportal/zOsKZmFRdUtvpqCImOVY.png',
        title: 'Angular',
        description: '希望是一个好东西，也许是最好的，好东西是不会消亡的',
        status: 1,
        updatedAt: '2018-07-26 00:00:00'
      },
      {
        id: 3,
        cover: 'https://gw.alipayobjects.com/zos/rmsportal/dURIMkkrRFpPgTuzkwnB.png',
        title: 'Ant Design',
        description: '城镇中有那么多的酒馆，她却偏偏走进了我的酒馆',
        status: 1,
        updatedAt: '2018-07-26 00:00:00'
      },
      {
        id: 4,
        cover: 'https://gw.alipayobjects.com/zos/rmsportal/sfjbOqnsXXJgNCjCzDBL.png',
        title: 'Snowy',
        description: '那时候我只会想自己想要什么，从不想自己拥有什么',
        status: 1,
        updatedAt: '2018-07-26 00:00:00'
      },
      {
        id: 5,
        cover: 'https://gw.alipayobjects.com/zos/rmsportal/siCrBXXhmvTQGWPNLBow.png',
        title: 'Bootstrap',
        description: '凛冬将至',
        status: 1,
        updatedAt: '2018-07-26 00:00:00'
      },
      {
        id: 6,
        cover: 'https://gw.alipayobjects.com/zos/rmsportal/ComBAopevLwENQdKWiIn.png',
        title: 'Vue',
        description: '生命就像一盒巧克力，结果往往出人意料',
        status: 1,
        updatedAt: '2018-07-26 00:00:00'
      }
    ],
    "pageSize": 10,
    "pageNo": 0,
    "totalPage": 6,
    "totalCount": 57
  },
  "status": 200,
  "timestamp": 1534955098193
}
```



更新时间
----

该文档最后更新于： 2023-12-27 PM 16:45
