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
package vip.xiaonuo.dev.modular.dict.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 字典添加参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 21:48
 */
@Getter
@Setter
public class DevDictAddParam {

    /** 父id */
    @ApiModelProperty(value = "父id", position = 1)
    @NotBlank(message = "parentId不能为空")
    private String parentId;

    /** 字典文字 */
    @ApiModelProperty(value = "字典文字", position = 2)
    @NotBlank(message = "dictLabel不能为空")
    private String dictLabel;

    /** 字典值 */
    @ApiModelProperty(value = "字典值", position = 3)
    @NotBlank(message = "dictValue不能为空")
    private String dictValue;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 4)
    @NotBlank(message = "category不能为空")
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 5)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 6)
    private String extJson;
}
