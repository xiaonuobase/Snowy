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
package vip.xiaonuo.sys.modular.org.service.impl;

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
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.sys.core.enums.SysDataTypeEnum;
import vip.xiaonuo.sys.modular.org.entity.SysOrg;
import vip.xiaonuo.sys.modular.org.enums.SysOrgCategoryEnum;
import vip.xiaonuo.sys.modular.org.mapper.SysOrgMapper;
import vip.xiaonuo.sys.modular.org.param.*;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;
import vip.xiaonuo.sys.modular.position.entity.SysPosition;
import vip.xiaonuo.sys.modular.position.service.SysPositionService;
import vip.xiaonuo.sys.modular.role.entity.SysRole;
import vip.xiaonuo.sys.modular.role.service.SysRoleService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {

    public static final String ORG_CACHE_ALL_KEY = "sys-org:all";

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysPositionService sysPositionService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public Page<SysOrg> page(SysOrgPageParam sysOrgPageParam) {
        QueryWrapper<SysOrg> queryWrapper = new QueryWrapper<SysOrg>().checkSqlInjection();
        // 查询部分字段
        queryWrapper.lambda().select(SysOrg::getId, SysOrg::getParentId, SysOrg::getName,
                SysOrg::getCategory, SysOrg::getSortCode);
        if(ObjectUtil.isNotEmpty(sysOrgPageParam.getParentId())) {
            queryWrapper.lambda().eq(SysOrg::getParentId, sysOrgPageParam.getParentId());
        }
        if(ObjectUtil.isNotEmpty(sysOrgPageParam.getSearchKey())) {
            queryWrapper.lambda().like(SysOrg::getName, sysOrgPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(sysOrgPageParam.getSortField(), sysOrgPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(sysOrgPageParam.getSortOrder());
            queryWrapper.orderBy(true, sysOrgPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(sysOrgPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(SysOrg::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<Tree<String>> tree() {
        List<SysOrg> sysOrgList = this.getAllOrgList();
        List<TreeNode<String>> treeNodeList = sysOrgList.stream().map(sysOrg ->
                new TreeNode<>(sysOrg.getId(), sysOrg.getParentId(),
                        sysOrg.getName(), sysOrg.getSortCode()).setExtra(JSONUtil.parseObj(sysOrg)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SysOrgAddParam sysOrgAddParam) {
        SysOrgCategoryEnum.validate(sysOrgAddParam.getCategory());
        SysOrg sysOrg = BeanUtil.toBean(sysOrgAddParam, SysOrg.class);
        // 重复名称
        boolean repeatName = this.count(new LambdaQueryWrapper<SysOrg>().eq(SysOrg::getParentId, sysOrg.getParentId())
                .eq(SysOrg::getName, sysOrg.getName())) > 0;
        if(repeatName) {
            throw new CommonException("存在重复的同级组织，名称为：{}", sysOrg.getName());
        }
        sysOrg.setCode(RandomUtil.randomString(10));
        this.save(sysOrg);

        // 发布增加事件
        CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(sysOrg));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysOrgEditParam sysOrgEditParam) {
        SysOrgCategoryEnum.validate(sysOrgEditParam.getCategory());
        SysOrg sysOrg = this.queryEntity(sysOrgEditParam.getId());
        BeanUtil.copyProperties(sysOrgEditParam, sysOrg);
        boolean repeatName = this.count(new LambdaQueryWrapper<SysOrg>().eq(SysOrg::getParentId, sysOrg.getParentId())
                .eq(SysOrg::getName, sysOrg.getName()).ne(SysOrg::getId, sysOrg.getId())) > 0;
        if(repeatName) {
            throw new CommonException("存在重复的同级组织，名称为：{}", sysOrg.getName());
        }
        List<SysOrg> originDataList = this.getAllOrgList();
        boolean errorLevel = this.getChildListById(originDataList, sysOrg.getId(), true).stream()
                .map(SysOrg::getId).collect(Collectors.toList()).contains(sysOrg.getParentId());
        if(errorLevel) {
            throw new CommonException("不可选择上级组织：{}", this.getById(originDataList, sysOrg.getParentId()).getName());
        }
        this.updateById(sysOrg);

        // 发布更新事件
        CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(sysOrg));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<SysOrgIdParam> sysOrgIdParamList) {
        List<String> orgIdList = CollStreamUtil.toList(sysOrgIdParamList, SysOrgIdParam::getId);
        if(ObjectUtil.isNotEmpty(orgIdList)) {
            List<SysOrg> allOrgList = this.getAllOrgList();
            // 获取所有子组织
            List<String> toDeleteOrgIdList = CollectionUtil.newArrayList();
            orgIdList.forEach(orgId -> toDeleteOrgIdList.addAll(this.getChildListById(allOrgList, orgId, true).stream()
                    .map(SysOrg::getId).collect(Collectors.toList())));

            // 组织下有人不能删除（直属组织）
            boolean hasOrgUser = sysUserService.count(new LambdaQueryWrapper<SysUser>().in(SysUser::getOrgId, toDeleteOrgIdList)) > 0;
            if(hasOrgUser) {
                throw new CommonException("请先删除组织下的用户");
            }
            // 组织下有人不能删除（兼任组织）
            List<String> positionJsonList = sysUserService.list(new LambdaQueryWrapper<SysUser>()
                    .isNotNull(SysUser::getPositionJson)).stream().map(SysUser::getPositionJson).collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(positionJsonList)) {
                List<String> positionOrgIdList = CollectionUtil.newArrayList();
                positionJsonList.forEach(positionJson -> JSONUtil.toList(JSONUtil.parseArray(positionJson), JSONObject.class)
                        .forEach(jsonObject -> positionOrgIdList.add(jsonObject.getStr("orgId"))));
                boolean hasPositionUser = CollectionUtil.intersectionDistinct(toDeleteOrgIdList, CollectionUtil.removeNull(positionOrgIdList)).size() > 0;
                if(hasPositionUser) {
                    throw new CommonException("请先删除组织下的用户");
                }
            }
            // 组织下有角色不能删除
            boolean hasRole = sysRoleService.count(new LambdaQueryWrapper<SysRole>().in(SysRole::getOrgId, toDeleteOrgIdList)) > 0;
            if(hasRole) {
                throw new CommonException("请先删除组织下的角色");
            }
            // 组织下有职位不能删除
            boolean hasPosition = sysPositionService.count(new LambdaQueryWrapper<SysPosition>().in(SysPosition::getOrgId, toDeleteOrgIdList)) > 0;
            if(hasPosition) {
                throw new CommonException("请先删除组织下的职位");
            }
            // 执行删除
            this.removeByIds(toDeleteOrgIdList);

            // 发布删除事件
            CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.ORG.getValue(), toDeleteOrgIdList);
        }
    }

    @Override
    public SysOrg detail(SysOrgIdParam sysOrgIdParam) {
        return this.queryEntity(sysOrgIdParam.getId());
    }

    @Override
    public SysOrg queryEntity(String id) {
        SysOrg sysOrg = this.getById(id);
        if(ObjectUtil.isEmpty(sysOrg)) {
            throw new CommonException("组织不存在，id值为：{}", id);
        }
        return sysOrg;
    }

    @Override
    public List<SysOrg> getAllOrgList() {
        // 从缓存中取
        Object cacheValue = commonCacheOperator.get(ORG_CACHE_ALL_KEY);
        if(ObjectUtil.isNotEmpty(cacheValue)) {
            return JSONUtil.toList(JSONUtil.parseArray(cacheValue), SysOrg.class);
        }
        List<SysOrg> orgList = this.list(new LambdaQueryWrapper<SysOrg>().orderByAsc(SysOrg::getSortCode));
        if(ObjectUtil.isNotEmpty(orgList)) {
            // 更新到缓存
            commonCacheOperator.put(ORG_CACHE_ALL_KEY, JSONUtil.toJsonStr(orgList));
        }
        return orgList;
    }

    @Override
    public String getOrgIdByOrgFullNameWithCreate(String orgFullName) {
        List<SysOrg> cachedAllOrgList = this.getAllOrgList();
        List<Tree<String>> treeList = TreeUtil.build(cachedAllOrgList.stream().map(sysOrg ->
                new TreeNode<>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()))
                .collect(Collectors.toList()), "0");
        return findOrgIdByOrgName("0", StrUtil.split(orgFullName, StrUtil.DASHED).iterator(), cachedAllOrgList, treeList);
    }

    public String findOrgIdByOrgName(String parentId, Iterator<String> iterator, List<SysOrg> cachedAllOrgList, List<Tree<String>> treeList) {
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
     * 执行创建组织
     *
     * @author xuyuxiang
     * @date 2023/3/8 9:38
     **/
    public String doCreateOrg(String parentId, String orgName, List<SysOrg> cachedAllOrgList) {
        //创建该组织
        SysOrg sysOrg = new SysOrg();
        sysOrg.setName(orgName);
        sysOrg.setCode(RandomUtil.randomString(10));
        sysOrg.setParentId(parentId);
        sysOrg.setCategory("0".equals(parentId)?SysOrgCategoryEnum.COMPANY.getValue():SysOrgCategoryEnum.DEPT.getValue());
        sysOrg.setSortCode(99);
        this.save(sysOrg);
        // 发布增加事件
        CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(sysOrg));
        // 将该组织加入缓存
        cachedAllOrgList.add(sysOrg);
        // 更新缓存
        commonCacheOperator.put(ORG_CACHE_ALL_KEY, cachedAllOrgList);
        return sysOrg.getId();
    }

    /* ====组织部分所需要用到的选择器==== */

    @Override
    public List<Tree<String>> orgTreeSelector() {
        List<SysOrg> sysOrgList = this.getAllOrgList();
        List<TreeNode<String>> treeNodeList = sysOrgList.stream().map(sysOrg ->
                new TreeNode<>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public Page<SysOrg> orgListSelector(SysOrgSelectorOrgListParam sysOrgSelectorOrgListParam) {
        LambdaQueryWrapper<SysOrg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询部分字段
        lambdaQueryWrapper.select(SysOrg::getId, SysOrg::getParentId, SysOrg::getName,
                SysOrg::getCategory, SysOrg::getSortCode);
        if(ObjectUtil.isNotEmpty(sysOrgSelectorOrgListParam.getParentId())) {
            lambdaQueryWrapper.eq(SysOrg::getParentId, sysOrgSelectorOrgListParam.getParentId());
        }
        if(ObjectUtil.isNotEmpty(sysOrgSelectorOrgListParam.getSearchKey())) {
            lambdaQueryWrapper.like(SysOrg::getName, sysOrgSelectorOrgListParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(SysOrg::getSortCode);
        return this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }

    @Override
    public Page<SysUser> userSelector(SysOrgSelectorUserParam sysOrgSelectorUserParam) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 只查询部分字段
        lambdaQueryWrapper.select(SysUser::getId, SysUser::getAvatar, SysUser::getOrgId, SysUser::getPositionId, SysUser::getAccount,
                SysUser::getName, SysUser::getSortCode, SysUser::getGender, SysUser::getEntryDate);
        // 如果查询条件为空，则直接查询
        if(ObjectUtil.isAllEmpty(sysOrgSelectorUserParam.getOrgId(), sysOrgSelectorUserParam.getSearchKey())) {
            return sysUserService.getAllUserSelectorList();
        } else {
            if(ObjectUtil.isNotEmpty(sysOrgSelectorUserParam.getOrgId())) {
                // 如果组织id不为空，则查询该组织及其子组织下的所有人
                List<String> childOrgIdList = CollStreamUtil.toList(this.getChildListById(this
                        .getAllOrgList(), sysOrgSelectorUserParam.getOrgId(), true), SysOrg::getId);
                if (ObjectUtil.isNotEmpty(childOrgIdList)) {
                    lambdaQueryWrapper.in(SysUser::getOrgId, childOrgIdList);
                } else {
                    return new Page<>();
                }
            }
            if(ObjectUtil.isNotEmpty(sysOrgSelectorUserParam.getSearchKey())) {
                lambdaQueryWrapper.like(SysUser::getName, sysOrgSelectorUserParam.getSearchKey());
            }
            lambdaQueryWrapper.orderByAsc(SysUser::getSortCode);
            return sysUserService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
        }
    }

    /* ====以下为各种递归方法==== */

    @Override
    public List<SysOrg> getParentAndChildListById(List<SysOrg> originDataList, String id, boolean includeSelf) {
        List<SysOrg> parentListById = this.getParentListById(originDataList, id, false);
        List<SysOrg> childListById = this.getChildListById(originDataList, id, true);
        parentListById.addAll(childListById);
        return parentListById;
    }

    @Override
    public List<SysOrg> getChildListById(List<SysOrg> originDataList, String id, boolean includeSelf) {
        List<SysOrg> resultList = CollectionUtil.newArrayList();
        execRecursionFindChild(originDataList, id, resultList);
        if(includeSelf) {
            SysOrg self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
    }

    @Override
    public List<SysOrg> getParentListById(List<SysOrg> originDataList, String id, boolean includeSelf) {
        List<SysOrg> resultList = CollectionUtil.newArrayList();
        execRecursionFindParent(originDataList, id, resultList);
        if(includeSelf) {
            SysOrg self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
    }

    public void execRecursionFindChild(List<SysOrg> originDataList, String id, List<SysOrg> resultList) {
        originDataList.forEach(item -> {
            if(item.getParentId().equals(id)) {
                resultList.add(item);
                execRecursionFindChild(originDataList, item.getId(), resultList);
            }
        });
    }

    public void execRecursionFindParent(List<SysOrg> originDataList, String id, List<SysOrg> resultList) {
        originDataList.forEach(item -> {
            if(item.getId().equals(id)) {
                SysOrg parent = this.getById(originDataList, item.getParentId());
                if(ObjectUtil.isNotEmpty(parent)) {
                    resultList.add(parent);
                }
                execRecursionFindParent(originDataList, item.getParentId(), resultList);
            }
        });
    }

    @Override
    public SysOrg getById(List<SysOrg> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, SysOrg::getId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }

    @Override
    public SysOrg getParentById(List<SysOrg> originDataList, String id) {
        SysOrg self = this.getById(originDataList, id);
        return ObjectUtil.isNotEmpty(self)?self:this.getById(originDataList, self.getParentId());
    }

    @Override
    public SysOrg getChildById(List<SysOrg> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, SysOrg::getParentId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }
}
