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
package vip.xiaonuo.dev.modular.relation.enums;

import lombok.Getter;

/**
 * 关系分类枚举
 *
 * @author xuyuxiang
 * @date 2022/4/21 19:56
 **/
@Getter
public enum DevRelationCategoryEnum {

    /* ====文件与业务关系==== */

    /** 文件与业务默认关联关系，后续有多种类型关联，可扩展 */
    FILE_TO_BIZ_DEFAULT("FILE_TO_BIZ_DEFAULT"),

    /* ====站内信与用户关系==== */

    /** 站内信与接收用户 */
    MSG_TO_USER("MSG_TO_USER");

    private final String value;

    DevRelationCategoryEnum(String value) {
        this.value = value;
    }
}
