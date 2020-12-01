package com.cn.xiaonuo.core.dbs;

/**
 * datasource的上下文
 *
 * @author xuyuxiang
 * @date 2020/8/24
 */
public class CurrentDataSourceContext {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源类型
     *
     * @param dataSourceType 数据库类型
     * @date 2020/8/24
     */
    public static void setDataSourceType(String dataSourceType) {
        CONTEXT_HOLDER.set(dataSourceType);
    }

    /**
     * 获取数据源类型
     *
     * @author xuyuxiang
     * @date 2020/8/24
     */
    public static String getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除数据源类型
     *
     * @author xuyuxiang
     * @date 2020/8/24
     */
    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }

}
