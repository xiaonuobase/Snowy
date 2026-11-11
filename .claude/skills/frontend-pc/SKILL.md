---
name: frontend-pc
description: snowy-admin-web 前端开发规范：api js 封装约定、index.vue 列表页 + form.vue 弹窗表单三件套、s-table、底座组件选型（附 components.md 组件手册）、hasPerm 按钮权限、i18n、request.js 双端。触发场景：1) 写前端页面或 API 封装 2) 新业务模块的前端三件 3) 使用组件/权限/字典。触发词：前端、Vue、页面、api js、index.vue、form.vue、s-table、Xn组件、底座组件、上传、选人、选择器、树选择、分页下拉、Ant Design Vue、AntdV、按钮权限、hasPerm、i18n、国际化、弹窗表单。
---

# snowy-admin-web 前端开发规范

## 技术栈与铁律

- Vue 3.5（`<script setup>` 语法）+ Vite 6 + **Ant Design Vue 4.2.6** + Pinia + vue-i18n + TailwindCSS
- **JavaScript，不是 TypeScript**——禁止 interface/type/as 等 TS 语法（存量例外：views/front/index.vue 用了 `lang="ts"`，不回改；新代码一律用 JS）
- 组件自动导入（unplugin）：a-xxx 组件、ref/computed 等 API **不需要 import**；`src/components` 下自动注册的全局组件用 kebab-case（`<xn-form-container>`、`<s-table>`），页面内 import 的局部组件用 PascalCase（`<Form ref="formRef" />`，避免和原生 `<form>` 冲突）
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
| `<script setup name="业务编码">` 第二 | name 必填（路由页面的 keep-alive 靠它，值要和菜单 SYS_RESOURCE.NAME 别名一致，如 notice），页面用业务编码小驼峰；src/components 下的通用组件沿用 PascalCase（如 XnEditor）。存量中缺 name 的文件不批量回改 |
| `<style scoped>` 第三 | 默认加 `scoped`；确需全局的另起一个不带 scoped 的块，仍排在最后 |

禁止把 `<script>` 写在 `<template>` 前面（Vue 官方 SFC 也支持，但本项目统一为 template 优先，混排会让文件之间没法快速对照）。

> 存量：出厂有 7 个文件是 script 前置写法（layout/components/bindEmail.vue、bindPhone.vue，views/auth/login/register.vue，views/auth/client/register/index.vue，views/front/index.vue，views/sys/user/userTab/bindForm/bindEmail.vue、bindPhone.vue），**不做批量回改**，新写或重构经过时按本规范来。

## 三件套结构（一个业务域）

```
snowy-admin-web/src/
├── api/biz/bizXxxApi.js          (1) API 封装
└── views/biz/xxx/
    ├── index.vue                 (2) 列表页
    ├── form.vue                  (3) 弹窗表单
    └── detail.vue                详情（可选）
```

## 1. API 封装模板（bizNoticeApi.js 为范本）

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

## 2. 列表页 index.vue 骨架

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
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
		>
			<template #operator>
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()" v-if="hasPerm('bizXxxAdd')">新增</a-button>
					<xn-batch-button v-if="hasPerm('bizXxxBatchDelete')" buttonName="批量删除" icon="DeleteOutlined" buttonDanger :selectedRowKeys="selectedRowKeys" @batchCallBack="deleteBatchBizXxx" />
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
	<Form ref="formRef" @successful="tableRef.refresh()" />
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
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		alert: { show: false, clear: () => { selectedRowKeys.value = [] } },
		rowSelection: { onChange: (keys) => { selectedRowKeys.value = keys } }
	}

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
</script>
```

要点：
- `<script setup name="xxx">` 带 name（keep-alive 需要，值与菜单 SYS_RESOURCE.NAME 别名一致）
- import 的 `Form` 必须挂载到模板（`<Form ref="formRef" @successful="tableRef.refresh()" />`），否则 formRef 为 undefined，点"新增"报错；批量删除用 `xn-batch-button`，它把 `selectedRowKeys` 转成 `[{ id }]` 经 `@batchCallBack` 传给删除方法
- `s-table` 的 `:data="loadData"` 传函数（不是数组），内部自动管理分页参数
- 刷新：`tableRef.value.refresh(true)`（回到第一页）/ `clearRefreshSelected()`（批量删后）
- 时间范围查询必须拆 startCreateTime/endCreateTime（与后端 PageParam 对应）

## 3. 弹窗表单 form.vue 骨架

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
	defineExpose({ onOpen })                        // 必须，父组件才能调 onOpen
</script>
```

