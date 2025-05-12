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
package vip.xiaonuo.biz.modular.group.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.modular.group.entity.BizGroup;
import vip.xiaonuo.biz.modular.group.mapper.BizGroupMapper;
import vip.xiaonuo.biz.modular.group.param.*;
import vip.xiaonuo.biz.modular.group.service.BizGroupService;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.org.service.BizOrgService;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.biz.modular.user.enums.BizUserStatusEnum;
import vip.xiaonuo.biz.modular.user.service.BizUserService;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.sys.api.SysGroupApi;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户组Service接口实现类
 *
 * @author yubaoshan
 * @date  2024/12/24 03:33
 **/
@Service
public class BizGroupServiceImpl extends ServiceImpl<BizGroupMapper, BizGroup> implements BizGroupService {

    @Resource
    private SysGroupApi sysGroupApi;

    @Resource
    private BizUserService bizUserService;

    @Resource
    private BizOrgService bizOrgService;

    @Override
    public Page<BizGroup> page(BizGroupPageParam bizGroupPageParam) {
        QueryWrapper<BizGroup> queryWrapper = new QueryWrapper<BizGroup>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(bizGroupPageParam.getName())) {
            queryWrapper.lambda().like(BizGroup::getName, bizGroupPageParam.getName());
        }
        if(ObjectUtil.isAllNotEmpty(bizGroupPageParam.getSortField(), bizGroupPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizGroupPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizGroupPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizGroupPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizGroup::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizGroupAddParam bizGroupAddParam) {
        BizGroup bizGroup = BeanUtil.toBean(bizGroupAddParam, BizGroup.class);
        this.save(bizGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizGroupEditParam bizGroupEditParam) {
        BizGroup bizGroup = this.queryEntity(bizGroupEditParam.getId());
        BeanUtil.copyProperties(bizGroupEditParam, bizGroup);
        this.updateById(bizGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizGroupIdParam> bizGroupIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(bizGroupIdParamList, BizGroupIdParam::getId));
    }

    @Override
    public BizGroup detail(BizGroupIdParam bizGroupIdParam) {
        return this.queryEntity(bizGroupIdParam.getId());
    }

    @Override
    public BizGroup queryEntity(String id) {
        BizGroup bizGroup = this.getById(id);
        if(ObjectUtil.isEmpty(bizGroup)) {
            throw new CommonException("用户组不存在，id值为：{}", id);
        }
        return bizGroup;
    }

    @Override
    public List<String> ownUser(BizGroupIdParam sysGroupIdParam) {
        return sysGroupApi.ownUser(sysGroupIdParam.getId());
    }

    @Override
    public List<Tree<String>> orgTreeSelector() {
        // 获取所有机构
        List<BizOrg> allOrgList = bizOrgService.list();
        // 定义机构集合
        Set<BizOrg> bizOrgSet = CollectionUtil.newHashSet();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            loginUserDataScope.forEach(orgId -> bizOrgSet.addAll(bizOrgService.getParentListById(allOrgList, orgId, true)));
        } else {
            return CollectionUtil.newArrayList();
        }
        List<TreeNode<String>> treeNodeList = bizOrgSet.stream().map(bizOrg ->
                        new TreeNode<>(bizOrg.getId(), bizOrg.getParentId(),
                                bizOrg.getName(), bizOrg.getSortCode()).setExtra(JSONUtil.parseObj(bizOrg)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public Page<BizUser> userSelector(BizGroupSelectorUserParam bizGroupSelectorUserParam) {
        QueryWrapper<BizUser> queryWrapper = new QueryWrapper<BizUser>().checkSqlInjection();
        // 只查询状态为正常的
        queryWrapper.lambda().eq(BizUser::getUserStatus, BizUserStatusEnum.ENABLE.getValue());
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            queryWrapper.lambda().in(BizUser::getOrgId, loginUserDataScope);
        } else {
            return new Page<>();
        }
        // 只查询部分字段
        queryWrapper.lambda().select(BizUser::getId, BizUser::getAvatar, BizUser::getOrgId, BizUser::getPositionId, BizUser::getAccount,
                BizUser::getName, BizUser::getSortCode, BizUser::getGender, BizUser::getEntryDate);
        if (ObjectUtil.isNotEmpty(bizGroupSelectorUserParam.getOrgId())) {
            // 如果机构id不为空，则查询该机构及其子机构下的所有人
            List<String> childOrgIdList = CollStreamUtil.toList(bizOrgService.getChildListById(bizOrgService
                    .getAllOrgList(), bizGroupSelectorUserParam.getOrgId(), true), BizOrg::getId);
            if (ObjectUtil.isNotEmpty(childOrgIdList)) {
                queryWrapper.lambda().in(BizUser::getOrgId, childOrgIdList);
            } else {
                return new Page<>();
            }
        }
        if(ObjectUtil.isNotEmpty(bizGroupSelectorUserParam.getSearchKey())) {
            queryWrapper.lambda().like(BizUser::getName, bizGroupSelectorUserParam.getSearchKey());
        }
        queryWrapper.lambda().orderByAsc(BizUser::getSortCode);
        return bizUserService.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public void grantUser(BizGroupGrantUserParam bizGroupGrantUserParam) {
        sysGroupApi.grantUser(bizGroupGrantUserParam.getId(), bizGroupGrantUserParam.getGrantInfoList());
    }
}
