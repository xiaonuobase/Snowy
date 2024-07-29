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
package vip.xiaonuo.im.modular.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.im.modular.message.param.ImMessageUserParam;
import vip.xiaonuo.im.modular.message.entity.ImMessage;
import vip.xiaonuo.im.modular.message.param.ImMessageAddParam;
import vip.xiaonuo.im.modular.message.param.ImMessageEditParam;
import vip.xiaonuo.im.modular.message.param.ImMessageIdParam;
import vip.xiaonuo.im.modular.message.param.ImMessagePageParam;

import java.util.List;

/**
 * IM-消息Service接口
 *
 * @author liuchunming
 * @date  2024/05/27 16:52
 **/
public interface ImMessageService extends IService<ImMessage> {

    /**
     * 获取IM-消息分页
     *
     * @author liuchunming
     * @date  2024/05/27 16:52
     */
    Page<ImMessage> page(ImMessagePageParam imMessagePageParam);

    /**
     * 添加IM-消息
     *
     * @author liuchunming
     * @date  2024/05/27 16:52
     */
    void add(ImMessageAddParam imMessageAddParam);

    /**
     * 编辑IM-消息
     *
     * @author liuchunming
     * @date  2024/05/27 16:52
     */
    void edit(ImMessageEditParam imMessageEditParam);

    /**
     * 删除IM-消息
     *
     * @author liuchunming
     * @date  2024/05/27 16:52
     */
    void delete(List<ImMessageIdParam> imMessageIdParamList);

    /**
     * 获取IM-消息详情
     *
     * @author liuchunming
     * @date  2024/05/27 16:52
     */
    ImMessage detail(ImMessageIdParam imMessageIdParam);

    /**
     * 获取IM-消息详情
     *
     * @author liuchunming
     * @date  2024/05/27 16:52
     **/
    ImMessage queryEntity(String id);

    /**
     * 查询跟当前用户聊天的所有用户
     *
     * @author chengchuanyao
     * @date 2024/7/19 18:40
     */
    Page<ImMessageUserParam> queryChatRecord();

    /**
     * 查询当前用户和指定用户的聊天记录
     *
     * @author chengchuanyao
     * @date 2024/7/20 11:51
     */
    Page<ImMessage> queryChatRecordWithUser(String userId,String chatType);

    /**
     * 将消息设为已读
     *
     * @author chengchuanyao
     * @date 2024/7/24 14:44
     */
    void setRead(List<ImMessageIdParam> imMessageIdParamList);

    /**
     *  撤回消息
     *
     * @author chengchuanyao
     * @date 2024/7/25 18:12
     */
    void recall(ImMessageIdParam imMessageIdParam);
}
