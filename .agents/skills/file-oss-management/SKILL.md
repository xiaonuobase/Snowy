---
name: file-oss-management
description: Snowy 文件上传下载与对象存储规范：DevFile 模块多后端上传 API、本地/OSS/COS/MinIO/RustFS/FTP 配置、前端 XnUpload/CropUpload 组件联动。触发场景：1) 业务需要上传文件/图片/附件 2) 下载或预览文件 3) 配置或切换文件存储后端。触发词：文件上传、上传、下载、OSS、MinIO、RustFS、FTP、阿里云、腾讯云、对象存储、XnUpload、附件、文件预览、DEV_FILE。
---

# Snowy 文件管理规范

## 架构

- 存储：dev 插件自己写的 `DevFile*Util`，封装各厂商原生 SDK（本地磁盘 / 阿里云 OSS / 腾讯云 COS / MinIO / RustFS(S3) / FTP），由 `DevFileServiceImpl` 按引擎值（`DevFileEngineTypeEnum`：LOCAL/ALIYUN/TENCENT/MINIO/RUSTFS/FTP）分派。pom 里虽声明了 x-file-storage，但代码没用它，**不要按 x-file-storage 的 API 写代码**
- 后端模块：`snowy-plugin-dev` 的 `modular/file/`（controller/entity/mapper/param/service/util/provider），元数据落 `DEV_FILE` 表
- 前端组件：`XnUpload`（通用上传）、`CropUpload`（头像裁剪）、`XnFilePreview`（预览）
- 管理界面：系统 → 基础工具 → 文件管理（文件记录的上传、查询、删除）

## 上传 API（DevFileController，路径前缀 /dev/file/）

按**后端** × **返回值** 组合成对：`upload{Backend}Return{Id|Url}`：

| 方法 | 返回 |
|---|---|
| `uploadDynamicReturnId / uploadDynamicReturnUrl` | 按系统默认存储（DEV 配置的动态后端），返回文件 id / 可访问 URL |
| `uploadImageDynamicReturnId / Url` | 图片专用（后缀白名单 jpg/jpeg/png/gif/bmp/webp，只校验后缀、不检查文件内容，按默认引擎存储） |
| `uploadDocumentDynamicReturnId / Url` | 文档专用（后缀白名单 pdf/doc/docx/xls/xlsx/ppt/pptx/txt，校验方式同上） |
| `uploadLocalReturnId / Url` | 强制本地存储 |
| `uploadAliyunReturnId / Url`、`uploadTencentReturnId / Url`、`uploadMinioReturnId / Url`、`uploadRustfsReturnId / Url`、`uploadFtpReturnId / Url` | 指定后端 |

参数一律 `@RequestPart("file") MultipartFile file`。

**返回 id 还是 Url**：需要后续管理/鉴权（可撤销、可追踪）→ 存 id（String 落业务表）；仅需展示 → Url。**推荐业务表存 id**（形如 `avatar`、`imageId` 字段），展示时经文件接口换 URL，可避免后端域名或下载签名规则变化导致已存 URL 失效。注意两种返回的下载方式不同：`*ReturnId` 上传的文件被标记为需授权下载，只能走 `authDownload`；`*ReturnUrl` 返回的是带签名的公开下载地址 `{backend-url}/dev/file/download?id=xxx&sign=xxx`。

## 业务代码用法

```java
// 一般不在后端手写上传逻辑——前端走 XnUpload 组件直接调 /dev/file/upload*，拿到 id 或 URL 存业务表（组件默认返回 URL，见下方前端联动）
// 后端需要主动上传时：
@Resource
private DevFileApi devFileApi;      // 跨插件走 dev-api
```

下载/预览（void + HttpServletResponse，见 DevFileController 尾部方法）：

