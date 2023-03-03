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
package vip.xiaonuo.sys.modular.user.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用户授权资源树结果
 *
 * @author xuyuxiang
 * @date 2022/7/27 15:09
 **/
@Getter
@Setter
public class SysUserGrantResourceTreeResult {

    /** 模块id */
    @ApiModelProperty(value = "模块id", position = 1)
    private String id;

    /** 模块名称*/
    @ApiModelProperty(value = "模块名称", position = 2)
    private String title;

    /** 模块图标 */
    @ApiModelProperty(value = "模块图标", position = 3)
    private String icon;

    /** 模块下菜单集合*/
    @ApiModelProperty(value = "模块下菜单集合", position = 4)
    private List<SysUserGrantResourceMenuResult> menu;

    /**
     * 授权菜单类
     *
     * @author xuyuxiang
     * @date 2022/8/13 16:54
     */
    @Getter
    @Setter
    public static class SysUserGrantResourceMenuResult {

        /** 菜单id */
        @ApiModelProperty(value = "菜单id", position = 1)
        private String id;

        /** 父id */
        @ApiModelProperty(value = "父id", position = 2)
        private String parentId;

        /** 父名称 */
        @ApiModelProperty(value = "菜单名称", position = 3)
        private String parentName;

        /** 标题 */
        @ApiModelProperty(value = "菜单标题", position = 4)
        private String title;

        /** 模块 */
        @ApiModelProperty(value = "菜单模块", position = 5)
        private String module;

        /** 菜单下按钮集合 */
        @ApiModelProperty(value = "菜单下按钮集合", position = 6)
        private List<SysUserGrantResourceButtonResult> button;

        /**
         * 授权按钮类
         *
         * @author xuyuxiang
         * @date 2022/8/13 16:54
         */
        @Getter
        @Setter
        public static class SysUserGrantResourceButtonResult {

            /** 按钮id */
            @ApiModelProperty(value = "按钮id", position = 1)
            private String id;

            /** 标题 */
            @ApiModelProperty(value = "按钮标题", position = 2)
            private String title;
        }
    }
}
