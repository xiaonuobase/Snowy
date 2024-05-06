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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

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
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 父ID */
    @Schema(description = "父ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "parentId不能为空")
    private String parentId;

    /** 名称 */
    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "title不能为空")
    private String title;

    /** 分类 */
    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "category不能为空")
    private String category;

    /** 模块 */
    @Schema(description = "模块", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "module不能为空")
    private String module;

    /** 菜单类型 */
    @Schema(description = "菜单类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "menuType不能为空")
    private String menuType;

    /** 路径 */
    @Schema(description = "路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "path不能为空")
    private String path;

    /** 图标 */
    @Schema(description = "图标", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "icon不能为空")
    private String icon;

    /** 颜色 */
    @Schema(description = "颜色", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "color不能为空")
    private String color;

    /** 规则类型 */
    @Schema(description = "规则类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "regType不能为空")
    private String regType;

    /** 可用状态 */
    @Schema(description = "可用状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "status不能为空")
    private String status;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;
}
