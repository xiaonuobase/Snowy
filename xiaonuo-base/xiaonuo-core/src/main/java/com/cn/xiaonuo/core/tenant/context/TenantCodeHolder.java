package com.cn.xiaonuo.core.tenant.context;

/**
 * 租户编码的临时存放
 *
 * @author xuyuxiang
 * @date 2019-06-19-14:08
 */
public class TenantCodeHolder {

    private static final ThreadLocal<String> TENANT_CODE_HOLDER = new ThreadLocal<>();

    public static void put(String value) {
        TENANT_CODE_HOLDER.set(value);
    }

    public static String get() {
        return TENANT_CODE_HOLDER.get();
    }

    public static void remove() {
        TENANT_CODE_HOLDER.remove();
    }
}
