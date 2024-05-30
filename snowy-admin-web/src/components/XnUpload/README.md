## 小诺文件上传

### 说明

改组件为文件上传、支持单个、多个文件；返回id、返回数组、返回所有

@author yubaoshan

@data 2024年5月27日09:15:17

### props定义

| 序号  | 编码                        | 类型      | 说明                                                                                          | 默认                               |
|-----|---------------------------|---------|---------------------------------------------------------------------------------------------|----------------------------------|
| 1   | uploadReturnIdApi         | String  | 上传返回id接口地址                                                                                  | /dev/file/uploadLocalReturnId    |
| 2   | uploadDynamicReturnUrlApi | String  | 上传返回url接口地址                                                                                 | /dev/file/uploadDynamicReturnUrl |
| 3   | uploadIdDownloadUrl       | String  | 当上传接口为id的情况下，配置下载接口                                                                         | /dev/file/download?id=           |
| 4   | uploadMode                | String  | 上传样式或图片方式 file、drag、image                                                                   | file                             |
| 5   | uploadNumber              | Number  | 上传数量                                                                                        | 1                                |
| 6   | uploadText                | String  | 上传文字                                                                                        | 上传                               |
| 7   | uploadResultType          | String  | 上传返回分类 字符串逗号隔离或数组 interval、array                                                            | interval                         |
| 8   | showUploadList      | Boolean | 跟antdv官方一样，是否显示文件列表                                                                         | true                             |
| 9   | accept                   | String | 跟antdv官方一样，接受上传的文件类型，如果uploadMode配置了image类型，上传的必须是图片，该参数也只能配置图片的某一项或多项，具体百度查看文件上传accept类型配置 | -                                |
| 10  | completeResult                   | Boolean | 是否是完整的结果（就是文件上传返回什么，该组件返回什么，uploadResultCategory必须为array）                                   | false                            |
| 11  | value                   | String, Array | 父组件传来的参数,通过v-model:value绑定                                                                  | -                                |


### emits定义

| 序号 | 方法名    | 参数类型                                  | 说明                          |
|----|--------|---------------------------------------|-----------------------------|
| 1  | value  | 根据uploadResultType、completeResult 而定  | 当选择用户后通过v-model:value绑定到组件上 |
| 2  | onChange | 根据uploadResultType、completeResult 而定  | 通过@onChange 方法返回上传的数据       |

### slot定义

| 序号 | 插槽名    | 用途          | 
|----|--------|-------------|
| 1  | explain | 主要用于一些提示性文字 |
