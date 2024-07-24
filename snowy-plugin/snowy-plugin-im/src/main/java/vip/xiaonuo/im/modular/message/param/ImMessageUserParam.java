package vip.xiaonuo.im.modular.message.param;

import lombok.Data;

import java.util.Date;

/**
 * 用户消息列表
 *
 * @author chengchuanyao
 * @date 2024/7/19 18:25
 */
@Data
public class ImMessageUserParam {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 消息
     */
    private String content;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 发送时间
     */
    private Date createTime;

    /**
     * 未读条数
     */
    private Integer unreadCount;
}
