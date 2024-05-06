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
package vip.xiaonuo.gen.modular.basic.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 代码生成基础添加参数
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Getter
@Setter
public class GenBasicAddParam {

    /** 主表名称 */
    @Schema(description = "主表名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "dbTable不能为空")
    private String dbTable;

    /** 主表主键 */
    @Schema(description = "主表主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "dbTableKey不能为空")
    private String dbTableKey;

    /** 插件名 */
    @Schema(description = "插件名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "pluginName不能为空")
    private String pluginName;

    /** 模块名 */
    @Schema(description = "模块名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "moduleName不能为空")
    private String moduleName;

    /** 表前缀移除 */
    @Schema(description = "表前缀移除", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "tablePrefix不能为空")
    private String tablePrefix;

    /** 生成方式 */
    @Schema(description = "生成方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "generateType不能为空")
    private String generateType;

    /** 所属模块 */
    @Schema(description = "所属模块", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "module不能为空")
    private String module;

    /** 上级目录 */
    @Schema(description = "上级目录", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "menuPid不能为空")
    private String menuPid;

    /** 移动端所属模块 */
    @Schema(description = "移动端所属模块", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mobileModule;

    /** 功能名 */
    @Schema(description = "功能名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "functionName不能为空")
    private String functionName;

    /** 业务名 */
    @Schema(description = "业务名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "busName不能为空")
    private String busName;

    /** 类名 */
    @Schema(description = "类名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "className不能为空")
    private String className;

    /** 表单布局 */
    @Schema(description = "表单布局", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "formLayout不能为空")
    private String formLayout;

    /** 使用栅格 */
    @Schema(description = "使用栅格", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "gridWhether不能为空")
    private String gridWhether;

    /** 排序 */
    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 作者名 */
    @Schema(description = "作者名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String authorName;

    /** 包名 */
    @Schema(description = "包名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String packageName;
}
