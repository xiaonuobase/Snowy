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
package vip.xiaonuo.im.modular.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.im.modular.member.entity.ImGroupMember;
import vip.xiaonuo.im.modular.member.mapper.ImGroupMemberMapper;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberAddParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberEditParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberIdParam;
import vip.xiaonuo.im.modular.member.param.ImGroupMemberPageParam;
import vip.xiaonuo.im.modular.member.service.ImGroupMemberService;

import java.util.List;

/**
 * IM-群组成员Service接口实现类
 *
 * @author liuchunming
 * @date  2024/05/27 16:48
 **/
@Service
public class ImGroupMemberServiceImpl extends ServiceImpl<ImGroupMemberMapper, ImGroupMember> implements ImGroupMemberService {

    @Override
    public Page<ImGroupMember> page(ImGroupMemberPageParam imGroupMemberPageParam) {
        QueryWrapper<ImGroupMember> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isAllNotEmpty(imGroupMemberPageParam.getSortField(), imGroupMemberPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(imGroupMemberPageParam.getSortOrder());
            queryWrapper.orderBy(true, imGroupMemberPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(imGroupMemberPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ImGroupMember::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ImGroupMemberAddParam imGroupMemberAddParam) {
        ImGroupMember imGroupMember = BeanUtil.toBean(imGroupMemberAddParam, ImGroupMember.class);
        this.save(imGroupMember);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ImGroupMemberEditParam imGroupMemberEditParam) {
        ImGroupMember imGroupMember = this.queryEntity(imGroupMemberEditParam.getId());
        BeanUtil.copyProperties(imGroupMemberEditParam, imGroupMember);
        this.updateById(imGroupMember);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ImGroupMemberIdParam> imGroupMemberIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(imGroupMemberIdParamList, ImGroupMemberIdParam::getId));
    }

    @Override
    public ImGroupMember detail(ImGroupMemberIdParam imGroupMemberIdParam) {
        return this.queryEntity(imGroupMemberIdParam.getId());
    }

    @Override
    public ImGroupMember queryEntity(String id) {
        ImGroupMember imGroupMember = this.getById(id);
        if(ObjectUtil.isEmpty(imGroupMember)) {
            throw new CommonException("IM-群组成员不存在，id值为：{}", id);
        }
        return imGroupMember;
    }
}
