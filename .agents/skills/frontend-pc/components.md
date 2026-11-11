# snowy-admin-web 底座组件手册

> frontend-pc 技能的附录，写业务页面时按「业务场景 → 推荐组件」表选型后，到这里查参数和坑。内容以组件源码为准（部分组件 README 已过时，如 XnUpload、XnIconSelector）。

## 注册规则

- `vite.config.mjs` 用 unplugin-vue-components 深度扫描 `src/components`：`Xxx/index.vue` 以目录名注册，模板直接写 kebab 标签（`<xn-upload>`），**不需要 import**
- `src/snowy.js` 另外全局注册 `s-table`、`ellipsis`、`drag-modal`
- **唯一例外 XnMdEditor**：只有 `mdEditor.js`，必须 `import { XnMdEditor, XnMdPreview } from '@/components/XnMdEditor/mdEditor'`
- 同目录其他 `.vue` 也会被注册为全局名：`VueEsign`、`UserSelectorTest`、`Detail`（HomeCard/BizNoticeCard/detail.vue）等，业务组件**不要起这些名字**并依赖自动导入

## 选择器的三条硬规则（最常写错）

1. 业务页面给选择器传的 API 必须来自 `@/api/biz/*`；`/sys/org|user|role|position|group/**` 在后端 `GlobalConfigure` 是**超管专属**，普通用户会被拒
2. biz 选择器接口带 `@SaCheckPermission`（如 `/biz/user/userSelector`），角色要在「授权权限」勾选
3. 回显只需 v-model 里放 id，组件内部调 `/sys/userCenter/get{User|Org|Role|Position|Group}ListByIdList` 取名称（非超管专属，普通用户可用）

可直接用的 biz 接口：

| 用途 | 接口（`@/api/biz/` 下） |
|---|---|
| 机构树 | `bizUserApi.userOrgTreeSelector`、`bizOrgApi.orgTreeSelector`、`bizGroupApi.groupOrgTreeSelector` |
| 机构祖先回显（XnTreeSelect） | `bizUserApi.userGetAncestorNodes`、`bizOrgApi.orgGetAncestorNodes` |
| 用户分页 | `bizUserApi.userSelector`、`bizOrgApi.orgUserSelector`、`bizGroupApi.groupUserSelector` |
| 职位分页 | `bizUserApi.userPositionSelector`、`bizPositionApi.positionPositionSelector` |
| 角色分页 | `bizUserApi.userRoleSelector` |
| 机构分页、用户组分页 | **biz 没有**，用 XnOrgSelector / XnGroupSelector 时需自建分页接口 |

## 表单与布局

### xn-form-container（业务 form.vue / detail.vue 外壳，必用）
- 抽屉还是弹窗由系统设置 `SNOWY_FORM_STYLE` 决定（出厂 drawer），带拖拽/全屏
- props：`v-model:open`、`title`、`width`（默认 `'50%'`，抽屉最小 378，≤768 屏全屏）、`bodyStyle`，其余透传 a-modal/a-drawer（常用 `destroy-on-close`）
- 事件：`close`、`ok`、`update:open`；插槽：`title`、`footer`（弹窗另有 `insertFooter/centerFooter/appendFooter`）
- 注意：**不写 `#footer` 就没有保存按钮**，按出厂在 `#footer` 放「关闭」「保存」
```vue
<xn-form-container :title="formData.id ? '编辑通知公告' : '增加通知公告'" :width="1000" v-model:open="open" :destroy-on-close="true" @close="onClose">
```

### xn-panel（单表列表页最外层）
- props：`title`、`bordered`(true)、`padding`(24)、`shadow`、`bottomGap`(10)；插槽 `title/extra/默认/footer`；内部滚动需父容器有高度

### XnResizablePanel + xn-tree-skeleton（左树右表）
- `direction`(row/column)、`initial-size`(200)、`min-size`、`max-size`、`md`(0=小屏隐藏左侧)、`collapsible`；插槽 `left/right`；方法 `setSize/getSize/toggleCollapse`
- 骨架屏 `type`：pulse/skeleton/bounce/glow/ripple/folder，与 `a-tree`/`a-empty` 组成 v-if 链
- 照抄 `views/biz/user/index.vue`（biz/org、biz/position 同构）

### drag-modal（全局，需要拖拽/全屏的普通大弹窗）
- `v-model:open`、`title`、`width`('50%')、`fullscreen/draggable/resizable`(默认 true)；事件 `ok`、`close`（不是 cancel）
- 注意：`:footer="null"` 无效，隐藏底栏要覆盖 `#footer`；CRUD 表单仍用 xn-form-container

## 列表

