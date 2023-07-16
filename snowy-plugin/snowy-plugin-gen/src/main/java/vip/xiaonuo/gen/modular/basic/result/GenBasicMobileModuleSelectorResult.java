package vip.xiaonuo.gen.modular.basic.result;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 2)
    private String name;
}
