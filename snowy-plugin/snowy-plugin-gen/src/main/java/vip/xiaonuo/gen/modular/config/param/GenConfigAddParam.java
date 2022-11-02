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
package vip.xiaonuo.gen.modular.config.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 代码生成详细配置添加参数
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Getter
@Setter
public class GenConfigAddParam {

    /** 基础ID */
    @ApiModelProperty(value = "基础ID", required = true, position = 1)
    @NotBlank(message = "basicId不能为空")
    private String basicId;

    /** 是否是主键 */
    @ApiModelProperty(value = "是否是主键", required = true, position = 2)
    @NotBlank(message = "isTableKey不能为空")
    private String isTableKey;

    /** 字段 */
    @ApiModelProperty(value = "字段", required = true, position = 3)
    @NotBlank(message = "fieldName不能为空")
    private String fieldName;

    /** 注释 */
    @ApiModelProperty(value = "注释", required = true, position = 4)
    @NotBlank(message = "fieldRemark不能为空")
    private String fieldRemark;

    /** 类型 */
    @ApiModelProperty(value = "类型", required = true, position = 5)
    @NotBlank(message = "fieldType不能为空")
    private String fieldType;

    /** 实体类型 */
    @ApiModelProperty(value = "实体类型", position = 6)
    private String fieldJavaType;

    /** 作用类型 */
    @ApiModelProperty(value = "作用类型", position = 7)
    private String effectType;

    /** 字典 */
    @ApiModelProperty(value = "字典", position = 8)
    private String dictTypeCode;

    /** 列表显示 */
    @ApiModelProperty(value = "列表显示", position = 9)
    private String whetherTable;

    /** 列省略 */
    @ApiModelProperty(value = "列省略", position = 10)
    private String whetherRetract;

    /** 增改 */
    @ApiModelProperty(value = "增改", position = 11)
    private String whetherAddUpdate;

    /** 必填 */
    @ApiModelProperty(value = "必填", position = 12)
    private String whetherRequired;

    /** 查询 */
    @ApiModelProperty(value = "查询", position = 13)
    private String queryWhether;

    /** 查询方式 */
    @ApiModelProperty(value = "查询方式", position = 14)
    private String queryType;

    /** 排序 */
    @ApiModelProperty(value = "排序", position = 15)
    private Integer sortCode;
}