要点：提交成功 `emit('successful')`；后端 Param 字段是 String（存 JSON）的多选值，提交前 `JSON.stringify`、回显时 `JSON.parse`（参考 notice 的 place），后端是 List 的字段直接传数组；字典选项 `tool.dictList('BIZ_XXX_TYPE')`。

## 注释规范（求整齐，一行到底）

**与后端 Javadoc 同一原则：一行说清，说不清就是代码该拆了。**

| 位置 | 写法 |
|---|---|
| 文件头（api js） | 四行块注释：一行中文描述 + 空行 + `@author` + `@date`（见上文模板） |
| 文件头（其他 js） | 已有的 Apache 版权块注释**必须原样保留**（不得删改）；新建 js 文件头可以写一行 `// 一句话说明本文件干什么` |
| 文件头（vue） | 不写文件头，第一行就是 `<template>` |
| 常量块 / 导出对象 | 上方**一行** `//` |
| 函数 / 方法 | 上方**一行** `//` |
| 行内 | 句末 `// 一句话`，别换行续写 |

### 禁止

| 禁止 | 替代 |
|---|---|
| 连续 2 行以上 `//` 堆叠成段 | 压成一行；压不下的属于设计约束，写进 `docs/` 而不是源码 |
| 解释「为什么这么设计」的长段说明 | 同上，源码只留结论 |
| JSDoc `@param` / `@returns` / `@type` | 一行 `//` 描述即可（api js 文件头的 @author/@date 除外） |
| 注释掉的废代码 | 直接删，git 里有 |

以上规则针对新写或修改的代码；出厂存量（部分文件有连续 `//`、少数文件有 JSDoc、若干注释掉的语句）不做批量回改。压不进一行的信息（模块边界、跨端契约）放模块 `docs/` 并在 INDEX.md 挂号，源码注释只说「这是什么」。

## 按钮权限

```vue
v-if="hasPerm('bizXxxAdd')"                              // 单码
v-if="hasPerm(['bizXxxEdit', 'bizXxxDelete'], 'and')"    // 数组 + and/or（第二参默认 'or'）
```

驼峰码来自 SYS_RESOURCE 的 BUTTON 行，与后端 @SaCheckPermission 的 URL 式是**两套**（详见 security-auth 技能）。

## 底座组件选型（写页面前先查，硬性）

**底座有现成组件的场景，禁止手写同类实现或引第三方库**（自己拼 a-modal 选人、自己写 a-upload、自己封分页下拉等）。下表选型后，到同目录 `components.md` 查参数、出厂示例与坑（以源码为准，部分组件 README 已过时）。组件均自动注册，模板直接写 kebab 标签不用 import，**唯一例外 XnMdEditor 必须手动 import**。

