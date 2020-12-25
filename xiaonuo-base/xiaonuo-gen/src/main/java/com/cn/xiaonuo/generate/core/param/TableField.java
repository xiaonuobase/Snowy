package com.cn.xiaonuo.generate.core.param;

import lombok.Data;

/**
 * 数据库表字段实体
 *
 * @author yubaoshan
 * @date 2020年12月17日00:08:40
 */
@Data
public class TableField {

    /**
     * 字段名
     */
    public String columnName;

    /**
     * 数据库中类型
     */
    public String dataType;

    /**
     * 字段描述
     */
    public String columnComment;

    /**
     * 主外键（用来做判断的）
     */
    public String columnKey;

    /**
     * 主键（转换后的）
     */
    public String columnKeyName;

    /**
     * Java类型(String,Integer,Date等)
     */
    private String javaType;

    /**
     * 是否是主键
     */
    private Boolean primaryKeyFlag = false;
}
