---
name: frontend-pc
description: snowy-admin-web 前端开发规范：api js 封装约定、index.vue 列表页 + form.vue 弹窗表单三件套、s-table、Xn 组件速查、hasPerm 按钮权限、i18n、request.js 双端。触发场景：1) 写前端页面或 API 封装 2) 新业务模块的前端三件 3) 使用组件/权限/字典。触发词：前端、Vue、页面、api js、index.vue、form.vue、s-table、Xn组件、Ant Design Vue、AntdV、按钮权限、hasPerm、i18n、国际化、弹窗表单。
---

# snowy-admin-web 前端开发规范

## 技术栈与铁律

- Vue 3.5（`<script setup>` 语法）+ Vite 6 + **Ant Design Vue 4.2.6** + Pinia + vue-i18n + TailwindCSS
- **JavaScript，不是 TypeScript**——禁止 interface/type/as 等 TS 语法
- 组件自动导入（unplugin）：a-xxx 组件、ref/computed 等 API **不需要 import**；自定义组件用 kebab-case 标签
- 包管理 npm；目录 `snowy-admin-web/`

## SFC 块顺序（硬性，不可颠倒）

**每个 .vue 文件三块固定按 `template` → `script` → `style` 排列**，缺 style 可以，顺序错不行。

```vue
<template>

</template>

<script setup name="业务编码">

</script>

<style scoped>

</style>
```

| 要求 | 说明 |
|---|---|
| `<template>` 永远第一 | 先看长什么样再看怎么实现；review diff 时结构稳定可预期 |
| `<script setup name="业务编码">` 第二 | name 必填（keep-alive 缓存靠它），值为业务编码小驼峰 |
| `<style scoped>` 第三 | 默认加 `scoped`；确需全局的另起一个不带 scoped 的块，仍排在最后 |

❌ 禁止把 `<script>` 写在 `<template>` 前面（Vue 官方 SFC 也支持，但本项目统一为 template 优先，混排会让文件之间没法快速对照）。

> 存量：`views/plugin/front/` 下有 29 个文件是 script 前置写法，**不做批量回改**，新写与重构经过时按本规范来。

## 三件套结构（一个业务域）

```
snowy-admin-web/src/
├── api/biz/bizXxxApi.js          ① API 封装
└── views/biz/xxx/
    ├── index.vue                 ② 列表页
    ├── form.vue                  ③ 弹窗表单
    └── detail.vue                详情（可选）
```

## ① API 封装模板（bizNoticeApi.js 为范本）

```js
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/biz/xxx/` + url, ...arg)

/**
 * XXX Api接口管理器
 *
 * @author 你的名字
 * @date  2026/08/18 10:00
 **/
