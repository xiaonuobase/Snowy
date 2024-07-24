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
package vip.xiaonuo.im.modular.message.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.im.modular.message.param.ImMessageUserParam;
import vip.xiaonuo.im.modular.message.entity.ImMessage;
import vip.xiaonuo.im.modular.message.mapper.ImMessageMapper;
import vip.xiaonuo.im.modular.message.param.ImMessageAddParam;
import vip.xiaonuo.im.modular.message.param.ImMessageEditParam;
import vip.xiaonuo.im.modular.message.param.ImMessageIdParam;
import vip.xiaonuo.im.modular.message.param.ImMessagePageParam;
import vip.xiaonuo.im.modular.message.service.ImMessageService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IM-消息Service接口实现类
 *
 * @author liuchunming
 * @date 2024/05/27 16:52
 **/
@Service
public class ImMessageServiceImpl extends ServiceImpl<ImMessageMapper, ImMessage> implements ImMessageService {

    @Override
    public Page<ImMessage> page(ImMessagePageParam imMessagePageParam) {
        QueryWrapper<ImMessage> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isAllNotEmpty(imMessagePageParam.getSortField(), imMessagePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(imMessagePageParam.getSortOrder());
            queryWrapper.orderBy(true, imMessagePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(imMessagePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ImMessage::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ImMessageAddParam imMessageAddParam) {
        ImMessage imMessage = BeanUtil.toBean(imMessageAddParam, ImMessage.class);
        this.save(imMessage);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ImMessageEditParam imMessageEditParam) {
        ImMessage imMessage = this.queryEntity(imMessageEditParam.getId());
        BeanUtil.copyProperties(imMessageEditParam, imMessage);
        this.updateById(imMessage);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ImMessageIdParam> imMessageIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(imMessageIdParamList, ImMessageIdParam::getId));
    }

    @Override
    public ImMessage detail(ImMessageIdParam imMessageIdParam) {
        return this.queryEntity(imMessageIdParam.getId());
    }

    @Override
    public ImMessage queryEntity(String id) {
        ImMessage imMessage = this.getById(id);
        if (ObjectUtil.isEmpty(imMessage)) {
            throw new CommonException("IM-消息不存在，id值为：{}", id);
        }
        return imMessage;
    }


    @Override
    public Page<ImMessageUserParam> queryChatRecord() {
        Page<ImMessageUserParam> imMessageUserVoPage = baseMapper.queryChatRecord(CommonPageRequest.defaultPage(), StpUtil.getLoginId().toString());
        return imMessageUserVoPage;
    }

    @Override
    public Page<ImMessage> queryChatRecordWithUser(String userId) {
        LambdaQueryWrapper<ImMessage> lqw = Wrappers.lambdaQuery();
        lqw.eq(ImMessage::getFromUserId, StpUtil.getLoginId().toString());
        lqw.eq(ImMessage::getToUserId, userId);
        lqw.or();
        lqw.eq(ImMessage::getFromUserId, userId);
        lqw.eq(ImMessage::getToUserId, StpUtil.getLoginId().toString());
        lqw.orderByDesc(ImMessage::getCreateTime);
        Page<ImMessage> imMessagePage = this.page(CommonPageRequest.defaultPage(), lqw);
        List<ImMessage> records = imMessagePage.getRecords();
        // 查询消息里边是否有未读数据 如果有修改为已读
        records.forEach(imMessage -> {
            if (imMessage.getToUserId().equals(StpUtil.getLoginId().toString()) && imMessage.getIsRead().equals("2")) {
                imMessage.setIsRead("1");
                this.updateById(imMessage);
            }
        });
        List<ImMessage> collect = records.stream().sorted(Comparator.comparing(ImMessage::getCreateTime)).collect(Collectors.toList());
        imMessagePage.setRecords(collect);
        return imMessagePage;
    }


    @Override
    public void setRead(List<ImMessageIdParam> imMessageIdParamList) {
        this.updateBatchById(CollStreamUtil.toList(imMessageIdParamList, ImMessageIdParam::getId).stream().map(id -> {
            ImMessage imMessage = new ImMessage();
            imMessage.setId(id);
            imMessage.setIsRead("1");
            return imMessage;
        }).collect(Collectors.toList()));
    }
}