| 接口 | 用法 |
|---|---|
| `/dev/file/download?id=xxx&sign=xxx` | 公开文件下载，免登录；**必须带 sign 签名参数**（平台在 `*ReturnUrl` 上传时生成并拼进返回的 URL），缺失或不符直接返回"下载链接无效"；需授权下载的文件走这里会被拒绝 |
| `/dev/file/authDownload?id=xxx` | 鉴权下载，需要登录 token（平台返回的地址形如 `...authDownload?id=xxx&token=`，由前端补上当前 token）；`*ReturnId` 上传的文件只能走这里 |

## 前端联动（详见 frontend-pc 技能）

```vue
<!-- 存文件 id：XnUpload 默认 uploadResultType='url'，存 id 必须显式传 uploadResultType="id"；
     id 模式默认走 uploadLocalReturnId（强制本地存储），要按默认引擎存储必须再传 uploadReturnIdApi -->
<xn-upload
    v-model:value="formData.imageId"
    uploadMode="image"
    :uploadNumber="1"
    uploadResultType="id"
    uploadReturnIdApi="/dev/file/uploadImageDynamicReturnId"
/>

<!-- 存 URL（组件默认模式，出厂通知公告、轮播图表单写法）：图片走图片专用接口 -->
<xn-upload
    v-model:value="formData.image"
    uploadMode="image"
    uploadDynamicReturnUrlApi="/dev/file/uploadImageDynamicReturnUrl"
/>
```

## 存储后端配置

- 各存储后端的连接参数：系统 → 系统运维 → 系统配置 →「文件配置」页签（存 DEV_CONFIG，键 `SNOWY_FILE_LOCAL_FOLDER_FOR_*` / `SNOWY_FILE_ALIYUN_*` / `SNOWY_FILE_TENCENT_*` / `SNOWY_FILE_MINIO_*` / `SNOWY_FILE_RUSTFS_*` / `SNOWY_FILE_FTP_*`），**不在 application.properties**
- 切换默认后端：系统 → 系统运维 → 系统配置 →「系统配置」页签的「默认文件引擎」（`SNOWY_SYS_DEFAULT_FILE_ENGINE`）；util 每次上传都重读配置，改完不用重启
- 上传大小限制：`spring.servlet.multipart.max-file-size=100MB`

## 常见错误正误对照

| 错误 | 正确 |
|---|---|
| 业务 Controller 自己写 MultipartFile 落盘逻辑 | 走 dev/file 上传接口（本地存储或云） |
| 业务表存云存储桶直链（如 `https://bucket.oss-xxx/...`，绕开平台，换桶就断） | 新业务优先存文件 id；平台默认返回的下载 URL（`{backend-url}/dev/file/download?id=…&sign=…`）也能用，但后端域名或下载签名规则一变就要迁移数据 |
| 前端手写 axios FormData 上传 | XnUpload / CropUpload 组件；组件覆盖不了的场景（富文本编辑器、文件管理批量上传、导入）可以 `new FormData()`，但必须调 `src/api` 下封装好的接口（如 `fileApi.fileUploadDynamicReturnUrl`） |
| 下载接口手写 response 输出流 | `/dev/file/download` 或 CommonDownloadUtil |
| 把 DEV_FILE 当业务附件表用 | 业务附件关系建自己的表（存 fileId 外键） |

## 检查清单

- [ ] 上传走 dev/file 标准接口，未自造落盘
- [ ] 新业务的附件字段优先存文件 id（varchar）；如果沿用 XnUpload 默认的 URL 模式，确认存的是平台下载地址，不是桶直链
- [ ] 前端用 XnUpload（头像用 CropUpload）
- [ ] 涉及私有文件的下载用 authDownload（鉴权）；公开文件的 download 地址带 sign
- [ ] 上传格式/大小有约束（图片/文档专用接口只按后缀白名单校验；其他接口只有 100MB 总限制，格式由前端组件或业务代码校验）

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/file/controller/DevFileController.java` | 全部上传/下载 API |
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/file/service/impl/DevFileServiceImpl.java` | 存储逻辑 |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/DevFileApi.java` | 跨插件文件接口 |
| `snowy-admin-web/src/components/XnUpload/` | 上传组件（含 README） |
| `snowy-admin-web/src/components/CropUpload/` | 裁剪上传组件 |
