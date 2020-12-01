package com.cn.xiaonuo.core.tenant.context;

/**
 * 租户对应的数据库的临时存放
 *
 * @author xuyuxiang
 * @date 2019-06-19-14:08
 */
public class TenantDbNameHolder {

    private static final ThreadLocal<String> DB_NAME_HOLDER = new ThreadLocal<>();

    public static void put(String value) {
        DB_NAME_HOLDER.set(value);
    }

    public static String get() {
        return DB_NAME_HOLDER.get();
    }

    public static void remove() {
        DB_NAME_HOLDER.remove();
    }
}