### s-table（全局，业务列表标准）
- `:data="loadData"`：函数收 `{current, size, sortField, sortOrder(ascend/descend), ...}`，返回 MP Page `{records,total,current,pages}`；`showPagination=false` 时可直接返回数组（调 `/list`）
- `:row-key="(record) => record.id"`（默认 `'key'`，必改）、`columns`、`rowSelection`（传持久对象，组件会直接改 `selectedRowKeys`）、`alert`、`toolConfig {refresh,height,columnSetting,striped}`、`lineSelection`
- 插槽 `operator`（左上按钮区）、`bodyCell`；方法 `refresh(true 回第一页)`、`clearSelected()`、`clearRefreshSelected()`

### xn-batch-button（批量删除/批量操作，出厂 24 处）
- `selectedRowKeys`(必填)、`buttonName`、`icon`("DeleteOutlined")、`buttonDanger`、`isPopconFirm`(true)、`idKey`('id')
- `@batchCallBack(params)`：params 已是 `[{id}]`，直接传 `xxxApi.xxxDelete(params)`，成功后 `tableRef.value.clearRefreshSelected()`
```vue
<xn-batch-button v-if="hasPerm('bizXxxBatchDelete')" buttonName="批量删除" icon="DeleteOutlined" buttonDanger :selectedRowKeys="selectedRowKeys" @batchCallBack="deleteBatch" />
```

### ellipsis（全局，表格长文本截断）
- `:length`（必填，中文按 2 计）、`tooltip`；文本只在 setup 读一次，s-table 单元格里没问题
```vue
<ellipsis :length="70" tooltip>{{ record.className }}</ellipsis>
```

### xn-card-list / xn-data-list（卡片 / 纵向信息列表，出厂未用）
- 不能直接吃 records，需先映射：card 每项 `{title, subTitle, img, contents:[{label,value}], badge:{text,color}, record}`；data-list 每项 `{title, descriptions, contents, extra, record}`
- `page {current,size,total}` 必须原地修改同一个 reactive 对象；每项必须带 `record`（card 还必须带 `badge`）
- 事件 `action({key, record})`、`page-change(current)`

## 表单字段

| 场景 | 组件 | v-model | 要点 |
|---|---|---|---|
| 字典下拉/单选/多选 | `dict-select` | **`v-model`**（不是 `:value`）String，checkbox 为 Array | `dict-type` 必填；`option-type` dropdown/radio/button/checkbox；选项读前端缓存，只在 mounted 读一次。出厂也常用 `a-select :options="tool.dictList('X')"` |
| 从大表下拉选一条（客户/产品/主管） | `xn-page-select` | `v-model:value` String | `page-function` 必传（返回 `{records:[{id,name}],total,current}`，字段名不同先 map）；`echo-function` 回显（收 `{idList:[id]}`），不传编辑态只显示 id；依赖父级条件时 `:auto-load="false"` 后 `ref.onPage({orgId})` |
| 选单个机构/上级节点 | `xn-tree-select` | `v-model:value` String（multiple 为 Array） | `tree-api` 必传（收 `{parentId?}`，返回扁平 `{id,parentId,name,isLeaf}`）+ `ancestor-api`；注意：**不会自动加载**：新增 `nextTick(() => ref.init())`，编辑 `ref.echo([id])`，destroy-on-close 每次打开都要调 |
| 富文本 | `xn-editor` | `v-model:value` HTML | 图片/视频自动传 `/dev/file/uploadDynamicReturnUrl`，C 端或特殊存储传 `fileUploadFunction`；`placeholder` 实际不生效；后端用长文本 |
| Markdown | `XnMdEditor` / `XnMdPreview` | **`v-model`** / `:text` | 必须手动 import；图片上传自己实现 `@upload-image(event, insertImage, files)` |
| 代码/模板/JSON 编辑 | `xn-code-editor` | `v-model:value` | `language` htmlmixed/javascript/css/xml，`height`('300px')；放进弹窗/Tab 显示后调 `ref.refresh()` |
| 只读高亮 JSON/报文 | `XnHighlightjs` | — | `code` 必须是字符串（对象先 stringify），`language`、`copy` |
| Cron | `cron` | `v-model` String | 6 段（秒分时日月周）；快捷项「每周星期日零点」`0 0 0 ? * 1` 存疑，使用前自测 |
| 颜色 | `color-picker` | `v-model:value` hex | `use-type` pure/gradient/both（渐变值是 CSS 字符串） |
| 图标 | `xn-icon-selector` | `v-model:value` | `:formRef="formRef"`；注意：只刷新名为 `icon` 的字段校验。`Selector/iconSelector` 为旧版不要用 |
| 手写签名 | `XnSignName` | — | `ref.show()` 打开，`@successful(base64)`；base64 体积大，存 longtext 或转文件上传 |