export default {
	// 获取XXX分页
	xxxPage(data) {
		return request('page', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	xxxSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除XXX
	xxxDelete(data) {
		return request('delete', data)
	},
	// 获取XXX详情
	xxxDetail(data) {
		return request('detail', data, 'get')
	}
}
```

- 第三参 method，默认 POST；GET 查询传 'get'
- 文件名 `biz{Xxx}Api.js`；缩进用 **Tab**（项目 Prettier 配置）
- 下载文件用 baseRequest 的第 4 参 `{ responseType: 'blob' }`

## ② 列表页 index.vue 骨架

```vue
<template>
	<a-card>
		<a-form ref="searchFormRef" :model="searchFormState" layout="inline">
			<!-- 搜索区：a-input / a-select / dict-select / a-range-picker -->
		</a-form>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="false"
		 bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="rowSelection"
		>
			<template #operator>
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('bizXxxAdd')">新增</a-button>
					<a-button danger @click="deleteBatchBizXxx()" v-if="hasPerm('bizXxxBatchDelete')">批量删除</a-button>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a @click="formRef.onOpen(record)" v-if="hasPerm('bizXxxEdit')">编辑</a>
					<a-divider type="vertical" v-if="hasPerm(['bizXxxEdit', 'bizXxxDelete'], 'and')" />
					<a-popconfirm title="确定删除吗？" @confirm="deleteBizXxx(record)">
						<a-button type="link" danger size="small" v-if="hasPerm('bizXxxDelete')">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
</template>

<script setup name="xxx">
	import tool from '@/utils/tool'
	import { cloneDeep } from 'lodash-es'
	import Form from './form.vue'
	import bizXxxApi from '@/api/biz/bizXxxApi'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }

	const columns = [
		{ title: '名称', dataIndex: 'name' },
		{ title: '排序', dataIndex: 'sortCode', width: 100 },
		{ title: '操作', dataIndex: 'action', align: 'center', width: 150 }
	]

	const loadData = (parameter) => {
		const searchFormParam = cloneDeep(searchFormState.value)
		// 时间范围重载：range-picker 的数组拆成 start/end 两个字段
		if (searchFormParam.createTime) {
			searchFormParam.startCreateTime = searchFormParam.createTime[0]
			searchFormParam.endCreateTime = searchFormParam.createTime[1]
			delete searchFormParam.createTime
		}
		return bizXxxApi.xxxPage(Object.assign(parameter, searchFormParam)).then((data) => data)
	}

	const deleteBizXxx = (record) => {
		bizXxxApi.xxxDelete([{ id: record.id }]).then(() => tableRef.value.refresh(true))
	}
	const deleteBatchBizXxx = (params) => {
		bizXxxApi.xxxDelete(params).then(() => tableRef.value.clearRefreshSelected())
	}

	formRef  // 模板引用（保持命名与 ref 一致）
	loadData
	deleteBizXxx
	deleteBatchBizXxx
</script>
```

要点：
- `<script setup name="xxx">` 带 name（keep-alive 需要）
- `s-table` 的 `:data="loadData"` 传函数（不是数组），内部自动管理分页参数
- 刷新：`tableRef.value.refresh(true)`（回到第一页）/ `clearRefreshSelected()`（批量删后）
- 时间范围查询必须拆 startCreateTime/endCreateTime（与后端 PageParam 对应）

## ③ 弹窗表单 form.vue 骨架

```vue
<template>
	<xn-form-container
		:title="formData.id ? '编辑XXX' : '增加XXX'"
		:width="1000"
		v-model:open="open"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
					<a-form-item label="名称：" name="name">
						<a-input v-model:value="formData.name" placeholder="请输入名称" allow-clear />
					</a-form-item>
				</a-col>
				<!-- 更多字段… -->
			</a-row>
		</a-form>
		<template #footer>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">提交</a-button>
			<a-button @click="onClose">取消</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="xxxForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import bizXxxApi from '@/api/biz/bizXxxApi'

	const open = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	const formData = ref({})
	const submitLoading = ref(false)

	// 打开（父组件 formRef.onOpen(record) 调用；record 为空 = 新增）
	const onOpen = (record) => {
		open.value = true
		if (record) {
			formData.value = Object.assign({}, cloneDeep(record))
		} else {
			formData.value = { sortCode: 99 }      // 默认值
		}
	}
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		open.value = false
	}
	const formRules = {
		name: [required('请输入名称')]
	}
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			bizXxxApi
				.xxxSubmitForm(cloneDeep(formData.value), formData.value.id)
				.then(() => {
					onClose()
					emit('successful')             // 通知父组件刷新列表
				})
				.finally(() => (submitLoading.value = false))
		})
	}
	defineExpose({ onOpen })                        // ★ 必须，父组件才能调 onOpen
</script>
```

要点：提交成功 `emit('successful')`；数组字段（多选）提交前 `JSON.stringify`（参考 notice 的 place）；字典选项 `tool.dictList('BIZ_XXX_TYPE')`。

## 注释规范（求整齐，一行到底）

**与后端 Javadoc 同一原则：一行说清，说不清就是代码该拆了。**

| 位置 | 写法 |
|---|---|
| 文件头（api js） | 四行块注释：一行中文描述 + 空行 + `@author` + `@date`（见上文模板） |
| 文件头（其他 js / vue） | 单行 `// 一句话说明本文件干什么` |
| 常量块 / 导出对象 | 上方**一行** `//` |
| 函数 / 方法 | 上方**一行** `//` |
| 行内 | 句末 `// 一句话`，别换行续写 |

### ❌ 禁止

| 禁止 | 替代 |
|---|---|
| 连续 2 行以上 `//` 堆叠成段 | 压成一行；压不下的属于设计约束，写进 `docs/` 而不是源码 |
| 解释「为什么这么设计」的长段说明 | 同上，源码只留结论 |
| JSDoc `@param` / `@returns` / `@type` | 一行 `//` 描述即可（api js 文件头的 @author/@date 除外） |
| 注释掉的废代码 | 直接删，git 里有 |

❌ 反例（`constants/enums.js` 旧写法，5 行注释配 1 行代码）：
```js
// 数据中台枚举常量，统一管理所有枚举值，避免硬编码
// 这里只放跨页面共用的值域；单个业务页自己用的选项一律放该页目录下的 xxxOptions.js，不要往这里堆

// 敏感类型枚举，取值与中文名须与后端 DataSecurityScanRuleServiceImpl.SENSITIVE_NAME_MAP 保持一致。
// 顺序即扫描规则表单下拉的展示顺序，CUSTOM 固定放末位。
// 注意：CUSTOM 规则的真实语义在规则自身的 sensitiveTypeName 上，列表展示优先取该字段
export const SensitiveTypeEnum = { ... }
```

