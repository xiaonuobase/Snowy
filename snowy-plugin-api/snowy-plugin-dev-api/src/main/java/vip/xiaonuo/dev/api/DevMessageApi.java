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
package vip.xiaonuo.dev.api;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 站内信API接口
 *
 * @author xuyuxiang
 * @date 2022/6/22 17:33
 **/
public interface DevMessageApi {

    /**
     * 发送站内信，默认：分类系统通知
     *
     * @param receiverIdList 接收的用户id集合
     * @param subject 主题
     * @author xuyuxiang
     * @date 2022/6/22 17:35
     **/
    void sendMessage(List<String> receiverIdList, String subject);

    /**
     * 发送站内信指定分类
     *
     * @param receiverIdList 接收的用户id集合
     * @param subject 主题
     * @author xuyuxiang
     * @date 2022/6/22 17:35
     **/
    void sendMessage(List<String> receiverIdList, String category, String subject);

    /**
     * 发送站内信带内容，默认：分类系统通知
     *
     * @param receiverIdList 接收的用户id集合
     * @param subject 主题
     * @param content 站内信内容
     * @author xuyuxiang
     * @date 2022/6/22 17:35
     **/
    void sendMessageWithContent(List<String> receiverIdList, String subject, String content);

    /**
     * 发送站内信带内容，指定分类
     *
     * @param receiverIdList 接收的用户id集合
     * @param subject 主题
     * @param content 站内信内容
     * @author xuyuxiang
     * @date 2022/6/22 17:35
     **/
    void sendMessageWithContent(List<String> receiverIdList, String category, String subject, String content);

    /**
     * 获取未读站内信列表
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:48
     */
    List<JSONObject> list(List<String> receiverIdList, Integer limit);

    /**
     * 获取未读站内信数量
     *
     * @author diantu
     * @date 2023/7/10
     */
    Long unreadCount(String loginId);

    /**
     * 获取站内信分页
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:48
     */
    Page<JSONObject> page(List<String> receiverIdList, String category);

    /**
     * 获取站内信详情
     *
     * @param id 站内信id
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    JSONObject detail(String id);

    /**
     * 站内信全部标记已读
     *
     * @author diantu
     * @date 2023/7/10
     */
    void allMessageMarkRead();

}