| 业务场景 | 推荐组件 | 一句话要点 |
|---|---|---|
| 列表分页/排序/勾选 | `s-table` | `:data="loadData"`、`:row-key="(record) => record.id"` |
| 批量删除/批量操作 | `xn-batch-button` | `@batchCallBack` 参数已是 `[{id}]`，直接给 delete 接口 |
| 列表页外层 / 左树右表 | `xn-panel` / `XnResizablePanel` + `xn-tree-skeleton` | 左树右表照抄 `views/biz/user/index.vue` |
| 新增/编辑/详情外壳 | `xn-form-container` | 自己写 `#footer` 保存按钮；需拖拽全屏的普通大弹窗用 `drag-modal` |
| 表格长文本 | `ellipsis` | `:length` 必填 |
| 字典下拉/单选/多选 | `dict-select` 或 `a-select :options="tool.dictList('X')"` | dict-select 用 `v-model`（不是 `v-model:value`） |
| 上传附件/图片 | `xn-upload` | 默认回传 url 逗号字符串；图片配 `/dev/file/uploadImageDynamicReturnUrl` |
| 头像/封面裁剪 | `crop-upload` | 只回传 Blob，上传自己调接口 |
| 附件预览 | `xn-file-preview` | 不支持 doc/ppt/wps/ofd |
| 富文本 / Markdown | `xn-editor` / `XnMdEditor` | Markdown 需手动 import、用 `v-model` |
| 代码/模板/JSON 编辑；只读高亮 | `xn-code-editor`；`XnHighlightjs` | 弹窗里显示后调 `refresh()`；高亮的 `code` 必须是字符串 |
| 选人（带头像，单/多选） | `xn-user-selector` | `dataType` 默认 `'string'`，与其他选择器相反 |
| 下拉选一条（主管/客户/产品等大表） | `xn-page-select` | 传 `page-function` + `echo-function`，选项字段固定 `id/name` |
| 选单个机构 / 上级节点 | `xn-tree-select` | 不会自动加载：新增调 `init()`、编辑调 `echo([id])` |
| 多选机构 / 职位 / 角色 / 用户组 | `xn-org-selector` / `xn-position-selector` / `xn-role-selector` / `xn-group-selector` | 存单字段写 `dataType="string"`；机构分页、用户组分页 biz 无现成接口需自建 |
| Cron / 颜色 / 图标 / 签名 | `cron` / `color-picker` / `xn-icon-selector` / `XnSignName` | 图标组件只刷新名为 `icon` 的字段校验 |
| 看板指标 / 卡片列表 / 信息列表 | `number-info` + `trend` / `xn-card-list` / `xn-data-list` | 列表类需先把 records 映射成组件结构 |
| 敏感区域水印 / 地图 | `xn-watermark` / `gaode-map`、`baidu-map` | 地图自备 key，`@complete` 后再渲染 |

**选择器 API 硬规则**：业务页传给选择器的接口必须来自 `@/api/biz/*`（如 `bizUserApi.userOrgTreeSelector`、`bizUserApi.userSelector`），**禁止传 `@/api/sys/*`**——`/sys/org|user|role|position|group/**` 是超管专属，普通用户会被拒；biz 选择器接口带 `@SaCheckPermission`，要给角色授权。

不要在业务页复用：首页 `sys-*-card`/`biz-*-card`、`shortcut-card`、`menu-tree-select`、`xn-context-menu`、`xn-lock-screen`、旧版 `icon-selector`（替代见 components.md 末节）。

## 请求与工具

- `src/utils/request.js`：`baseRequest`（B 端）；`src/utils/clientRequest.js`：`clientBaseRequest`（C 端，读取 CLIENT_TOKEN；出厂 C 端登录、个人中心 API 已用它，新写的 C 端接口照此）；token 请求头名 `token`；code!==200 和网络异常（超时、断网、HTTP 非 2xx）都会自动 message.error，页面不要再自己弹一次，个别请求不想提示时在 baseRequest 第 4 参传 `{ skipErrorMessage: true }`；add/edit/delete 成功自动提示
- 接口地址：`.env.production` 的 `VITE_API_BASEURL` 只认两种写法——`/api`（默认，部署时由 nginx 反代并去掉 /api 前缀）或完整地址 `http(s)://...`（直连后端）；`VITE_PROXY_TARGET` 只供 `npm run prod`、`npm run preview` 的本地代理使用。代码里拼接口地址统一用 request.js 导出的 `getApiBaseUrl()`，禁止写死 `/api` 或 82 端口
- `src/utils/tool.js`：`tool.dictList('编码')` 取字典选项、`tool.dictTypeData('编码', 值)` 把字典值转成文字、`tool.translateTree(顶级字典值, 值)` 做树形字典翻译、`tool.data.get/set` 读写本地缓存（前端没有通用的树化工具）
- `src/utils/formRules.js`：`required('提示')` 校验工厂
- 国密：`src/utils/smCrypto.js`（登录密码 SM2 加密）

