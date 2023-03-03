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
package vip.xiaonuo.sys.modular.relation.enums;

import lombok.Getter;

/**
 * 关系分类枚举
 *
 * @author xuyuxiang
 * @date 2022/4/21 19:56
 **/
@Getter
public enum SysRelationCategoryEnum {

    /** 用户工作台数据 */
    SYS_USER_WORKBENCH_DATA("SYS_USER_WORKBENCH_DATA"),

    /** 用户日程数据 */
    SYS_USER_SCHEDULE_DATA("SYS_USER_SCHEDULE_DATA"),

    /** 用户拥有资源 */
    SYS_USER_HAS_RESOURCE("SYS_USER_HAS_RESOURCE"),

    /** 用户拥有权限 */
    SYS_USER_HAS_PERMISSION("SYS_USER_HAS_PERMISSION"),

    /** 用户拥有角色 */
    SYS_USER_HAS_ROLE("SYS_USER_HAS_ROLE"),

    /** 角色拥有资源 */
    SYS_ROLE_HAS_RESOURCE("SYS_ROLE_HAS_RESOURCE"),

    /** 角色拥有移动端菜单 */
    SYS_ROLE_HAS_MOBILE_MENU("SYS_ROLE_HAS_MOBILE_MENU"),

    /** 角色拥有权限 */
    SYS_ROLE_HAS_PERMISSION("SYS_ROLE_HAS_PERMISSION");

    private final String value;

    SysRelationCategoryEnum(String value) {
        this.value = value;
    }
}
