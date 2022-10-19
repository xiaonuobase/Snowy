<div align="center">
    <p align="center">
        <img src="./snowy-admin-web/public/img/logo.png" height="150" alt="logo"/>
    </p>
</div>

## 框架介绍

Snowy（SnowyAdmin）是国内首个国密前后端分离快速开发平台，集成国密加解密插件，
软件层面完全符合等保测评要求，同时实现国产化机型、中间件、数据库适配，是您的不二之选！
技术框架与密码结合，让更多的人认识密码，使用密码；更是让前后分离“密”不可分。


采用SpringBoot+MybatisPlus+AntDesignVue+Vite 等更多优秀组件及前沿技术开发，注释丰富，代码简洁，开箱即用！


Snowy谐音“小诺”，恰应小诺团队名称；意思为”下雪的、纯洁的“，寓意框架追求简洁至上，大道至简。

<p align="center">     
    <p align="center">
        <a href="https://gitee.com/xiaonuobase/snowy">
            <img src="https://gitee.com/xiaonuobase/snowy/badge/star.svg?theme=dark" alt="Gitee star">
        </a>
        <a href="https://gitee.com/xiaonuobase/snowy">
            <img src="https://gitee.com/xiaonuobase/snowy/badge/fork.svg?theme=dark" alt="Gitee fork">
        </a>
        <a href="https://www.antdv.com/docs/vue/introduce-cn/">
            <img src="https://img.shields.io/badge/vue-3.2-blue.svg" alt="bootstrap">
        </a> 
        <a href="http://spring.io/projects/spring-boot">
            <img src="https://img.shields.io/badge/vite-2.8-green.svg" alt="spring-boot">
        </a>
        <a href="https://www.antdv.com/docs/vue/introduce-cn/">
            <img src="https://img.shields.io/badge/vue--ant--design-3.2-blue.svg" alt="bootstrap">
        </a> 
        <a href="http://spring.io/projects/spring-boot">
            <img src="https://img.shields.io/badge/spring--boot-2.5-green.svg" alt="spring-boot">
        </a>
        <a href="http://mp.baomidou.com">
            <img src="https://img.shields.io/badge/mybatis--plus-3.5-blue.svg" alt="mybatis-plus">
        </a>  
        <a href="./LICENSE">
            <img src="https://img.shields.io/badge/license-Apache%202-red" alt="license Apache 2.0">
        </a>
    </p>
</p>

## 快速链接

