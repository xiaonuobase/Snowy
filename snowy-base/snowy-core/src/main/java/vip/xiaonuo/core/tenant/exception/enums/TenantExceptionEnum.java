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
package vip.xiaonuo.core.tenant.exception.enums;

import vip.xiaonuo.core.annotion.ExpEnumType;
import vip.xiaonuo.core.exception.enums.abs.AbstractBaseExceptionEnum;
import vip.xiaonuo.core.factory.ExpEnumCodeFactory;
import vip.xiaonuo.core.tenant.consts.TenantExpEnumConstant;

/**
 * 多租户异常枚举
 *
 * @author xuyuxiang
 * @date 2020/8/24
 */
@ExpEnumType(module = TenantExpEnumConstant.TENANT_MODULE_EXP_CODE, kind = TenantExpEnumConstant.TENANT_EXCEPTION_ENUM)
public enum TenantExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 更新租户的密码错误
     */
    UPDATE_TENANT_PASSWORD_ERROR(1, "更新租户的密码错误"),

    /**
     * 多数据源模块未启用，找不到DatabaseInfoService
     */
    DBS_MODULAR_NOT_ENABLE_ERROR(2, "多数据源模块未启用，找不到DatabaseInfoService"),

    /**
     * 找不到该租户信息
     */
    CNAT_FIND_TENANT_ERROR(3, "找不到该租户信息"),

    /**
     * 多租户模块未启用，找不到TenantInfoService
     */
    TENANT_MODULE_NOT_ENABLE_ERROR(4, "多租户模块未启用，找不到TenantInfoService");

    TenantExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private final Integer code;

    private final String message;

    @Override
    public Integer getCode() {
        return ExpEnumCodeFactory.getExpEnumCode(this.getClass(), code);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
