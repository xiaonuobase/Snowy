package com.cn.xiaonuo.generate.core.enums;

import lombok.Getter;

/**
 * 代码生成过程中被过滤的字段
 *
 * @author yubaoshan
 * @date 2020年12月17日00:11:40
 */
@Getter
public enum TableFilteredFieldsEnum {

    CREATE_TIME("create_time"),
    UPDATE_TIME("update_time"),
    CREATE_USER("create_user"),
    UPDATE_USER("update_user");

    private final String propertyName;

    TableFilteredFieldsEnum(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * 是否本枚举包含该字段
     *
     * @author yubaoshan
     * @date 2020年12月17日00:11:40
     */
    public static boolean contains(String propertyName) {
        for (TableFilteredFieldsEnum gunsFilteredFieldsEnum : TableFilteredFieldsEnum.values()) {
            if (gunsFilteredFieldsEnum.propertyName.equals(propertyName)) {
                return true;
            }
        }
        return false;
    }
}
