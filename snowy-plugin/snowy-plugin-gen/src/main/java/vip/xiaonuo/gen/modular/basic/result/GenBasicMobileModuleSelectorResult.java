package vip.xiaonuo.gen.modular.basic.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 代码生成模块所需要用到的移动端模块选择的结果
 *
 * @author 每天一点
 * @date 2023/7/15 22:28
 **/
@Getter
@Setter
public class GenBasicMobileModuleSelectorResult {
    /** id */
    @Schema(description = "id")
    private String id;

    /** 名称 */
    @Schema(description = "名称")
    private String name;
}
