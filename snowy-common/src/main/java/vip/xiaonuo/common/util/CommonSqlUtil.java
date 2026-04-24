/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.common.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SQL工具类，处理数据库兼容性问题
 *
 * @author yubaoshan
 * @date 2026/2/12 18:43
 **/
public class CommonSqlUtil {

    /** Oracle IN子句最大元素数量限制 */
    private static final int IN_CLAUSE_LIMIT = 999;

    /**
     * 安全的IN查询，自动处理Oracle IN子句1000限制
     * 当集合大小超过999时，自动拆分为多个IN子句用OR连接：
     * (column IN (...999个) OR column IN (...999个) OR ...)
     *
     * @param wrapper MyBatis-Plus LambdaQueryWrapper
     * @param column  查询列
     * @param values  IN子句的值集合
     * @param <T>     实体类型
     */
    public static <T> void safeIn(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> column, Collection<?> values) {
        if (ObjectUtil.isEmpty(values)) {
            return;
        }
        if (values.size() <= IN_CLAUSE_LIMIT) {
            wrapper.in(column, values);
            return;
        }
        List<?> valueList = values instanceof List ? (List<?>) values : new ArrayList<>(values);
        List<? extends List<?>> partitions = CollectionUtil.split(valueList, IN_CLAUSE_LIMIT);
        wrapper.and(w -> {
            for (int i = 0; i < partitions.size(); i++) {
                if (i == 0) {
                    w.in(column, partitions.get(i));
                } else {
                    w.or().in(column, partitions.get(i));
                }
            }
        });
    }

    /**
     * 使用预计算表的子查询替代IN查询，适用于大数据量场景
     * 通过MAP表查找SCOPE_KEY，再从SCOPE表获取orgId列表：
     * column IN (SELECT ORG_ID FROM SYS_USER_DATA_SCOPE WHERE USER_ID = '{userId}'
     *   AND SCOPE_KEY = (SELECT SCOPE_KEY FROM SYS_USER_DATA_SCOPE_MAP WHERE USER_ID = '{userId}' AND API_URL = '{apiUrl}'))
     * <p>
     * 按API维度精确过滤，相同orgId集合的API共享SCOPE_KEY，大幅减少预计算表数据量。
     * SQL固定长度，不受数据量影响，数据库可缓存执行计划，走索引高效查询。
     * </p>
     *
     * @param wrapper MyBatis-Plus LambdaQueryWrapper
     * @param column  查询列
     * @param userId  当前登录用户ID
     * @param apiUrl  当前请求的API地址
     * @param <T>     实体类型
     */
    public static <T> void scopeIn(LambdaQueryWrapper<T> wrapper, SFunction<T, ?> column, String userId, String apiUrl) {
        // 防御性处理：移除单引号防止SQL注入
        String safeUserId = userId.replace("'", "");
        String safeApiUrl = apiUrl.replace("'", "");
        wrapper.inSql(column, "SELECT ORG_ID FROM SYS_USER_DATA_SCOPE WHERE USER_ID = '" + safeUserId
                + "' AND SCOPE_KEY = (SELECT SCOPE_KEY FROM SYS_USER_DATA_SCOPE_MAP WHERE USER_ID = '"
                + safeUserId + "' AND API_URL = '" + safeApiUrl + "')");
    }
}
