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
package vip.xiaonuo.im.modular.member.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * IM-群组成员编辑参数
 *
 * @author liuchunming
 * @date  2024/05/27 16:48
 **/
@Getter
@Setter
public class ImGroupMemberEditParam {

    /** ID */
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 群id */
    @Schema(description = "群id")
    private String groupId;

    /** 用户id */
    @Schema(description = "用户id")
    private String userId;

    /** 用户类型：1-B端，2-C端 */
    @Schema(description = "用户类型：1-B端，2-C端")
    private String userType;

    /** 角色：1-群主，2-管理员，3-普通成员 */
    @Schema(description = "角色：1-群主，2-管理员，3-普通成员")
    private String role;

    /** 禁言结束时间 */
    @Schema(description = "禁言结束时间")
    private Date silenceTime;

}
