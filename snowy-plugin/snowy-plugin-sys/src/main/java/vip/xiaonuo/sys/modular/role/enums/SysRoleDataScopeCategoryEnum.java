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
package vip.xiaonuo.sys.modular.role.enums;

import lombok.Getter;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 给角色授权权限对应的数据范围枚举
 *
 * @author xuyuxiang
 * @date 2022/4/21 19:56
 **/
@Getter
public enum SysRoleDataScopeCategoryEnum {

    /** 全部 */
    SCOPE_ALL("SCOPE_ALL"),

    /** 仅自己 */
    SCOPE_SELF("SCOPE_SELF"),

    /** 仅所属组织 */
    SCOPE_ORG("SCOPE_ORG"),

    /** 所属组织及以下 */
    SCOPE_ORG_CHILD("SCOPE_ORG_CHILD"),

    /** 自定义组织 */
    SCOPE_ORG_DEFINE("SCOPE_ORG_DEFINE");

    private final String value;

    SysRoleDataScopeCategoryEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = SCOPE_ALL.getValue().equals(value) || SCOPE_SELF.getValue().equals(value) ||
                SCOPE_ORG.getValue().equals(value) || SCOPE_ORG_CHILD.getValue().equals(value) ||
                SCOPE_ORG_DEFINE.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的数据范围：{}", value);
        }
    }
}
