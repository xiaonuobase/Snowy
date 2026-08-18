---
name: file-oss-management
description: Snowy 文件上传下载与对象存储规范：DevFile 模块多后端上传 API、本地/OSS/COS/MinIO 配置、前端 XnUpload/CropUpload 组件联动。触发场景：1) 业务需要上传文件/图片/附件 2) 下载或预览文件 3) 配置或切换文件存储后端。触发词：文件上传、上传、下载、OSS、MinIO、阿里云、腾讯云、对象存储、XnUpload、附件、文件预览、DEV_FILE。
---

# Snowy 文件管理规范

## 架构

- 存储：x-file-storage 框架，多后端可切换（本地磁盘 / 阿里云 OSS / 腾讯云 COS / MinIO / rustfs）
- 后端模块：`snowy-plugin-dev` 的 `modular/file/`（controller/entity/mapper/param/service/util/provider），元数据落 `DEV_FILE` 表
- 前端组件：`XnUpload`（通用上传）、`CropUpload`（头像裁剪）、`XnFilePreview`（预览）
- 管理界面：开发工具 → 文件管理

## 上传 API（DevFileController，路径前缀 /dev/file/）

按**后端** × **返回值** 组合成对：`upload{Backend}Return{Id|Url}`：

| 方法 | 返回 |
|---|---|
| `uploadDynamicReturnId / uploadDynamicReturnUrl` | 按系统默认存储（DEV 配置的动态后端），返回文件 id / 可访问 URL |
| `uploadImageDynamicReturnId / Url` | 图片专用（校验图片格式） |
| `uploadDocumentDynamicReturnId / Url` | 文档专用 |
| `uploadLocalReturnId / Url` | 强制本地存储 |
| `uploadAliyunReturnId / Url`、`uploadTencentReturnId / Url`、`uploadMinioReturnId / Url` 等 | 指定后端 |

参数一律 `@RequestPart("file") MultipartFile file`。

**返回 id 还是 Url**：需要后续管理/鉴权（可撤销、可追踪）→ 存 id（String 落业务表）；仅需展示 → Url。**推荐业务表存 id**（形如 `avatar`、`imageId` 字段），展示时经文件接口换 URL，可防直链失效。

## 业务代码用法

```java
// 一般不在后端手写上传逻辑——前端走 XnUpload 组件直接调 /dev/file/upload*，拿到 id 存业务表
// 后端需要主动上传时：
@Resource
private DevFileApi devFileApi;      // 跨插件走 dev-api
```

下载/预览：`/dev/file/download`、`/dev/file/authDownload`（鉴权下载，void + HttpServletResponse，见 DevFileController 尾部方法）。

## 前端联动（详见 frontend-pc 技能）

```vue
<XnUpload
    :upload-number="1"
    v-model:value="formData.imageId"       <!-- 业务字段存文件 id -->
/>
```

## 存储后端配置

- 本地/各云的连接参数：`application.properties` 的 `snowy.file-storage.*` 段 + 开发工具→文件管理里配置（存 DEV_CONFIG）
- 切换默认后端：文件管理界面设置；改配置后无需重启（动态）
- 上传大小限制：`spring.servlet.multipart.max-file-size=100MB`

## 常见错误正误对照

| ❌ | ✅ |
|---|---|
| 业务 Controller 自己写 MultipartFile 落盘逻辑 | 走 dev/file 上传接口（本地存储或云） |
| 业务表存完整外链 URL（换桶全断） | 存文件 id，展示时换 URL |
| 前端手写 axios FormData 上传 | XnUpload / CropUpload 组件 |
| 下载接口手写 response 输出流 | `/dev/file/download` 或 CommonDownloadUtil |
| 把 DEV_FILE 当业务附件表用 | 业务附件关系建自己的表（存 fileId 外键） |

## 检查清单

- [ ] 上传走 dev/file 标准接口，未自造落盘
- [ ] 业务表存的是文件 id（varchar）
- [ ] 前端用 XnUpload（头像用 CropUpload）
- [ ] 涉及私有文件的下载用 authDownload（鉴权）
- [ ] 上传格式/大小有约束（图片接口自动校验图片）

## 参考实现

| 文件 | 说明 |
|---|---|
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/file/controller/DevFileController.java` | 全部上传/下载 API |
| `snowy-plugin/snowy-plugin-dev/src/main/java/vip/xiaonuo/dev/modular/file/service/impl/DevFileServiceImpl.java` | 存储逻辑 |
| `snowy-plugin-api/snowy-plugin-dev-api/src/main/java/vip/xiaonuo/dev/api/DevFileApi.java` | 跨插件文件接口 |
| `snowy-admin-web/src/components/XnUpload/` | 上传组件（含 README） |
| `snowy-admin-web/src/components/CropUpload/` | 裁剪上传组件 |
