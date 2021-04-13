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
package vip.xiaonuo.core.consts;

/**
 * aop顺序的常量
 * <p>
 * 顺序越小越靠前
 *
 * @author yubaoshan
 * @date 2020/4/10 15:33
 */
public interface AopSortConstant {

    /**
     * 全局异常拦截器
     */
    int GLOBAL_EXP_HANDLER_AOP = -120;

    /**
     * 结果包装的aop
     */
    int WRAPPER_AOP = -110;

    /**
     * 接口资源权限校验
     */
    int PERMISSION_AOP = -100;

    /**
     * 数据范围AOP
     */
    int DATA_SCOPE_AOP = -50;

    /**
     * 多租户的aop
     */
    int TENANT_EXCHANGE_AOP = -10;

    /**
     * 多数据源切换的aop
     */
    int MULTI_DATA_SOURCE_AOP = 1;

    /**
     * 业务日志的AOP
     */
    int BUSINESS_LOG_AOP = 100;

}