## i18n

业务页面直接写中文，**不强制**国际化（biz/dev/gen/mobile 页面和 layout 公共区域现状均为中文直写）；vue-i18n 只用于 auth 登录/找回密码页和 sys/user 列表，在这些已接入的页面改动时延续 `$t('xxx.yyy')`，词条加到 `src/locales/lang/zh-cn.js` 和 `src/locales/lang/en.js`（语言键 `zh-cn` / `en`）。纯业务管理页的动态数据（字典/菜单）不走 i18n。

## 常见错误正误对照

TS、Element Plus、块顺序见上方铁律与 SFC 块顺序，不重复。

| 错误 | 正确 |
|---|---|
| 新代码手写 `import { ref } from 'vue'`（冗余） | 自动导入，不写 |
| 自己 axios.get('/api/biz/xxx') | api js + baseRequest 封装 |
| `:data="tableData"` 数组 | s-table `:data="loadData"` 函数 |
| 业务主表单直接弹 a-modal | `xn-form-container` |
| 手写 a-modal + a-table 选人/选机构、手写 a-upload、手写分页下拉 | 用底座组件（见上方选型表） |
| 选择器传 `@/api/sys/*` 接口 | 传 `@/api/biz/*` 选择器接口 |
| 忘 `defineExpose({ onOpen })` | 父组件打不开表单 |
| 在已接入 i18n 的页面（auth 登录/找回密码、sys/user 列表）里新增硬编码中文 | 延续该页的 `$t('xxx')` 并补 locales 词条；其他页面直接写中文 |
| `v-html="formData.content"` 直接渲染富文本 | 先消毒：`const safeContent = computed(() => DOMPurify.sanitize(formData.value.content \|\| ''))`，模板 `v-html="safeContent"`（参考 views/biz/notice/detail.vue） |

> 存量说明：出厂部分文件（多在 components/）保留了显式 `import { ref } from 'vue'`，不回改；在已打开的抽屉或容器里再弹出的二级子表单可以用 a-modal（出厂 sys/resource/button/form.vue 等 4 处）。

## 检查清单

- [ ] 每个 .vue 块顺序为 template → script → style，script 带 name
- [ ] 三件套齐全（api js / index.vue / form.vue）
- [ ] api js 的 URL 前缀与后端一致（如 /biz/xxx/，自建插件用自己的前缀）
- [ ] s-table loadData 模式 + 时间范围拆分
- [ ] 按钮权限 hasPerm 码与 SYS_RESOURCE BUTTON 行一致
- [ ] 提交成功 emit('successful')
- [ ] 上传/选人/选机构/树选择/分页下拉/批量按钮等场景已用底座组件，未手写同类实现；选择器 API 来自 `@/api/biz/*`
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
| `.agents/skills/frontend-pc/components.md` | 底座组件手册（参数/示例/坑） |
| `snowy-admin-web/src/views/biz/user/index.vue`、`form.vue` | 左树右表、xn-tree-select、xn-page-select、xn-role-selector 范本 |

> notice 范本里有出厂历史写法，**改名照抄前先核对**：按钮码 `bizNoticerUpdateStatus`（多一个 r，与 SQL 一致，新模块写正确拼写如 `bizXxxUpdateStatus`）、`selectedRowKeys.value = ref([])`（应为 `selectedRowKeys.value = []`）、`Object.assign(parameter, searchFormParam)` 会改写 s-table 传入对象（推荐 `Object.assign({}, parameter, searchFormParam)`）。
| `snowy-admin-web/src/components/` | 全部组件 |
