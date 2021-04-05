<div align="center">
    <p align="center">
        <img src="./_web/public/logo.png" height="150" alt="logo"/>
    </p>
</div>

### 框架介绍
<div align="center"><h4 align="center">xiaonuo中文名称为：小诺，源于‘诺’字诚信，‘小’字衬托</h4></div>
<div align="center"><h4 align="center">小诺的初衷是以服务为重心，我们代码可以写的烂（并不烂），对同行程序员的态度那是必须友好</h4></div>
<div align="center"><h4 align="center">我们致力于做好软件脚手架产品，并为每一个需要帮助的人免费提供技术支持，开源以来也得到了很多小伙伴的认可与优秀的口碑</h4></div>
<div align="center"><h4 align="center">同时此项目名称为小诺版本的前后分离版，故名称起为xiaonuo-vue，例如：layui版本为xiaonuo-layui</h4></div>
<div align="center"><h4 align="center">xiaonuo-vue为XiaoNuo生态技术框架环境中的vue版本</h4></div>
<div align="center"><h4 align="center">前后端分离架构，开箱即用，紧随前沿技术</h4></div>

<p align="center">     
    <p align="center">
        <a href="https://www.antdv.com/docs/vue/introduce-cn/">
            <img src="https://img.shields.io/badge/vue-2.x-blue.svg" alt="bootstrap">
        </a> 
        <a href="https://www.antdv.com/docs/vue/introduce-cn/">
            <img src="https://img.shields.io/badge/vue--ant--design-2.1.0-blue.svg" alt="bootstrap">
        </a> 
        <a href="http://spring.io/projects/spring-boot">
            <img src="https://img.shields.io/badge/spring--boot-2.3.1-green.svg" alt="spring-boot">
        </a>
        <a href="http://mp.baomidou.com">
            <img src="https://img.shields.io/badge/mybatis--plus-3.3.2-blue.svg" alt="mybatis-plus">
        </a>  
        <a href="./LICENSE">
            <img src="https://img.shields.io/badge/license-Apache%202-red" alt="license Apache 2.0">
        </a>
        <a href="https://gitee.com/xiaonuobase/xiaonuo-vue">
            <img src="https://gitee.com/xiaonuobase/xiaonuo-vue/badge/star.svg?theme=dark" alt="Gitee star">
        </a>
        <a href="https://gitee.com/xiaonuobase/xiaonuo-vue">
            <img src="https://gitee.com/xiaonuobase/xiaonuo-vue/badge/fork.svg?theme=dark" alt="Gitee fork">
        </a>
        <a href="https://github.com/xiaonuobase/xiaonuo-vue">
            <img src="https://img.shields.io/github/stars/xiaonuobase/xiaonuo-vue?style=social" alt="GitHub stars">
        </a>
        <a href="https://github.com/xiaonuobase/xiaonuo-vue">
            <img src="https://img.shields.io/github/forks/xiaonuobase/xiaonuo-vue?style=social" alt="GitHub forks">
        </a>
        <a href="https://github.com/xiaonuobase/xiaonuo-vue">
            <img src="https://img.shields.io/github/repo-size/xiaonuobase/xiaonuo-vue" alt="size">
        </a>
    </p>
</p>

### 快速启动

您的开发电脑需要安装：NodeJs（最新版）、npm或yarn（最新版）建议使用yarn、Mysql5.7、Jdk1.8、Maven3.6.3（配置阿里仓库地址）、开发工具推荐idea

* 启动前端：打开_web文件夹，进行依赖下载，运行npm install或yarn命令，再运行npm run serve或 yarn run serve
* 启动后端：打开application-local中配置数据库信息，运行XiaoNuoApplication类即可启动
* 浏览器访问：http://localhost:81 （默认前端端口为：81，后端端口为：82）

### 其他版本

