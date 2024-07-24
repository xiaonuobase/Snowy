package vip.xiaonuo.im.modular.message.param;

import lombok.Data;

/**
 * 发送消息参数
 * @author chengchuanyao
 * @date 2024/7/23 16:38
 */
@Data
public class ImMessageSendParam {

    /**
     * 消息发送人
     */
    private String fromUserId;

    /**
     * 消息接收人id
     */
    private String toUserId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private String type;

}
