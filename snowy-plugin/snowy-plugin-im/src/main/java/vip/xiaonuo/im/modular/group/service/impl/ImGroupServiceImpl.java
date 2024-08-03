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
package vip.xiaonuo.im.modular.group.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.im.modular.group.entity.ImGroup;
import vip.xiaonuo.im.modular.group.mapper.ImGroupMapper;
import vip.xiaonuo.im.modular.group.param.ImGroupAddParam;
import vip.xiaonuo.im.modular.group.param.ImGroupEditParam;
import vip.xiaonuo.im.modular.group.param.ImGroupIdParam;
import vip.xiaonuo.im.modular.group.param.ImGroupPageParam;
import vip.xiaonuo.im.modular.group.service.ImGroupService;
import vip.xiaonuo.im.modular.member.entity.ImGroupMember;
import vip.xiaonuo.im.modular.member.service.ImGroupMemberService;
import vip.xiaonuo.im.modular.message.entity.ImMessage;
import vip.xiaonuo.im.modular.message.service.ImMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IM-群组Service接口实现类
 *
 * @author liuchunming
 * @date  2024/05/27 16:40
 **/
@Service
@RequiredArgsConstructor
public class ImGroupServiceImpl extends ServiceImpl<ImGroupMapper, ImGroup> implements ImGroupService {

    private final ImGroupMemberService imGroupMemberService;

    private final ImMessageService imMessageService;

    @Override
    public Page<ImGroup> page(ImGroupPageParam imGroupPageParam) {
        QueryWrapper<ImGroup> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(imGroupPageParam.getName())) {
            queryWrapper.lambda().like(ImGroup::getName, imGroupPageParam.getName());
        }
        if(ObjectUtil.isAllNotEmpty(imGroupPageParam.getSortField(), imGroupPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(imGroupPageParam.getSortOrder());
            queryWrapper.orderBy(true, imGroupPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(imGroupPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ImGroup::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ImGroupAddParam imGroupAddParam) {
        List<String> receiverIdList = imGroupAddParam.getReceiverIdList();
        if (CollectionUtil.isEmpty(receiverIdList)){
            throw new CommonException("用户列表不能为空");
        }
        ImGroup imGroup = BeanUtil.toBean(imGroupAddParam, ImGroup.class);
        this.save(imGroup);
        boolean contains = receiverIdList.contains(StpUtil.getLoginId().toString());
        if(!contains) {
            receiverIdList.add(StpUtil.getLoginId().toString());
        }
        List<ImGroupMember> imGroupMembers = new ArrayList<>();
        receiverIdList.forEach(s->{
            ImGroupMember imGroupMember = new ImGroupMember();
            imGroupMember.setGroupId(imGroup.getId());
            imGroupMember.setUserId(s);
            // 先默认是B端用户
            imGroupMember.setUserType("1");
            if(s.equals(StpUtil.getLoginId().toString())){
                imGroupMember.setRole("1");
            }else{
                imGroupMember.setRole("3");
            }
            imGroupMembers.add(imGroupMember);
        });
        // 批量新增
        imGroupMemberService.saveBatch(imGroupMembers,imGroupMembers.size());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ImGroupEditParam imGroupEditParam) {
        List<String> receiverIdList = imGroupEditParam.getReceiverIdList();
        if (CollectionUtil.isEmpty(receiverIdList)){
            throw new CommonException("用户列表不能为空");
        }
        // 通过群组id查询原来的人数 用于判断是否有人员变动 删除去除的 新增 新增的
        List<ImGroupMember> imGroupMembers = imGroupMemberService.list(new QueryWrapper<ImGroupMember>().lambda().eq(ImGroupMember::getGroupId, imGroupEditParam.getId()));
        List<String> userIdList = new ArrayList<>();
        imGroupMembers.forEach(imGroupMember -> userIdList.add(imGroupMember.getUserId()));
        // 去除的
        List<String> removeList = new ArrayList<>(userIdList);
        removeList.removeAll(receiverIdList);
        // 新增的
        List<String> addList = new ArrayList<>(receiverIdList);
        addList.removeAll(userIdList);
        // 删除去除的
        if(CollectionUtil.isNotEmpty(removeList)){
            imGroupMemberService.remove(new QueryWrapper<ImGroupMember>().lambda().eq(ImGroupMember::getGroupId, imGroupEditParam.getId()).in(ImGroupMember::getUserId, removeList));
        }
        // 新增新增的
        if(CollectionUtil.isNotEmpty(addList)){
            List<ImGroupMember> addImGroupMembers = new ArrayList<>();
            addList.forEach(s->{
                ImGroupMember imGroupMember = new ImGroupMember();
                imGroupMember.setGroupId(imGroupEditParam.getId());
                imGroupMember.setUserId(s);
                // 先默认是B端用户
                imGroupMember.setUserType("1");
                imGroupMember.setRole("3");
                addImGroupMembers.add(imGroupMember);
            });
            imGroupMemberService.saveBatch(addImGroupMembers);
        }
        ImGroup imGroup = this.queryEntity(imGroupEditParam.getId());
        BeanUtil.copyProperties(imGroupEditParam, imGroup);
        this.updateById(imGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ImGroupIdParam> imGroupIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(imGroupIdParamList, ImGroupIdParam::getId));
        imGroupIdParamList.forEach(imGroupIdParam -> {
            // 删除完成之后需要删除群组中人员的数据
            imGroupMemberService.remove(new QueryWrapper<ImGroupMember>().lambda().eq(ImGroupMember::getGroupId, imGroupIdParam.getId()));
            // 删除当前群聊的聊天记录
            imMessageService.remove(new QueryWrapper<ImMessage>().lambda().eq(ImMessage::getToUserId, imGroupIdParam.getId()));
        });
    }

    @Override
    public ImGroup detail(ImGroupIdParam imGroupIdParam) {
        ImGroup imGroup = this.queryEntity(imGroupIdParam.getId());
        List<ImGroupMember> imGroupMembers = imGroupMemberService.list(new QueryWrapper<ImGroupMember>().lambda().eq(ImGroupMember::getGroupId, imGroupIdParam.getId()));
        imGroup.setImGroupMembers(imGroupMembers);
        return imGroup;
    }

    @Override
    public ImGroup queryEntity(String id) {
        ImGroup imGroup = this.getById(id);
        if(ObjectUtil.isEmpty(imGroup)) {
            throw new CommonException("IM-群组不存在，id值为：{}", id);
        }
        return imGroup;
    }

    @Override
    public List<ImGroup> listByUser() {
        LambdaQueryWrapper<ImGroupMember> imGroupMemberLambdaQueryWrapper = Wrappers.lambdaQuery();
        imGroupMemberLambdaQueryWrapper.eq(ImGroupMember::getUserId, StpUtil.getLoginId());
        imGroupMemberLambdaQueryWrapper.select(ImGroupMember::getGroupId);
        List<ImGroupMember> list = imGroupMemberService.list(imGroupMemberLambdaQueryWrapper);
        List<String> groupIds = list.stream().map(ImGroupMember::getGroupId).distinct().collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(groupIds)){
            return this.listByIds(groupIds);
        }
        return List.of();
    }
}