* layui单体版本：https://gitee.com/xiaonuobase/xiaonuo-layui
* vue前后分离版本：https://gitee.com/xiaonuobase/xiaonuo-vue
* cloud微服务前后分离版本：https://gitee.com/xiaonuobase/xiaonuo-cloud
* 我们的其他产品线同样开源，如需关注最新动态可加入QQ群聊探讨：[732230670](https://wpa.qq.com/msgrd?v=3&uin=732230670&_blank)
* 如果我们的产品能满足您的需求，很期待您给我们右上角点个 star

### 在线演示

* 账号密码：superAdmin/123456，地址：https://vue.xiaonuo.vip

### 在线文档

* https://doc.xiaonuo.vip

### 框架亮点及优势

1. 模块化架构设计，层次清晰，业务层推荐写到单独模块，框架升级不影响业务。
```
模块树
├─xiaonuo                    ->项目工程
│  ├─xiaonuo-base            ->框架基础模块
│       ├─xiaonuo-core       ->核心模块
│       ├─xiaonuo-gen        ->代码生成
│       ├─xiaonuo-system     ->基础业务
│  ├─xiaonuo-main            ->业务开始模块
│       ├─业务               ->您的业务
```
2、独创前端字典翻译

全部字典数据储存前端store，后端接口数据统一过滤器翻译

下拉框，多选框等取值只需1行代码：（'dictData'为过滤器名称，'sex'为字典类型code）返回数组字典
```
this.$options.filters['dictData']('sex')
或直接给值
{{ code | dictData }}
```

列表数据中字典翻译：（'code'为字典类型唯一code，'value'为待翻译的值）返回name
```
{{ code | dictType(value) }}
```

3、独创的数据权限范围机制

数据范围的分配也来自于给用户单独分配的数据范围，最终决定用户有几个公司的数据范围的是，用户拥有的角色的数据范围 + 用户直接分配的数据范围

若一个用户有多个角色，系统最终判定用户有哪些数据范围是以多个角色和用户数据范围的 并集 为准。

仅通过注解就可以获取当前用户的数据范围，不强制联查sql可根据业务需求极其灵活的使用
```
@DataScope
```
param类继承baseparam，使用param.getDadaScope即可获取到数据权限列表

```
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserParam extends BaseParam {
```

4、独创的文件预览系统

支持txt.doc.docx.ppt.pptx.xls.xlsx.pdf.png.jpg.jpeg.bmp.gif等

预览速度快，兼容性好，支持常见文本格式.只需在运行环境一键安装libreoffice即可，运行简单，操作方便。

```
#libreoffice文档在线预览配置
# CentOS 下安装 libreoffice：
# 安装：yum -y install libreoffice
# Linux 中文字体乱码解决：
# 1、上传 C:\Windows\Fonts 下的字体到 /usr/share/fonts/windows 目录
# 2、执行命令： chmod 644 /usr/share/fonts/windows/* && fc-cache -fv
jodconverter:
  local:
    #暂时关闭预览，启动时会有点慢
    enabled: false
    #设置libreoffice主目录 linux地址如：/usr/lib64/libreoffice
    office-home: C:\Program Files\LibreOffice
    #开启多个libreoffice进程，每个端口对应一个进程
    port-numbers: 8100
    #libreoffice进程重启前的最大进程数
    max-tasks-per-process: 100
```
5、其他优势

前后端分离架构，分离开发，分离部署，前后端互不影响。

前端技术采用vue + antdvPro + axios。

后端采用spring boot + mybatis-plus + hutool等，开源可靠。

基于spring security(jwt) + 用户UUID双重认证。

基于AOP实现的接口粒度的鉴权，最细粒度过滤权限资源。

基于hibernate validator实现的校验框架，支持自定义校验注解。

提供Request-No的响应header快速定位线上异常问题。

在线用户可查，可在线踢人，同账号登录可同时在线，可单独在线（通过系统参数配置）。

支持前端 + 后端在线代码生成。

文件，短信，缓存，邮件等，利用接口封装，方便拓展。

短信默认使用阿里云sms，缓存默认使用内存缓存。

### 框架说明及后续补充

* 纯手研发搭建框架脚手架，在自己用的时候，也为各位小伙伴打下坚固的接私活利器。
* 后续我们会行发多个版本，将适配多个数据库环境，国产化环境，并且根据多年经验会出相关系统中用到的案例，提供给大家使用！
* 如需了解我们更多，请移步官网：https://xiaonuo.vip
* 当然，有问题讨论的小伙伴还可以加入我们的QQ技术群：[732230670](https://wpa.qq.com/msgrd?v=3&uin=732230670&_blank)，一起学习讨论。

### 效果图

<table>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-62d4b535dadbfa8ff343cb290d58be43ef0.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-98b3e79f8008b6319ce6394d80172ff02a3.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2020/1208/133142_37420daa_1980003.jpeg"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2020/1208/133250_3749a395_1980003.jpeg"/></td>
    </tr>
</table>

### 详细功能

1. 主控面板、控制台页面，可进行工作台，分析页，统计等功能的展示。
2. 用户管理、对企业用户和系统管理员用户的维护，可绑定用户职务，机构，角色，数据权限等。
3. 应用管理、通过应用来控制不同维度的菜单展示。
4. 机构管理、公司组织架构维护，支持多层级结构的树形结构。
5. 职位管理、用户职务管理，职务可作为用户的一个标签，职务目前没有和权限等其他功能挂钩。
6. 菜单管理、菜单目录，菜单，和按钮的维护是权限控制的基本单位。
7. 角色管理、角色绑定菜单后，可限制相关角色的人员登录系统的功能范围。角色也可以绑定数据授权范围。
8. 字典管理、系统内各种枚举类型的维护。
9. 访问日志、用户的登录和退出日志的查看和管理。
10. 操作日志、用户的操作业务的日志的查看和管理。
11. 服务监控、服务器的运行状态，Java虚拟机信息，jvm等数据的查看。
12. 在线用户、当前系统在线用户的查看。
13. 数据监控、druid控制台功能，可查看sql的运行信息。
14. 公告管理、系统的公告的管理。
15. 文件管理、文件的上传下载查看等操作，文件可使用本地存储，阿里云oss，腾讯cos接入，支持拓展。
16. 定时任务、定时任务的维护，通过cron表达式控制任务的执行频率。
17. 系统配置、系统运行的参数的维护，参数的配置与系统运行机制息息相关。
18. 邮件发送、发送邮件功能。
19. 短信发送、短信发送功能，可使用阿里云sms，腾讯云sms，支持拓展。

### 参与贡献

- 欢迎各路英雄好汉参与xiaonuo全系版本代码贡献，期待您的加入！
- 1.  Fork 本仓库
- 2.  新建 Feat_xxx 分支
- 3.  提交代码
- 4.  新建 Pull Request

### 更新日志：

#### v1.3.3
- 1、【新增】代码生成增加部门选择、用户选择组件化
- 2、【修复】SwaggerConfig只扫描基础包下的接口
- 3、【修改】修复角色授权菜单上下级关联动作
- 4、【修复】修复token失效时间秒数设置过大时出现异常的问题，生成token时统一采用秒计算
- 5、【修复】更新菜单、机构等树时，父节点不能选择自己的子节点
- 6、【修复】修复注释错别字

#### v1.3.2
- 1、【bug修复】修复一个代码生成bug
- 2、【修复】新增数据时，若手动设置了创建人和创建时间，则不去设置创建人和创建时间
- 3、【修复】前端传入token为undefined时，判断为无效
- 4、【新增】新增XCard组件，为列表查询区域统一样式
- 5、【修改】更改所有列表界面默认为“紧凑”
- 6、【修改】更改所有列表界面查询区域与列表在不同card显示

#### v1.3.1
- 1、【更新】更换新版logo，小诺更有朝气
- 2、【更新】更新ip地址定位描述
- 3、【更新】代码生成支持oracle数据库直连，添加number类型转换，增加oracle菜单sql

#### v1.3
- 1、【更新】代码生成增强，配置每个字段作用域

#### v1.2.5
- 1、【修复】修复用户生日更新失败的bug
- 2、【更新】优化代码，pdf文件预览无需转换
- 3、【更新】MappingCache常量字段名称更正
- 4、【更新】升级验证码请求限制

#### v1.2.4
- 1、更换登录界面图片及细节调整
- 2、更新前端布局多处细节，更协调
- 3、设置顶部导航栏由64px为55px及其他细节
- 4、设置菜单栏由255px为230px及其他细节

#### v1.2.3
- 1、修复打war包出现的注入失败导致无法启动的问题
- 2、升级多个插件版本为最新

#### v1.2.2
- 1、界面列表组件新增快捷操作刷新、列设置、密度、全屏功能，实时响应
- 2、列表组件新增slot，列表头按钮放入插槽，界面更协调
- 3、功能列表代码统一调整规范

#### v1.2.1
- 1、集成 AJ-Captcha 验证码功能
- 2、调整日志统一打印格式
- 3、修复代码生成详情实现类主键问题

#### v1.2.0
- 1、适配Oracle数据库
- 2、日志列表增加时间段查询
- 3、修正邮箱数据问题

#### v1.1.1
- 1、代码生成中查询条件增加为全部字段，可根据不同程序删减代码

#### v1.1
- 1、新增代码生成器，以独立的模块存在，更加方便维护
- 2、修复多处细节问题

### 版权说明

- xiaonuo生态技术框架全系版本采用 Apache License2.0协议
- 代码可用于个人项目等接私活或企业项目脚手架使用，xiaonuo全系开源版完全免费
- 二次开发请不要删除和修改根目录下的LICENSE文件及代码头部作者声明。
- 请不要删除和修改xiaonuo源码头部的版权声明及出处。

### XiaoNuo技术团队荣誉作品

| 成员组成 | 负责内容 |
| :---: | :---: |
| 俞宝山 | 全栈 |
| 徐玉祥 | 全栈 | 
| 董夏雨 | 全栈 |
