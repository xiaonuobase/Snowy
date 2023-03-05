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
package vip.xiaonuo.sys.modular.role.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色授权权限参数
 *
 * @author xuyuxiang
 * @date 2022/7/27 15:05
 **/
@Getter
@Setter
public class SysRoleGrantPermissionParam {

    /** 角色id */
    @ApiModelProperty(value = "角色id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 授权权限信息 */
    @Valid
    @ApiModelProperty(value = "授权权限信息", required = true, position = 2)
    @NotNull(message = "grantInfoList不能为空")
    private List<SysRoleGrantPermission> grantInfoList;

    /**
     * 角色授权权限类
     *
     * @author xuyuxiang
     * @date 2022/4/28 23:19
     */
    @Getter
    @Setter
    public static class SysRoleGrantPermission {

        /** 接口地址 */
        @ApiModelProperty(value = "接口地址", position = 1)
        @NotBlank(message = "apiUrl不能为空")
        private String apiUrl;

        /** 数据范围分类 */
        @ApiModelProperty(value = "数据范围分类", position = 2)
        @NotBlank(message = "scopeCategory不能为空")
        private String scopeCategory;

        /** 自定义范围组织id集合 */
        @ApiModelProperty(value = "自定义范围组织id集合", position = 3)
        @NotNull(message = "scopeDefineOrgIdList不能为空")
        private List<String> scopeDefineOrgIdList;
    }
}
