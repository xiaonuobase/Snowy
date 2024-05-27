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
import vip.xiaonuo.im.modular.group.entity.ImGroup;
import vip.xiaonuo.im.modular.group.mapper.ImGroupMapper;
import vip.xiaonuo.im.modular.group.param.ImGroupAddParam;
import vip.xiaonuo.im.modular.group.param.ImGroupEditParam;
import vip.xiaonuo.im.modular.group.param.ImGroupIdParam;
import vip.xiaonuo.im.modular.group.param.ImGroupPageParam;
import vip.xiaonuo.im.modular.group.service.ImGroupService;

import java.util.List;

/**
 * IM-群组Service接口实现类
 *
 * @author liuchunming
 * @date  2024/05/27 16:40
 **/
@Service
public class ImGroupServiceImpl extends ServiceImpl<ImGroupMapper, ImGroup> implements ImGroupService {

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
        ImGroup imGroup = BeanUtil.toBean(imGroupAddParam, ImGroup.class);
        this.save(imGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ImGroupEditParam imGroupEditParam) {
        ImGroup imGroup = this.queryEntity(imGroupEditParam.getId());
        BeanUtil.copyProperties(imGroupEditParam, imGroup);
        this.updateById(imGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ImGroupIdParam> imGroupIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(imGroupIdParamList, ImGroupIdParam::getId));
    }

    @Override
    public ImGroup detail(ImGroupIdParam imGroupIdParam) {
        return this.queryEntity(imGroupIdParam.getId());
    }

    @Override
    public ImGroup queryEntity(String id) {
        ImGroup imGroup = this.getById(id);
        if(ObjectUtil.isEmpty(imGroup)) {
            throw new CommonException("IM-群组不存在，id值为：{}", id);
        }
        return imGroup;
    }
}