### xn-upload（上传首选）
- `v-model:value`：默认 url **逗号字符串**（varchar 存）；`upload-result-type` url/id，`upload-result-category` interval(逗号串)/array，`complete-result` 需配 array
- `upload-mode` file(默认)/drag/image/video；`upload-number`（默认 1，>1 多选）、`accept`、`upload-text`
- 上传接口：url 模式 `/dev/file/uploadDynamicReturnUrl`，图片 `/dev/file/uploadImageDynamicReturnUrl`，文档 `/dev/file/uploadDocumentDynamicReturnUrl`；id 模式默认 `/dev/file/uploadLocalReturnId`（本地存储，与 url 模式存储后端可能不同）。普通登录用户可调
- 注意：上传地址 prop 只在初始化时读一次，挂载后改不生效；删光文件时回传 `undefined`
- 方法 `uploadFileList()` → `[{name,type,size,url}]`
```vue
<xn-upload v-model:value="formData.image" uploadMode="image" uploadDynamicReturnUrlApi="/dev/file/uploadImageDynamicReturnUrl" />
```

### crop-upload（头像/封面裁剪）
- `img-src`、`auto-crop-width/height`(100)；`ref.show()`，`@successful({fileName, blobData})`
- 组件不上传：自己 `new File([blobData], fileName)` 放 FormData 调接口；标题固定「头像裁剪」

## 人员 / 组织类选择器（弹窗式）

| 组件 | 必传 API | `dataType` 默认 | 已选展示 | 方法 |
|---|---|---|---|---|
| `xn-user-selector` | `org-tree-api` + `user-page-api` | **`'string'`（逗号串）**，其他任意值返回 id 数组 | 头像 + 姓名 | `showUserPlusModal(ids)` |
| `xn-org-selector` | `org-tree-api` + `org-page-api`（biz 无，需自建） | `'array'` | a-tag | `showModel(ids)` |
| `xn-position-selector` | `org-tree-api` + `position-page-api` | `'array'` | a-tag | `showModel(ids)` |
| `xn-role-selector` | `org-tree-api` + `role-page-api` | `'array'` | a-tag | `showModel(ids)` |
| `xn-group-selector` | `data-page-api`（biz 无，需自建，返回 `{records:[{id,name}],total}`） | `'array'` | a-tag | `showModel(ids)` |

- 通用 props：`radio-model`（单选）、`add-show`（显示 + 按钮）、`v-model:value`；事件 `update:value`、`onBack`
- 注意：存单个字段时显式写 `dataType="string"`（user 默认就是 string，其他默认 array，**方向相反**）
- 不在表单里用（如授权弹窗）：`:add-show="false"`，`ref.showModel(ids)` 打开，`@onBack` 取 id 数组
- 只选一个机构用 `xn-tree-select`，只下拉选一个人/职位用 `xn-page-select`，更轻
```vue
<xn-user-selector :org-tree-api="selectorApiFunction.orgTreeApi" :user-page-api="selectorApiFunction.userPageApi" :radio-model="true" v-model:value="formData.directorId" />
// const selectorApiFunction = { orgTreeApi: (p) => bizOrgApi.orgTreeSelector(p), userPageApi: (p) => bizOrgApi.orgUserSelector(p) }
```

## 展示类

| 场景 | 组件 | 要点 |
|---|---|---|
| 附件在线预览 | `xn-file-preview` | `src`（自动拼 token）或 `data`(Blob) 二选一 + `file-type`；支持图片/pdf/docx/xlsx/pptx，不支持 doc/ppt/wps/ofd；Blob 模式先 `responseType:'blob'`，后端返回 JSON 错误时交给 `downloadUtil.resultDownload` |
| 敏感区域水印 | `xn-watermark` | `content`（数组按行显示），防删除自动重建；全局水印是 App.vue 的 `a-watermark` |
| 看板指标/涨跌 | `number-info`、`trend` | number-info 的 `total/subTotal` 必须 Number；trend 默认涨红跌绿，`reverseColor` 反转 |
| 地图 | `gaode-map` / `baidu-map` | 自备 `api-key`；覆盖物在 `@complete` 后渲染；多图分别传 `mid`；百度一页只能一个；高德新 key 需业务自配 `window._AMapSecurityConfig` |

## 不要在业务页复用

| 组件 | 原因 / 替代 |
|---|---|
| HomeCard 下 13 个 `sys-*-card` / `biz-*-card` | 内部绑首页接口，sys 系列多为超管数据，仅首页用 |
| `shortcut-card`、`menu-tree-select` | 首页快捷方式/个人设置专用；业务树选择用 `xn-tree-select` |
| `xn-context-menu` | 标签栏内部组件；业务右键菜单用 `a-dropdown trigger="contextmenu"` |
| `xn-lock-screen` | App.vue 全局挂载；需要锁屏调 `hooks/useAutoLock.js` 的 `doLockScreen()` |
| `icon-selector` / `icon-mobile-selector` | 旧版，用 `xn-icon-selector` |
