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
package vip.xiaonuo.gen.modular.basic.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 代码生成类型枚举
 *
 * @author xuyuxiang
 * @date 2026/04/08
 **/
@Getter
public enum GenCategoryEnum {

    /** 单表格 */
    TABLE("TABLE", "单表格"),

    /** 单树表 */
    TREE("TREE", "单树表"),

    /** 左树右表（双表） */
    LEFT_TREE_TABLE("LEFT_TREE_TABLE", "左树右表"),

    /** 主子表（双表） */
    MASTER_DETAIL("MASTER_DETAIL", "主子表");

    private final String value;

    private final String description;

    GenCategoryEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static void validate(String value) {
        boolean flag = TABLE.getValue().equals(value) || TREE.getValue().equals(value)
                || LEFT_TREE_TABLE.getValue().equals(value) || MASTER_DETAIL.getValue().equals(value);
        if (!flag) {
            throw new CommonException("不支持的代码生成类型：{}", value);
        }
    }

    /**
     * 是否为双表类型
     */
    public static boolean isDualTable(String value) {
        return LEFT_TREE_TABLE.getValue().equals(value) || MASTER_DETAIL.getValue().equals(value);
    }

    /**
     * 是否为树类型（单树表或左树右表）
     */
    public static boolean isTreeType(String value) {
        return TREE.getValue().equals(value) || LEFT_TREE_TABLE.getValue().equals(value);
    }
}
