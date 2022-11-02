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
package vip.xiaonuo.gen.modular.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 代码生成基础
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Getter
@Setter
@TableName("GEN_BASIC")
public class GenBasic extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 主表名称 */
    @ApiModelProperty(value = "主表名称", position = 2)
    private String dbTable;

    /** 主表主键 */
    @ApiModelProperty(value = "主表主键", position = 3)
    private String dbTableKey;

    /** 表前缀移除 */
    @ApiModelProperty(value = "表前缀移除", position = 4)
    private String tablePrefix;

    /** 生成方式 */
    @ApiModelProperty(value = "生成方式", position = 5)
    private String generateType;

    /** 所属模块 */
    @ApiModelProperty(value = "所属模块", position = 6)
    private String module;

    /** 上级目录 */
    @ApiModelProperty(value = "上级目录", position = 7)
    private String menuPid;

    /** 功能名 */
    @ApiModelProperty(value = "功能名", position = 8)
    private String functionName;

    /** 业务名 */
    @ApiModelProperty(value = "业务名", position = 9)
    private String busName;

    /** 类名 */
    @ApiModelProperty(value = "类名", position = 10)
    private String className;

    /** 表单布局 */
    @ApiModelProperty(value = "表单布局", position = 11)
    private String formLayout;

    /** 使用栅格 */
    @ApiModelProperty(value = "使用栅格", position = 12)
    private String gridWhether;

    /** 排序 */
    @ApiModelProperty(value = "排序", position = 13)
    private Integer sortCode;

    /** 包名 */
    @ApiModelProperty(value = "包名", position = 14)
    private String packageName;

    /** 作者 */
    @ApiModelProperty(value = "作者", position = 15)
    private String authorName;
}
