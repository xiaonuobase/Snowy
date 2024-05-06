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
package vip.xiaonuo.gen.modular.config.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 代码生成详细配置
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Getter
@Setter
@TableName("GEN_CONFIG")
public class GenConfig extends CommonEntity {

    /** id */
    @Schema(description = "id")
    private String id;

    /** 基础ID */
    @Schema(description = "基础ID")
    private String basicId;

    /** 是否主键 */
    @Schema(description = "是否主键")
    private String isTableKey;

    /** 字段 */
    @Schema(description = "字段")
    private String fieldName;

    /** 注释 */
    @Schema(description = "注释")
    private String fieldRemark;

    /** 类型 */
    @Schema(description = "类型")
    private String fieldType;

    /** 实体类型 */
    @Schema(description = "实体类型")
    private String fieldJavaType;

    /** 作用类型 */
    @Schema(description = "作用类型")
    private String effectType;

    /** 字典 */
    @Schema(description = "字典")
    private String dictTypeCode;

    /** 列表显示 */
    @Schema(description = "列表显示")
    private String whetherTable;

    /** 列省略 */
    @Schema(description = "列省略")
    private String whetherRetract;

    /** 增改 */
    @Schema(description = "增改")
    private String whetherAddUpdate;

    /** 必填 */
    @Schema(description = "必填")
    private String whetherRequired;

    /** 查询 */
    @Schema(description = "查询")
    private String queryWhether;

    /** 查询方式 */
    @Schema(description = "查询方式")
    private String queryType;

    /** 排序 */
    @Schema(description = "排序")
    private Integer sortCode;
}
