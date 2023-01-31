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
package vip.xiaonuo.mobile.modular.resource.param.menu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 移动端菜单编辑参数
 *
 * @author yubaoshan
 * @date  2023/01/28 22:42
 **/
@Getter
@Setter
public class MobileMenuEditParam {

    /** 主键 */
    @ApiModelProperty(value = "主键", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 父ID */
    @ApiModelProperty(value = "父ID", required = true, position = 2)
    @NotBlank(message = "parentId不能为空")
    private String parentId;

    /** 名称 */
    @ApiModelProperty(value = "名称", required = true, position = 3)
    @NotBlank(message = "title不能为空")
    private String title;

    /** 分类 */
    @ApiModelProperty(value = "分类", required = true, position = 4)
    @NotBlank(message = "category不能为空")
    private String category;

    /** 模块 */
    @ApiModelProperty(value = "模块", required = true, position = 5)
    @NotBlank(message = "module不能为空")
    private String module;

    /** 菜单类型 */
    @ApiModelProperty(value = "菜单类型", position = 6)
    @NotBlank(message = "menuType不能为空")
    private String menuType;

    /** 路径 */
    @ApiModelProperty(value = "路径", position = 7)
    @NotBlank(message = "path不能为空")
    private String path;

    /** 图标 */
    @ApiModelProperty(value = "图标", required = true, position = 8)
    @NotBlank(message = "icon不能为空")
    private String icon;

    /** 颜色 */
    @ApiModelProperty(value = "颜色", required = true, position = 9)
    @NotBlank(message = "color不能为空")
    private String color;

    /** 规则类型 */
    @ApiModelProperty(value = "规则类型", required = true, position = 10)
    @NotBlank(message = "regType不能为空")
    private String regType;

    /** 可用状态 */
    @ApiModelProperty(value = "可用状态", required = true, position = 11)
    @NotBlank(message = "status不能为空")
    private String status;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 12)
    private Integer sortCode;
}