下载地址：[https://gitee.com/xiaonuobase/snowy](https://gitee.com/xiaonuobase/snowy)

演示地址：[https://snowy.xiaonuo.vip](https://snowy.xiaonuo.vip)

文档地址：[https://xiaonuo.vip/doc](https://xiaonuo.vip/doc)

## 快速启动

全栈工程师推荐idea

### 前端支撑
| 插件 | 版本   | 用途 |
|--- | ----- | ----- |
| node.js | 最新版 |  JavaScript运行环境 |

### 启动前端

```
npm install
```
```
npm run dev
```
### 后端支撑
| 插件 | 版本 | 用途 |
| --- | ----- |  ----- |
| jdk | 11 / 1.8 |java环境 |
| lombok | idea内 |代码简化插件 |
| maven | 最新版 |包管理工具 |
| redis | 最新版 | 缓存库 |
| mysql | 8.0 / 5.7 | 数据库 |

### 启动后端
开发工具内配置好maven并在代码中配置数据库即可启动

## 代码结构

Snowy2.0框架对代码以插件化的模式进行分包，使得包层级结构更加清晰合理，同时降低了耦合度，关于插件模块化开发的规范请查阅文档【SNOWY开源文档——前端手册or后端手册——开发规范】板块。

```
snowy
  |-snowy-admin-web == 前端
    |-public == 基础静态文件
    |-src == 前端源代码
      |-api == API接口转发
      |-assets == 静态文件
      |-components == VUE组件
      |-config == 基础配置
      |-layout == 基础布局
      |-locales == 多语言配置
      |-router == 基础路由配置
      |-store == VUEX缓存配置
      |-style == 样式风格配置
      |-utils == 工具类
      |-views == 所有视图界面
  |-snowy-common == 基础通用模块
  |-snowy-plugin == 插件包
    |-snowy-plugin-auth == 登录鉴权插件
    |-snowy-plugin-biz == 业务功能插件
    |-snowy-plugin-client == C端功能插件
    |-snowy-plugin-dev == 开发工具插件
    |-snowy-plugin-sys == 系统功能插件
  |-snowy-plugin-api == 插件api包
    |-snowy-plugin-auth-api == 登录鉴权插件api接口
    |-snowy-plugin-biz-api == 业务功能插件api接口
    |-snowy-plugin-client-api == C端功能插件api接口
    |-snowy-plugin-dev-api == 开发工具插件api接口
    |-snowy-plugin-sys-api == 系统功能插件api接口
  |-snowy-web-app == 主启动模块
```


## 分支说明

- master 

正式稳定版本，具体版本升级内容看更新标签

- dev 

团队开发的分支（代码可能随时会推，不保证运行和使用）

- snowy1.8 

1.x分支，目前已停止新增功能，只限于bug的维护，推荐使用2x版本

## 视频教程

免费教程地址：[https://space.bilibili.com/50101698/channel/collectiondetail?sid=739071](https://space.bilibili.com/50101698/channel/collectiondetail?sid=739071)

<img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=4016e9m3XODiVlJlUQUS03pg7bCCXa_TYalxvdtil320XXItVMh4-gAe&name=/001.jpg"/>
<img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=902bZPTH7tjPGGSj05qVl8OHdqZFO-EBeD_OmmYnr3Bg-vUuxXnr0COr&name=/002.jpg"/>

作者也在上班工作，所以在利用休息时间为大家创作，录制视频的目的也是为各位小伙伴提供文档跟技术交流群聊之外的上手学习资料

> 视频由小诺开源技术团队王同学（每天一点）进行录制

## 效果图:fire:

<table>
    <tr>
        <td><img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=a938SjhgZ5ayRmNxjyvqNeG4piLbdyB39rdXaFyKsqCVrkmwLRyBcBc&name=/%E7%99%BB%E5%BD%95%E9%A1%B5%E9%9D%A2.png"/></td>
        <td><img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=b5e9VS9CKAeez01eHUfUdGyzm9eRSvtPrw9AF90mt_vPImvieiU9BR0&name=/%E7%B3%BB%E7%BB%9F%E9%A6%96%E9%A1%B5.png"/></td>
    </tr>
    <tr>
        <td><img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=d173c7qJ7dgrK3vN1ovs55qtuDGW6bFOdiYglAsDNCJbI1LDifNuu_E&name=/%E7%94%A8%E6%88%B7%E7%AE%A1%E7%90%86.png"/></td>
        <td><img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=0086BVQAINW_1mFSSz3Of4gsyreG3fX-6BZqiqLb0kWSXA-6ff6dD4Y&name=/%E6%9C%8D%E5%8A%A1%E7%9B%91%E6%8E%A7.png"/></td>
    </tr>
    <tr>
        <td><img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=1b72cVKHNtArl1A7qTeaAMicO1Pcv99U9PrPn4ESfwgk1VqCRmEIVqc&name=/%E6%8E%88%E6%9D%83%E6%9D%83%E9%99%90.png"/></td>
        <td><img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=1cc4CdKq2y5-hjuCfBLe5QiydnJMJfHWiM25mbobRsDBD7LK2Czkl3g&name=/%E6%93%8D%E4%BD%9C%E6%97%A5%E5%BF%97.png"/></td>
    </tr>
    <tr>
        <td><img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=f923EqvOkfbhNtN2pXA0Z55I5fRX4-_XWTmiGA8QBM_JJyIELv7ugLM&name=/EC%E6%95%A3%E7%82%B9%E5%9B%BE.png"/></td>
        <td><img src="https://pan.xiaonuo.vip/?explorer/share/file&hash=e43fxc4TEVvJCZNuBNcueFAh5Mi9CDwnc25v25krItJ0iKj1wKBnqfY&name=/%E8%8F%9C%E5%8D%95%E7%AE%A1%E7%90%86.png"/></td>
    </tr>
</table>


## 密码分步:fire:

| 功能                        | 算法类型          |
| ----------------------      | ------------- |
| 登录        | SM2前端加密，后端解密 |
| 登录登出日志        | SM2对登录登出日志做签名完整性保护存储    |
| 操作日志        | SM2对操作日志做签名完整性保护存储    |
| 用户密码        | SM3完整性保护存储，登录时做完整性校验    |
| 用户手机号        | SM4（cbc模式）加解密使用字段脱敏    |

## 官方技术群


QQ技术群：732230670

微信技术群：因群达到200人以上，需加微信拉群

<table>
    <tr>
        <td>微信群</td>
        <td><img src="https://pan.xiaonuo.vip/?explorer/share/fileOut&shareID=7qwFVcdA&path=%7BshareItemLink%3A7qwFVcdA%7D%2F" width="120"/></td>
    </tr>
</table>

## 团队成员

| 成员 | 技术 | 昵称 | 
| :---: | :---: | :---: | 
| 俞宝山 | 全栈 | 俞宝山 | 
| 徐玉祥 | 全栈 | 就是那个锅 | 
| 董夏雨 | 全栈 | 阿董 | 

## 曾获荣誉

<p align="center">
    <img src="https://pan.xiaonuo.vip/?explorer/share/fileOut&shareID=7xtGQLOA&path=%7BshareItemLink%3A7xtGQLOA%7D%2F"/>
</p>

## 版权说明

- Snowy生态技术框架全系版本采用 Apache License2.0协议

- 代码可用于个人项目等接私活或企业项目脚手架使用，Snowy全系开源版完全免费

- 二次开发如用于开源竞品请先联系群主沟通，未经审核视为侵权

- 请不要删除和修改Snowy源码头部的版权与作者声明及出处
