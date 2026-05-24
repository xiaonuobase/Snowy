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
package vip.xiaonuo.sys.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 权限转移项参数
 *
 * @author yubaoshan
 * @date 2026/05/13
 */
@Getter
@Setter
public class SysUserTransferItemParam {

    @Schema(description = "资源类型（SUBORDINATE/ROLE/TODO_TASK/DONE_TASK）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "resourceType不能为空")
    private String resourceType;

    @Schema(description = "是否全部转移")
    private Boolean transferAll;

    @Schema(description = "选中的资源ID列表（transferAll为false时使用）")
    private List<String> selectedIds;
}
