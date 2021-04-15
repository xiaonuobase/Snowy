/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.sys.core.consts;

/**
 * 系统管理异常枚举编码构成常量
 * <p>
 * 异常枚举编码由3部分组成，如下：
 * <p>
 * 模块编码（2位） + 分类编码（4位） + 具体项编码（至少1位）
 * <p>
 * 模块编码和分类编码在ExpEnumCodeConstant类中声明
 *
 * @author yubaoshan
 * @date 2020/6/19 20:46
 */
public interface SysExpEnumConstant {

    /**
     * 模块分类编码（2位）
     * <p>
     * snowy-system模块异常枚举编码
     */
    int SNOWY_SYS_MODULE_EXP_CODE = 20;

    /* 分类编码（4位） */
    /**
     * 系统应用相关异常枚举
     */
    int SYS_APP_EXCEPTION_ENUM = 1100;

    /**
     * 系统参数配置相关异常枚举
     */
    int SYS_CONFIG_EXCEPTION_ENUM = 1200;

    /**
     * 系统字典值相关异常枚举
     */
    int SYS_DICT_DATA_EXCEPTION_ENUM = 1300;

    /**
     * 系统字典类型相关异常枚举
     */
    int SYS_DICT_TYPE_EXCEPTION_ENUM = 1400;

    /**
     * 文件信息表相关枚举
     */
    int SYS_FILE_INFO_EXCEPTION_ENUM = 1500;

    /**
     * 系统菜单相关异常枚举
     */
    int SYS_MENU_EXCEPTION_ENUM = 1600;

    /**
     * 系统组织机构相关异常枚举
     */
    int SYS_ORG_EXCEPTION_ENUM = 1700;

    /**
     * 系统职位相关异常枚举
     */
    int SYS_POS_EXCEPTION_ENUM = 1800;

    /**
     * 系统角色相关异常枚举
     */
    int SYS_ROLE_EXCEPTION_ENUM = 1900;

    /**
     * 系统用户相关异常枚举
     */
    int SYS_USER_EXCEPTION_ENUM = 2000;

    /**
     * 系统通知公告相关异常枚举
     */
    int SYS_NOTICE_EXCEPTION_ENUM = 2100;

    /**
     * 定时任务相关
     */
    int TIMER_EXCEPTION_ENUM = 2200;

    /**
     * 邮件相关
     */
    int EMAIL_EXCEPTION_ENUM = 2300;

    /**
     * 短信相关
     */
    int SMS_EXCEPTION_ENUM = 2400;

    /**
     * Oauth登录相关
     */
    int OAUTH_EXCEPTION_ENUM = 2500;

}
