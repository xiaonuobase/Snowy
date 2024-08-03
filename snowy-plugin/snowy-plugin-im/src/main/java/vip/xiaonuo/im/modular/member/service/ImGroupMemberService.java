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
package vip.xiaonuo.im.modular.member.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.im.modular.member.entity.ImGroupMember;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberAddParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberEditParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberIdParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberPageParam;

import java.util.List;

/**
 * IM-群组成员Service接口
 *
 * @author liuchunming
 * @date  2024/05/27 16:48
 **/
public interface ImGroupMemberService extends IService<ImGroupMember> {

    /**
     * 获取IM-群组成员分页
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    Page<ImGroupMember> page(ImGroupMemberPageParam imGroupMemberPageParam);

    /**
     * 添加IM-群组成员
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    void add(ImGroupMemberAddParam imGroupMemberAddParam);

    /**
     * 编辑IM-群组成员
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    void edit(ImGroupMemberEditParam imGroupMemberEditParam);

    /**
     * 删除IM-群组成员
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    void delete(List<ImGroupMemberIdParam> imGroupMemberIdParamList);

    /**
     * 获取IM-群组成员详情
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     */
    ImGroupMember detail(ImGroupMemberIdParam imGroupMemberIdParam);

    /**
     * 获取IM-群组成员详情
     *
     * @author liuchunming
     * @date  2024/05/27 16:48
     **/
    ImGroupMember queryEntity(String id);

    /**
     * 在群组中禁言某个用户
     *
     * @author chengchuanyao
     * @date 2024/8/3 13:36
     */
    void silence(ImGroupMemberEditParam imGroupMemberEditParam);

    /**
     *  获取当前用户被禁言的群组
     *
     * @author chengchuanyao
     * @date 2024/8/3 13:20
     */
    List<ImGroupMember> getSilenceGroup();


    /**
     * 在群组中解除禁言某个用户
     *
     * @author chengchuanyao
     * @date 2024/8/3 13:56
     */
    void cancelSilence(ImGroupMemberEditParam imGroupMemberEditParam);
}
