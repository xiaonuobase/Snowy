package com.cn.xiaonuo.core.pojo.base.validate;

import lombok.Builder;
import lombok.Data;

/**
 * 校验参数时用的方法参数
 *
 * @author xuyuxiang
 * @date 2020/8/17 21:43
 */
@Data
@Builder
public class UniqueValidateParam {

    /**
     * 表名称
     */
    String tableName;

    /**
     * 列名称
     */
    String columnName;

    /**
     * 被参数校验时候的字段的值
     */
    String value;

    /**
     * 校验时，是否排除当前的记录
     */
    Boolean excludeCurrentRecord;

    /**
     * 当前记录的主键id
     */
    Long id;

    /**
     * 排除所有被逻辑删除的记录的控制
     */
    Boolean excludeLogicDeleteItems;

    /**
     * 逻辑删除的字段名
     */
    String logicDeleteFieldName;

    /**
     * 逻辑删除的字段的值
     */
    String logicDeleteValue;

}
