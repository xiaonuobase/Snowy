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
package vip.xiaonuo.im.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * IM-消息编辑参数
 *
 * @author liuchunming
 * @date  2024/05/27 16:52
 **/
@Getter
@Setter
public class ImMessageEditParam {

    /** ID */
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 发送人id/群id */
    @Schema(description = "发送人id/群id")
    private String fromUserId;

    /** 接收人id/群id */
    @Schema(description = "接收人id/群id")
    private String toUserId;

    /** 发送人类型：1-B端，2-C端 */
    @Schema(description = "发送人类型：1-B端，2-C端")
    private String fromUserType;

    /** 接收人类型：1-B端，2-C端 */
    @Schema(description = "接收人类型：1-B端，2-C端")
    private String toUserType;

    /** 消息内容 */
    @Schema(description = "消息内容")
    private String content;

    /** 聊天类型：1-单聊，2-群聊 */
    @Schema(description = "聊天类型：1-单聊，2-群聊")
    private String chatType;

    /** 消息类型：1-文本，2-图片，3-视频，4-文件 */
    @Schema(description = "消息类型：1-文本，2-图片，3-视频，4-文件")
    private String type;

    /** 消息状态：1-已发送，2-已接收 */
    @Schema(description = "消息状态：1-已发送，2-已接收")
    private String status;

    /** 是否已读：1-已读，2-未读 */
    @Schema(description = "是否已读：1-已读，2-未读")
    private String isRead;

    /** 是否撤回：1-已撤回，2-未撤回 */
    @Schema(description = "是否撤回：1-已撤回，2-未撤回")
    private String isRecall;

}
