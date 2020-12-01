package com.cn.xiaonuo.core.tenant.exception.enums;

import com.cn.xiaonuo.core.annotion.ExpEnumType;
import com.cn.xiaonuo.core.exception.enums.abs.AbstractBaseExceptionEnum;
import com.cn.xiaonuo.core.factory.ExpEnumCodeFactory;
import com.cn.xiaonuo.core.tenant.consts.TenantExpEnumConstant;

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
