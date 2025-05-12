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
package vip.xiaonuo.client.core.enums;

import lombok.Getter;

/**
 * 密码复杂度类型枚举
 *
 * @author xuyuxiang
 * @date 2023/3/3 10:40
 **/
@Getter
public enum ClientPasswordComplexityEnum {

    /**
     * 无限制
     */
    REG0("REG0", "无限制"),

    /**
     * 必须包含数字和字母
     */
    REG1("REG1", "必须包含数字和字母"),

    /**
     * 必须包含数字和大写字母
     */
    REG2("REG2", "必须包含数字和大写字母"),

    /**
     * 必须包含数字、大写字母、小写字母和特殊字符
     */
    REG3("REG3", "必须包含数字、大写字母、小写字母和特殊字符"),

    /**
     * 至少包含数字、字母和特殊字符中的两种
     */
    REG4("REG4", "至少包含数字、字母和特殊字符中的两种"),

    /**
     * 至少包含数字、大写字母、小写字母和特殊字符的三种
     */
    REG5("REG5", "至少包含数字、大写字母、小写字母和特殊字符的三种");

    private final String value;

    private final String message;

    ClientPasswordComplexityEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }
}
