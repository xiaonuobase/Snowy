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
package vip.xiaonuo.biz.modular.org.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.core.enums.BizDataTypeEnum;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.org.enums.BizOrgCategoryEnum;
import vip.xiaonuo.biz.modular.org.mapper.BizOrgMapper;
import vip.xiaonuo.biz.modular.org.param.*;
import vip.xiaonuo.biz.modular.org.service.BizOrgService;
import vip.xiaonuo.biz.modular.position.entity.BizPosition;
import vip.xiaonuo.biz.modular.position.service.BizPositionService;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.biz.modular.user.service.BizUserService;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.sys.api.SysRoleApi;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 机构Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class BizOrgServiceImpl extends ServiceImpl<BizOrgMapper, BizOrg> implements BizOrgService {

    public static final String ORG_CACHE_ALL_KEY = "sys-org:all";
    
    @Resource
    private CommonCacheOperator commonCacheOperator;
    
    @Resource
    private SysRoleApi sysRoleApi;

    @Resource
    private BizPositionService bizPositionService;

    @Resource
    private BizUserService bizUserService;

    @Override
    public Page<BizOrg> page(BizOrgPageParam bizOrgPageParam) {
        QueryWrapper<BizOrg> queryWrapper = new QueryWrapper<BizOrg>().checkSqlInjection();
        // 查询部分字段
        queryWrapper.lambda().select(BizOrg::getId, BizOrg::getParentId, BizOrg::getName,
                BizOrg::getCategory, BizOrg::getSortCode);
        if(ObjectUtil.isNotEmpty(bizOrgPageParam.getParentId())) {
            queryWrapper.lambda().eq(BizOrg::getParentId, bizOrgPageParam.getParentId());
        }
        if(ObjectUtil.isNotEmpty(bizOrgPageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizOrg::getName, bizOrgPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(bizOrgPageParam.getSortField(), bizOrgPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizOrgPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizOrgPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizOrgPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizOrg::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            queryWrapper.lambda().in(BizOrg::getId, loginUserDataScope);
        } else {
            return new Page<>();
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<Tree<String>> tree() {
        // 获取所有机构
        List<BizOrg> allOrgList = this.list();
        // 定义机构集合
        Set<BizOrg> bizOrgSet = CollectionUtil.newHashSet();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            loginUserDataScope.forEach(orgId -> bizOrgSet.addAll(this.getParentListById(allOrgList, orgId, true)));
        } else {
            return CollectionUtil.newArrayList();
        }
        List<TreeNode<String>> treeNodeList = bizOrgSet.stream().map(bizOrg ->
                new TreeNode<>(bizOrg.getId(), bizOrg.getParentId(),
                        bizOrg.getName(), bizOrg.getSortCode()).setExtra(JSONUtil.parseObj(bizOrg)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizOrgAddParam bizOrgAddParam) {
        BizOrgCategoryEnum.validate(bizOrgAddParam.getCategory());
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizOrgAddParam.getParentId())) {
                throw new CommonException("您没有权限在该机构下增加机构，机构id：{}", bizOrgAddParam.getParentId());
            }
        } else {
            throw new CommonException("您没有权限增加机构");
        }
        BizOrg bizOrg = BeanUtil.toBean(bizOrgAddParam, BizOrg.class);

        // 重复名称
        boolean repeatName = this.count(new LambdaQueryWrapper<BizOrg>().eq(BizOrg::getParentId, bizOrg.getParentId())
                .eq(BizOrg::getName, bizOrg.getName())) > 0;
        if(repeatName) {
            throw new CommonException("存在重复的同级机构，名称为：{}", bizOrg.getName());
        }
        bizOrg.setCode(RandomUtil.randomString(10));
        this.save(bizOrg);

        // 发布增加事件
        CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(bizOrg));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizOrgEditParam bizOrgEditParam) {
        BizOrgCategoryEnum.validate(bizOrgEditParam.getCategory());
        BizOrg bizOrg = this.queryEntity(bizOrgEditParam.getId());
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizOrg.getId())) {
                throw new CommonException("您没有权限编辑该机构，机构id：{}", bizOrg.getId());
            }
            if(!loginUserDataScope.contains(bizOrg.getParentId())) {
                throw new CommonException("您没有权限编辑该机构下的机构，机构id：{}", bizOrg.getParentId());
            }
        } else {
            throw new CommonException("您没有权限编辑该机构，机构id：{}", bizOrg.getId());
        }
        BeanUtil.copyProperties(bizOrgEditParam, bizOrg);
        boolean repeatName = this.count(new LambdaQueryWrapper<BizOrg>().eq(BizOrg::getParentId, bizOrg.getParentId())
                .eq(BizOrg::getName, bizOrg.getName()).ne(BizOrg::getId, bizOrg.getId())) > 0;
        if(repeatName) {
            throw new CommonException("存在重复的同级机构，名称为：{}", bizOrg.getName());
        }
        List<BizOrg> originDataList = this.list();
        boolean errorLevel = this.getChildListById(originDataList, bizOrg.getId(), true).stream()
                .map(BizOrg::getId).collect(Collectors.toList()).contains(bizOrg.getParentId());
        if(errorLevel) {
            throw new CommonException("不可选择上级机构：{}", this.getById(originDataList, bizOrg.getParentId()).getName());
        }
        this.updateById(bizOrg);

        // 发布更新事件
        CommonDataChangeEventCenter.doUpdateWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(bizOrg));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizOrgIdParam> bizOrgIdParamList) {
        List<String> orgIdList = CollStreamUtil.toList(bizOrgIdParamList, BizOrgIdParam::getId);
        if(ObjectUtil.isNotEmpty(orgIdList)) {
            // 校验数据范围
            List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
            if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
                if(!loginUserDataScope.containsAll(orgIdList)) {
                    throw new CommonException("您没有权限删除这些机构，机构id：{}", orgIdList);
                }
            } else {
                throw new CommonException("您没有权限删除这些机构，机构id：{}", orgIdList);
            }
            List<BizOrg> allOrgList = this.list();
            // 获取所有子机构
            List<String> toDeleteOrgIdList = CollectionUtil.newArrayList();
            orgIdList.forEach(orgId -> toDeleteOrgIdList.addAll(this.getChildListById(allOrgList, orgId, true).stream()
                    .map(BizOrg::getId).collect(Collectors.toList())));
            // 机构下有人不能删除（直属机构）
            boolean hasOrgUser = bizUserService.count(new LambdaQueryWrapper<BizUser>().in(BizUser::getOrgId, toDeleteOrgIdList)) > 0;
            if(hasOrgUser) {
                throw new CommonException("请先删除机构下的人员");
            }
            // 机构下有人不能删除（兼任机构）
            List<String> positionJsonList = bizUserService.list(new LambdaQueryWrapper<BizUser>()
                    .isNotNull(BizUser::getPositionJson)).stream().map(BizUser::getPositionJson).collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(positionJsonList)) {
                List<String> positionOrgIdList = CollectionUtil.newArrayList();
                positionJsonList.forEach(positionJson -> JSONUtil.toList(JSONUtil.parseArray(positionJson), JSONObject.class)
                        .forEach(jsonObject -> positionOrgIdList.add(jsonObject.getStr("orgId"))));
                boolean hasPositionUser = CollectionUtil.intersectionDistinct(toDeleteOrgIdList, CollectionUtil.removeNull(positionOrgIdList)).size() > 0;
                if(hasPositionUser) {
                    throw new CommonException("请先删除机构下的人员");
                }
            }
            // 机构下有角色不能删除
            boolean hasRole = sysRoleApi.orgHasRole(toDeleteOrgIdList);
            if(hasRole) {
                throw new CommonException("请先删除机构下的角色");
            }
            // 机构下有岗位不能删除
            boolean hasPosition = bizPositionService.count(new LambdaQueryWrapper<BizPosition>().in(BizPosition::getOrgId, toDeleteOrgIdList)) > 0;
            if(hasPosition) {
                throw new CommonException("请先删除机构下的岗位");
            }
            // 执行删除
            this.removeByIds(toDeleteOrgIdList);

            // 发布删除事件
            CommonDataChangeEventCenter.doDeleteWithDataId(BizDataTypeEnum.ORG.getValue(), toDeleteOrgIdList);
        }
    }

    @Override
    public BizOrg detail(BizOrgIdParam bizOrgIdParam) {
        return this.queryEntity(bizOrgIdParam.getId());
    }

    @Override
    public BizOrg queryEntity(String id) {
        BizOrg bizOrg = this.getById(id);
        if(ObjectUtil.isEmpty(bizOrg)) {
            throw new CommonException("机构不存在，id值为：{}", id);
        }
        return bizOrg;
    }

    @Override
    public List<BizOrg> getAllOrgList() {
        return this.list(new LambdaQueryWrapper<BizOrg>().orderByAsc(BizOrg::getSortCode));
    }

    @Override
    public String getOrgIdByOrgFullNameWithCreate(String orgFullName) {
        List<BizOrg> cachedAllOrgList = this.getAllOrgList();
        List<Tree<String>> treeList = TreeUtil.build(cachedAllOrgList.stream().map(bizOrg ->
                new TreeNode<>(bizOrg.getId(), bizOrg.getParentId(), bizOrg.getName(), bizOrg.getSortCode()))
                .collect(Collectors.toList()), "0");
        return findOrgIdByOrgName("0", StrUtil.split(orgFullName, StrUtil.DASHED).iterator(), cachedAllOrgList, treeList);
    }

    public String findOrgIdByOrgName(String parentId, Iterator<String> iterator, List<BizOrg> cachedAllOrgList, List<Tree<String>> treeList) {
        String orgName = iterator.next();
        if(ObjectUtil.isNotEmpty(treeList)) {
            List<Tree<String>> findList = treeList.stream().filter(tree -> tree.getName().equals(orgName)).collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(findList)) {
                if(iterator.hasNext()) {
                    return findOrgIdByOrgName(findList.get(0).getId(), iterator, cachedAllOrgList, findList.get(0).getChildren());
                } else {
                    return findList.get(0).getId();
                }
            }
        }
        String orgId = this.doCreateOrg(parentId, orgName, cachedAllOrgList);
        if(iterator.hasNext()) {
            return findOrgIdByOrgName(orgId, iterator, cachedAllOrgList, CollectionUtil.newArrayList());
        } else {
            return orgId;
        }
    }

    /**
     * 执行创建机构
     *
     * @author xuyuxiang
     * @date 2023/3/8 9:38
     **/
    public String doCreateOrg(String parentId, String orgName, List<BizOrg> cachedAllOrgList) {
        //创建该机构
        BizOrg bizOrg = new BizOrg();
        bizOrg.setName(orgName);
        bizOrg.setCode(RandomUtil.randomString(10));
        bizOrg.setParentId(parentId);
        bizOrg.setCategory("0".equals(parentId)?BizOrgCategoryEnum.COMPANY.getValue():BizOrgCategoryEnum.DEPT.getValue());
        bizOrg.setSortCode(99);
        this.save(bizOrg);
        // 发布增加事件
        CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(bizOrg));
        // 将该机构加入缓存
        cachedAllOrgList.add(bizOrg);
        // 更新缓存
        commonCacheOperator.put(ORG_CACHE_ALL_KEY, cachedAllOrgList);
        return bizOrg.getId();
    }

    /* ====机构部分所需要用到的选择器==== */

    @Override
    public List<Tree<String>> orgTreeSelector() {
        LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        // 定义机构集合
        Set<BizOrg> bizOrgSet = CollectionUtil.newHashSet();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 获取所有机构
            List<BizOrg> allOrgList = this.list();
            loginUserDataScope.forEach(orgId -> bizOrgSet.addAll(this.getParentListById(allOrgList, orgId, true)));
            List<String> loginUserDataScopeFullList = bizOrgSet.stream().map(BizOrg::getId).collect(Collectors.toList());
            lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScopeFullList);
        } else {
            return CollectionUtil.newArrayList();
        }
        lambdaQueryWrapper.orderByAsc(BizOrg::getSortCode);
        List<BizOrg> bizOrgList = this.list(lambdaQueryWrapper);
        List<TreeNode<String>> treeNodeList = bizOrgList.stream().map(bizOrg ->
                new TreeNode<>(bizOrg.getId(), bizOrg.getParentId(), bizOrg.getName(), bizOrg.getSortCode()))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public List<BizOrg> orgListSelector(BizOrgSelectorOrgListParam bizOrgSelectorOrgListParam) {
        LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScope);
        } else {
            return CollectionUtil.newArrayList();
        }
        // 查询部分字段
        lambdaQueryWrapper.select(BizOrg::getId, BizOrg::getParentId, BizOrg::getName,
                BizOrg::getCategory, BizOrg::getSortCode);
        if(ObjectUtil.isNotEmpty(bizOrgSelectorOrgListParam.getParentId())) {
            lambdaQueryWrapper.eq(BizOrg::getParentId, bizOrgSelectorOrgListParam.getParentId());
        }
        if(ObjectUtil.isNotEmpty(bizOrgSelectorOrgListParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizOrg::getName, bizOrgSelectorOrgListParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(BizOrg::getSortCode);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public Page<BizUser> userSelector(BizOrgSelectorUserParam bizOrgSelectorUserParam) {
        LambdaQueryWrapper<BizUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            lambdaQueryWrapper.in(BizUser::getOrgId, loginUserDataScope);
        } else {
            return new Page<>();
        }
        // 只查询部分字段
        lambdaQueryWrapper.select(BizUser::getId, BizUser::getAvatar, BizUser::getOrgId, BizUser::getPositionId, BizUser::getAccount,
                BizUser::getName, BizUser::getSortCode, BizUser::getGender, BizUser::getEntryDate);
        if (ObjectUtil.isNotEmpty(bizOrgSelectorUserParam.getOrgId())) {
            // 如果机构id不为空，则查询该机构及其子机构下的所有人
            List<String> childOrgIdList = CollStreamUtil.toList(this.getChildListById(this
                    .getAllOrgList(), bizOrgSelectorUserParam.getOrgId(), true), BizOrg::getId);
            if (ObjectUtil.isNotEmpty(childOrgIdList)) {
                lambdaQueryWrapper.in(BizUser::getOrgId, childOrgIdList);
            } else {
                return new Page<>();
            }
        }
        if(ObjectUtil.isNotEmpty(bizOrgSelectorUserParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizUser::getName, bizOrgSelectorUserParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(BizUser::getSortCode);
        return bizUserService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }

    /* ====以下为各种递归方法==== */

    @Override
    public List<BizOrg> getParentAndChildListById(List<BizOrg> originDataList, String id, boolean includeSelf) {
        List<BizOrg> parentListById = this.getParentListById(originDataList, id, false);
        List<BizOrg> childListById = this.getChildListById(originDataList, id, true);
        parentListById.addAll(childListById);
        return parentListById;
    }

    @Override
    public List<BizOrg> getChildListById(List<BizOrg> originDataList, String id, boolean includeSelf) {
        List<BizOrg> resultList = CollectionUtil.newArrayList();
        execRecursionFindChild(originDataList, id, resultList);
        if(includeSelf) {
            BizOrg self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
    }

    @Override
    public List<BizOrg> getParentListById(List<BizOrg> originDataList, String id, boolean includeSelf) {
        List<BizOrg> resultList = CollectionUtil.newArrayList();
        execRecursionFindParent(originDataList, id, resultList);
        if(includeSelf) {
            BizOrg self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
    }


    public void execRecursionFindChild(List<BizOrg> originDataList, String id, List<BizOrg> resultList) {
        originDataList.forEach(item -> {
            if(item.getParentId().equals(id)) {
                resultList.add(item);
                execRecursionFindChild(originDataList, item.getId(), resultList);
            }
        });
    }

    public void execRecursionFindParent(List<BizOrg> originDataList, String id, List<BizOrg> resultList) {
        originDataList.forEach(item -> {
            if(item.getId().equals(id)) {
                BizOrg parent = this.getById(originDataList, item.getParentId());
                if(ObjectUtil.isNotEmpty(parent)) {
                    resultList.add(parent);
                }
                execRecursionFindParent(originDataList, item.getParentId(), resultList);
            }
        });
    }

    @Override
    public BizOrg getById(List<BizOrg> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, BizOrg::getId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }

    @Override
    public BizOrg getParentById(List<BizOrg> originDataList, String id) {
        BizOrg self = this.getById(originDataList, id);
        return ObjectUtil.isNotEmpty(self)?self:this.getById(originDataList, self.getParentId());
    }

    @Override
    public BizOrg getChildById(List<BizOrg> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, BizOrg::getParentId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }
}