✅ 正例（各留一行，约束信息移交文档）：
```js
// 数据中台跨页面共用枚举常量

// 敏感类型枚举，须与后端 SENSITIVE_NAME_MAP 一致
export const SensitiveTypeEnum = { ... }
```

> 压不进一行的信息（模块边界约定、跨端契约、字段优先级规则）不是删掉，是**换地方**——放模块的 `docs/` 并在 INDEX.md 挂号。源码注释负责「这是什么」，文档负责「为什么、怎么配合」。

## 按钮权限

```vue
v-if="hasPerm('bizXxxAdd')"                              // 单码
v-if="hasPerm(['bizXxxEdit', 'bizXxxDelete'], 'and')"    // 数组 + and/or（第二参默认 'or'）
```

驼峰码来自 SYS_RESOURCE 的 BUTTON 行，与后端 @SaCheckPermission 的 URL 式是**两套**（详见 security-auth 技能）。

## 高频组件速查（src/components/，37 个）

| 组件 | 用途 |
|---|---|
| `s-table` | 列表表格（分页自动） |
| `xn-form-container` | 表单弹窗容器（新增/编辑通用壳） |
| `dict-select` | 字典下拉：`<dict-select v-model:value="x" dict-type-code="BIZ_XXX" />` |
| `xn-upload` | 上传（uploadMode="image"；uploadDynamicReturnUrlApi 指定后端接口） |
| `xn-editor` / `xn-md-editor` | 富文本 / Markdown |
| `xn-user-selector` / `xn-org-selector` / `xn-role-selector` / `xn-position-selector` / `xn-group-selector` | 各类选择器 |
| `xn-tree-select` / `tree-select` | 树选择（组织/分类） |
| `xn-file-preview` | 文件预览 |
| `xn-sign-name` | 手写签名 |
| `cron` | Cron 表达式生成 |
| `crop-upload` | 头像裁剪上传 |
| `xn-resizable-panel` | 可拖拽分栏（左树右表布局用） |

（多数组件目录带 README.md，用前可读）

## 请求与工具

- `src/utils/request.js`：`baseRequest`（B 端）；`src/utils/clientRequest.js`（C 端）；token 请求头名 `token`；code!==200 自动 message.error；add/edit/delete 成功自动提示
- `src/utils/tool.js`：`tool.dictList('编码')` 取字典、`tool.dataToTree()` 树化等
- `src/utils/formRules.js`：`required('提示')` 校验工厂
- 国密：`src/utils/smCrypto.js`（登录密码 SM2 加密）

## i18n

页面文案 `$t('xxx.yyy')`，词条加 `src/locales/{zh-cn,en-us}/` 对应文件。**遵循现有 i18n 习惯**；纯业务管理页的动态数据（字典/菜单）不走 i18n。

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| TypeScript 语法 | 纯 JS |
| Element Plus（el-xxx）组件 | Ant Design Vue（a-xxx） |
| `import { ref } from 'vue'` | 自动导入，不写 |
| 自己 axios.get('/api/biz/xxx') | api js + baseRequest 封装 |
| `:data="tableData"` 数组 | s-table `:data="loadData"` 函数 |
| 表单不用容器直接弹 a-modal | `xn-form-container` |
| 忘 `defineExpose({ onOpen })` | 父组件打不开表单 |
| `<script>` 写在 `<template>` 前面 | 固定 template → script → style |
| 硬编码中文文案不进 i18n（公共区域） | $t + locales |

## 检查清单

- [ ] 每个 .vue 块顺序为 template → script → style，script 带 name
- [ ] 三件套齐全（api js / index.vue / form.vue）
- [ ] api js 的 URL 前缀与后端一致（/biz/xxx/）
- [ ] s-table loadData 模式 + 时间范围拆分
- [ ] 按钮权限 hasPerm 码与 SYS_RESOURCE BUTTON 行一致
- [ ] 提交成功 emit('successful')
- [ ] 无 TS / Element Plus 残留；Tab 缩进
- [ ] 注释每处不超过一行，无连续 `//` 段落、无 JSDoc @param/@returns、无注释掉的废代码

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-admin-web/src/api/biz/bizNoticeApi.js` | API 封装范本 |
| `snowy-admin-web/src/views/biz/notice/index.vue` | 列表页范本（含批量删/状态切换/字典） |
| `snowy-admin-web/src/views/biz/notice/form.vue` | 表单范本（含上传/富文本/多选序列化） |
| `snowy-admin-web/src/views/biz/notice/detail.vue` | 详情范本 |
| `snowy-admin-web/src/utils/request.js` | 请求封装 |
| `snowy-admin-web/src/utils/tool.js` | 前端工具 |
| `snowy-admin-web/src/components/` | 全部组件 |
