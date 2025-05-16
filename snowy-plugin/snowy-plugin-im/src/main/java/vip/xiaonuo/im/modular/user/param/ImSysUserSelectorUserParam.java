package vip.xiaonuo.im.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author chengchuanyao
 * @date 2025/5/16 16:20
 */
@Getter
@Setter
public class ImSysUserSelectorUserParam {

    /** 当前页 */
    @Schema(description = "当前页码")
    private Integer current;

    /** 每页条数 */
    @Schema(description = "每页条数")
    private Integer size;

    /** 组织id */
    @Schema(description = "组织id")
    private String orgId;

    /** 姓名关键词 */
    @Schema(description = "姓名关键词")
    private String searchKey;
}
