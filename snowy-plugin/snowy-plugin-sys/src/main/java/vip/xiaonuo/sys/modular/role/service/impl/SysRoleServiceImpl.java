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
package vip.xiaonuo.sys.modular.role.service.impl;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.mobile.api.MobileMenuApi;
import vip.xiaonuo.sys.core.enums.SysBuildInEnum;
import vip.xiaonuo.sys.core.enums.SysDataTypeEnum;
import vip.xiaonuo.sys.modular.org.entity.SysOrg;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;
import vip.xiaonuo.sys.modular.relation.entity.SysRelation;
import vip.xiaonuo.sys.modular.relation.enums.SysRelationCategoryEnum;
import vip.xiaonuo.sys.modular.relation.service.SysRelationService;
import vip.xiaonuo.sys.modular.resource.entity.SysMenu;
import vip.xiaonuo.sys.modular.resource.entity.SysModule;
import vip.xiaonuo.sys.modular.resource.enums.SysResourceCategoryEnum;
import vip.xiaonuo.sys.modular.resource.enums.SysResourceMenuTypeEnum;
import vip.xiaonuo.sys.modular.resource.service.SysMenuService;
import vip.xiaonuo.sys.modular.resource.service.SysModuleService;
import vip.xiaonuo.sys.modular.role.entity.SysRole;
import vip.xiaonuo.sys.modular.role.enums.SysRoleCategoryEnum;
import vip.xiaonuo.sys.modular.role.mapper.SysRoleMapper;
import vip.xiaonuo.sys.modular.role.param.*;
import vip.xiaonuo.sys.modular.role.result.*;
import vip.xiaonuo.sys.modular.role.service.SysRoleService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private SysModuleService sysModuleService;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private MobileMenuApi mobileMenuApi;

    @Override
    public Page<SysRole> page(SysRolePageParam sysRolePageParam) {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<SysRole>().checkSqlInjection();
        // 查询部分字段
        queryWrapper.lambda().select(SysRole::getId, SysRole::getOrgId, SysRole::getName,
                SysRole::getCategory, SysRole::getSortCode);
        if(ObjectUtil.isNotEmpty(sysRolePageParam.getOrgId())) {
            queryWrapper.lambda().eq(SysRole::getOrgId, sysRolePageParam.getOrgId());
        }
        if(ObjectUtil.isNotEmpty(sysRolePageParam.getCategory())) {
            queryWrapper.lambda().eq(SysRole::getCategory, sysRolePageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(sysRolePageParam.getSearchKey())) {
            queryWrapper.lambda().like(SysRole::getName, sysRolePageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(sysRolePageParam.getSortField(), sysRolePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(sysRolePageParam.getSortOrder());
            queryWrapper.orderBy(true, sysRolePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(sysRolePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(SysRole::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SysRoleAddParam sysRoleAddParam) {
        SysRoleCategoryEnum.validate(sysRoleAddParam.getCategory());
        if(SysRoleCategoryEnum.ORG.getValue().equals(sysRoleAddParam.getCategory())) {
            if(ObjectUtil.isEmpty(sysRoleAddParam.getOrgId())) {
                throw new CommonException("orgId不能为空");
            }
        } else {
            sysRoleAddParam.setOrgId(null);
        }
        SysRole sysRole = BeanUtil.toBean(sysRoleAddParam, SysRole.class);
        boolean repeatName = this.count(new LambdaQueryWrapper<SysRole>().eq(SysRole::getOrgId, sysRole.getOrgId())
                .eq(SysRole::getName, sysRole.getName())) > 0;
        if(repeatName) {
            if(ObjectUtil.isEmpty(sysRole.getOrgId())) {
                throw new CommonException("存在重复的全局角色，名称为：{}", sysRole.getName());
            } else {
                throw new CommonException("同组织下存在重复的角色，名称为：{}", sysRole.getName());
            }
        }
        sysRole.setCode(RandomUtil.randomString(10));
        this.save(sysRole);

        // 发布增加事件
        CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.ROLE.getValue(), JSONUtil.createArray().put(sysRole));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysRoleEditParam sysRoleEditParam) {
        SysRole sysRole = this.queryEntity(sysRoleEditParam.getId());
        boolean superRole = sysRole.getCode().equals(SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue());
        if(superRole) {
            throw new CommonException("不可编辑超管角色");
        }
        if(SysRoleCategoryEnum.ORG.getValue().equals(sysRoleEditParam.getCategory())) {
            if (ObjectUtil.isEmpty(sysRoleEditParam.getOrgId())) {
                throw new CommonException("orgId不能为空");
            }
        } else {
            sysRoleEditParam.setOrgId(null);
        }
        boolean repeatName = this.count(new LambdaQueryWrapper<SysRole>().eq(SysRole::getOrgId, sysRole.getOrgId())
                .eq(SysRole::getName, sysRole.getName()).ne(SysRole::getId, sysRole.getId())) > 0;
        if(repeatName) {
            if(ObjectUtil.isEmpty(sysRole.getOrgId())) {
                throw new CommonException("存在重复的全局角色，名称为：{}", sysRole.getName());
            } else {
                throw new CommonException("同组织下存在重复的角色，名称为：{}", sysRole.getName());
            }
        }
        BeanUtil.copyProperties(sysRoleEditParam, sysRole);
        this.updateById(sysRole);

        // 发布更新事件
        CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.ROLE.getValue(), JSONUtil.createArray().put(sysRole));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<SysRoleIdParam> sysRoleIdParamList) {
        List<String> sysRoleIdList = CollStreamUtil.toList(sysRoleIdParamList, SysRoleIdParam::getId);
        if(ObjectUtil.isNotEmpty(sysRoleIdList)) {
            boolean containsSuperAdminRole = this.listByIds(sysRoleIdList).stream().map(SysRole::getCode)
                    .collect(Collectors.toSet()).contains(SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue());
            if(containsSuperAdminRole) {
                throw new CommonException("不可删除系统内置超管角色");
            }
            // 级联删除角色与用户关系
            sysRelationService.remove(new LambdaUpdateWrapper<SysRelation>().in(SysRelation::getTargetId, sysRoleIdList)
                    .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue()));
            // 级联删除角色与资源关系
            sysRelationService.remove(new LambdaUpdateWrapper<SysRelation>().in(SysRelation::getObjectId, sysRoleIdList)
                    .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue()));
            // 级联删除角色与权限关系
            sysRelationService.remove(new LambdaUpdateWrapper<SysRelation>().in(SysRelation::getObjectId, sysRoleIdList)
                    .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_PERMISSION.getValue()));
            // 执行删除
            this.removeByIds(sysRoleIdList);

            // 发布删除事件
            CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.ROLE.getValue(), sysRoleIdList);
        }
    }

    @Override
    public SysRole detail(SysRoleIdParam sysRoleIdParam) {
        return this.queryEntity(sysRoleIdParam.getId());
    }

    @Override
    public SysRoleOwnResourceResult ownResource(SysRoleIdParam sysRoleIdParam) {
        SysRoleOwnResourceResult sysRoleOwnResourceResult = new SysRoleOwnResourceResult();
        sysRoleOwnResourceResult.setId(sysRoleIdParam.getId());
        sysRoleOwnResourceResult.setGrantInfoList(sysRelationService.getRelationListByObjectIdAndCategory(sysRoleIdParam.getId(),
                SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue()).stream().map(sysRelation ->
                JSONUtil.toBean(sysRelation.getExtJson(), SysRoleOwnResourceResult.SysRoleOwnResource.class)).collect(Collectors.toList()));
        return sysRoleOwnResourceResult;
    }

    @Override
    public void grantResource(SysRoleGrantResourceParam sysRoleGrantResourceParam) {
        String id = sysRoleGrantResourceParam.getId();
        SysRole sysRole = this.queryEntity(id);
        List<String> menuIdList = sysRoleGrantResourceParam.getGrantInfoList().stream()
                .map(SysRoleGrantResourceParam.SysRoleGrantResource::getMenuId).collect(Collectors.toList());
        if(!SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue().equals(sysRole.getCode())) {
            if(ObjectUtil.isNotEmpty(menuIdList)) {
                Set<String> sysModuleIdList = sysMenuService.listByIds(menuIdList).stream().map(SysMenu::getModule).collect(Collectors.toSet());
                boolean containsSystemModule = sysModuleService.listByIds(sysModuleIdList).stream().map(SysModule::getCode)
                        .collect(Collectors.toSet()).contains(SysBuildInEnum.BUILD_IN_MODULE_CODE.getValue());
                if(containsSystemModule) {
                    throw new CommonException("非超管角色不可被授权系统模块菜单资源");
                }
            }
        }
        List<String> extJsonList = sysRoleGrantResourceParam.getGrantInfoList().stream()
                .map(JSONUtil::toJsonStr).collect(Collectors.toList());
        sysRelationService.saveRelationBatchWithClear(id, menuIdList, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue(),
                extJsonList);
    }

    @Override
    public SysRoleOwnMobileMenuResult ownMobileMenu(SysRoleIdParam sysRoleIdParam) {
        SysRoleOwnMobileMenuResult sysRoleOwnMobileMenuResult = new SysRoleOwnMobileMenuResult();
        sysRoleOwnMobileMenuResult.setId(sysRoleIdParam.getId());
        sysRoleOwnMobileMenuResult.setGrantInfoList(sysRelationService.getRelationListByObjectIdAndCategory(sysRoleIdParam.getId(),
                SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue()).stream().map(sysRelation ->
                JSONUtil.toBean(sysRelation.getExtJson(), SysRoleOwnMobileMenuResult.SysRoleOwnMobileMenu.class)).collect(Collectors.toList()));
        return sysRoleOwnMobileMenuResult;
    }

    @Override
    public void grantMobileMenu(SysRoleGrantMobileMenuParam sysRoleGrantMobileMenuParam) {
        String id = sysRoleGrantMobileMenuParam.getId();
        List<String> menuIdList = sysRoleGrantMobileMenuParam.getGrantInfoList().stream()
                .map(SysRoleGrantMobileMenuParam.SysRoleGrantMobileMenu::getMenuId).collect(Collectors.toList());
        List<String> extJsonList = sysRoleGrantMobileMenuParam.getGrantInfoList().stream()
                .map(JSONUtil::toJsonStr).collect(Collectors.toList());
        sysRelationService.saveRelationBatchWithClear(id, menuIdList, SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue(),
                extJsonList);
    }

    @Override
    public SysRoleOwnPermissionResult ownPermission(SysRoleIdParam sysRoleIdParam) {
        SysRoleOwnPermissionResult sysRoleOwnPermissionResult = new SysRoleOwnPermissionResult();
        sysRoleOwnPermissionResult.setId(sysRoleIdParam.getId());
        sysRoleOwnPermissionResult.setGrantInfoList(sysRelationService.getRelationListByObjectIdAndCategory(sysRoleIdParam.getId(),
                SysRelationCategoryEnum.SYS_ROLE_HAS_PERMISSION.getValue()).stream().map(sysRelation ->
                JSONUtil.toBean(sysRelation.getExtJson(), SysRoleOwnPermissionResult.SysRoleOwnPermission.class)).collect(Collectors.toList()));
        return sysRoleOwnPermissionResult;
    }

    @Override
    public void grantPermission(SysRoleGrantPermissionParam sysRoleGrantPermissionParam) {
        String id = sysRoleGrantPermissionParam.getId();
        List<String> apiUrlList = sysRoleGrantPermissionParam.getGrantInfoList().stream()
                .map(SysRoleGrantPermissionParam.SysRoleGrantPermission::getApiUrl).collect(Collectors.toList());
        List<String> extJsonList = sysRoleGrantPermissionParam.getGrantInfoList().stream()
                .map(JSONUtil::toJsonStr).collect(Collectors.toList());
        sysRelationService.saveRelationBatchWithClear(id, apiUrlList, SysRelationCategoryEnum.SYS_ROLE_HAS_PERMISSION.getValue(),
                extJsonList);
    }

    @Override
    public List<String> ownUser(SysRoleIdParam sysRoleIdParam) {
        return sysRelationService.getRelationObjectIdListByTargetIdAndCategory(sysRoleIdParam.getId(),
                SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
    }

    @Override
    public void grantUser(SysRoleGrantUserParam sysRoleGrantUserParam) {
        String id = sysRoleGrantUserParam.getId();
        List<String> grantInfoList = sysRoleGrantUserParam.getGrantInfoList();
        sysRelationService.remove(new LambdaQueryWrapper<SysRelation>().eq(SysRelation::getTargetId, id)
                .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue()));
        sysRelationService.saveBatch(grantInfoList.stream().map(userId -> {
            SysRelation sysRelation = new SysRelation();
            sysRelation.setObjectId(userId);
            sysRelation.setTargetId(id);
            sysRelation.setCategory(SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
            return sysRelation;
        }).collect(Collectors.toList()));
    }

    @Override
    public SysRole queryEntity(String id) {
        SysRole sysRole = this.getById(id);
        if(ObjectUtil.isEmpty(sysRole)) {
            throw new CommonException("角色不存在，id值为：{}", id);
        }
        return sysRole;
    }

    /* ====角色部分所需要用到的选择器==== */

    @Override
    public List<Tree<String>> orgTreeSelector() {
        List<SysOrg> sysOrgList = sysOrgService.getAllOrgList();
        List<TreeNode<String>> treeNodeList = sysOrgList.stream().map(sysOrg ->
                new TreeNode<>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public List<SysRoleGrantResourceTreeResult> resourceTreeSelector() {
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SysMenu::getCategory, SysResourceCategoryEnum.MODULE.getValue(), SysResourceCategoryEnum.MENU.getValue(),
                SysResourceCategoryEnum.BUTTON.getValue());
        List<SysMenu> allMenuAndButtonAndFieldList = sysMenuService.list(lambdaQueryWrapper);
        List<SysMenu> sysModuleList = CollectionUtil.newArrayList();
        List<SysMenu> sysMenuList = CollectionUtil.newArrayList();
        List<SysMenu> sysButtonList = CollectionUtil.newArrayList();
        allMenuAndButtonAndFieldList.forEach(sysMenu -> {
            if (sysMenu.getCategory().equals(SysResourceCategoryEnum.MODULE.getValue())) {
                sysModuleList.add(sysMenu);
            }
            if (sysMenu.getCategory().equals(SysResourceCategoryEnum.MENU.getValue())) {
                sysMenuList.add(sysMenu);
            }
            if (sysMenu.getCategory().equals(SysResourceCategoryEnum.BUTTON.getValue())) {
                sysButtonList.add(sysMenu);
            }
        });
        List<SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult> leafMenuList = CollectionUtil.newArrayList();
        SysMenu rootSysMenu = new SysMenu();
        rootSysMenu.setId("0");
        rootSysMenu.setParentId("-1");
        rootSysMenu.setSortCode(-1);
        sysMenuList.add(rootSysMenu);
        List<TreeNode<String>> treeNodeList = sysMenuList.stream().map(sysMenu ->
                new TreeNode<>(sysMenu.getId(), sysMenu.getParentId(),
                        sysMenu.getTitle(), sysMenu.getSortCode())).collect(Collectors.toList());
        List<Tree<String>> treeList = TreeUtil.build(treeNodeList, "-1");

        sysMenuList.forEach(sysMenu -> {
            // 排除菜单中的目录
            boolean isLeafMenu = !"0".equals(sysMenu.getId())
                    && !sysMenu.getMenuType().equals(SysResourceMenuTypeEnum.CATALOG.getValue());
            if(isLeafMenu) {
                SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult sysRoleGrantResourceMenuResult =
                        new SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult();
                BeanUtil.copyProperties(sysMenu, sysRoleGrantResourceMenuResult);
                JSONObject parentJsonObject = getParentNode(treeList, sysMenu);
                List<String> parentIdSplitList = StrUtil.split(parentJsonObject.getStr("parentId"), StrUtil.DASHED);
                List<String> parentNameSplitList = StrUtil.split(parentJsonObject.getStr("parentName"), StrUtil.DASHED);
                if(parentNameSplitList.size() > 1) {
                    sysRoleGrantResourceMenuResult.setParentId(parentIdSplitList.get(3));
                    sysRoleGrantResourceMenuResult.setParentName(parentNameSplitList.get(0));
                    StringBuilder selfNamePrefix = new StringBuilder();
                    for(int i = 1; i< parentNameSplitList.size(); i++) {
                        selfNamePrefix.append(parentNameSplitList.get(i)).append(StrUtil.DASHED);
                    }
                    sysRoleGrantResourceMenuResult.setTitle(selfNamePrefix + sysRoleGrantResourceMenuResult.getTitle());
                } else {
                    sysRoleGrantResourceMenuResult.setParentName(parentJsonObject.getStr("parentName"));
                }
                sysRoleGrantResourceMenuResult.setButton(this.getChildListById(sysButtonList, sysMenu.getId(), false)
                        .stream().map(sysMenuItem -> {
                            SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult.SysRoleGrantResourceButtonResult
                                    sysRoleGrantResourceButtonResult = new SysRoleGrantResourceTreeResult
                                    .SysRoleGrantResourceMenuResult.SysRoleGrantResourceButtonResult();
                            BeanUtil.copyProperties(sysMenuItem, sysRoleGrantResourceButtonResult);
                            return sysRoleGrantResourceButtonResult;
                        }).collect(Collectors.toList()));
                leafMenuList.add(sysRoleGrantResourceMenuResult);
            }
        });
        Map<String, List<SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult>> menuListGroup = leafMenuList.stream()
                .collect(Collectors.groupingBy(SysRoleGrantResourceTreeResult.SysRoleGrantResourceMenuResult::getModule));
        return sysModuleList.stream().map(sysModule -> {
            SysRoleGrantResourceTreeResult sysRoleGrantResourceTreeResult = new SysRoleGrantResourceTreeResult();
            sysRoleGrantResourceTreeResult.setId(sysModule.getId());
            sysRoleGrantResourceTreeResult.setTitle(sysModule.getTitle());
            sysRoleGrantResourceTreeResult.setIcon(sysModule.getIcon());
            sysRoleGrantResourceTreeResult.setMenu(menuListGroup.get(sysModule.getId()));
            return sysRoleGrantResourceTreeResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SysRoleGrantMobileMenuTreeResult> mobileMenuTreeSelector() {
        return BeanUtil.copyToList(mobileMenuApi.mobileMenuTreeSelector(), SysRoleGrantMobileMenuTreeResult.class);
    }

    @Override
    public List<String> permissionTreeSelector() {
        List<String> permissionResult = CollectionUtil.newArrayList();
        SpringUtil.getApplicationContext().getBeansOfType(RequestMappingHandlerMapping.class).values()
                .forEach(requestMappingHandlerMapping -> requestMappingHandlerMapping.getHandlerMethods()
                        .forEach((key, value) -> {
                            SaCheckPermission saCheckPermission = value.getMethod().getAnnotation(SaCheckPermission.class);
                            if(ObjectUtil.isNotEmpty(saCheckPermission)) {
                                PathPatternsRequestCondition pathPatternsCondition = key.getPathPatternsCondition();
                                if (pathPatternsCondition != null) {
                                    String apiName = "未定义接口名称";
                                    Operation apiOperation = value.getMethod().getAnnotation(Operation.class);
                                    if(ObjectUtil.isNotEmpty(apiOperation)) {
                                        String annotationValue = apiOperation.summary();
                                        if(ObjectUtil.isNotEmpty(annotationValue)) {
                                            apiName = annotationValue;
                                        }
                                    }
                                    String nm = StrUtil.BRACKET_START + apiName + StrUtil.BRACKET_END;
                                    pathPatternsCondition.getPatterns().forEach(pt -> permissionResult.add(pt + nm));
                                }
                            }
                        }));
        return CollectionUtil.sortByPinyin(permissionResult.stream().filter(api ->
                !api.startsWith("/" + StrUtil.BRACKET_START)
                        && !api.startsWith("/error")
                        && !api.contains("/api-docs")
                        && !api.contains("/swagger-resources")).collect(Collectors.toList()));
    }

    @Override
    public Page<SysRole> roleSelector(SysRoleSelectorRoleParam sysRoleSelectorRoleParam) {
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询部分字段
        lambdaQueryWrapper.select(SysRole::getId, SysRole::getOrgId, SysRole::getName,
                SysRole::getCategory, SysRole::getSortCode);
        if(ObjectUtil.isNotEmpty(sysRoleSelectorRoleParam.getOrgId())) {
            lambdaQueryWrapper.eq(SysRole::getOrgId, sysRoleSelectorRoleParam.getOrgId());
        }
        if(ObjectUtil.isNotEmpty(sysRoleSelectorRoleParam.getCategory())) {
            lambdaQueryWrapper.eq(SysRole::getCategory, sysRoleSelectorRoleParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(sysRoleSelectorRoleParam.getSearchKey())) {
            lambdaQueryWrapper.like(SysRole::getName, sysRoleSelectorRoleParam.getSearchKey());
        }
        if(ObjectUtil.isNotEmpty(sysRoleSelectorRoleParam.getDataScopeList())) {
            lambdaQueryWrapper.in(SysRole::getOrgId, sysRoleSelectorRoleParam.getDataScopeList());
        }
        // 排除超管角色
        if(sysRoleSelectorRoleParam.isExcludeSuperAdmin()) {
            lambdaQueryWrapper.ne(SysRole::getCode, SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue());
        }
        lambdaQueryWrapper.orderByAsc(SysRole::getSortCode);
        return this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }

    @Override
    public Page<SysUser> userSelector(SysRoleSelectorUserParam sysRoleSelectorUserParam) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 只查询部分字段
        lambdaQueryWrapper.select(SysUser::getId, SysUser::getAvatar, SysUser::getOrgId, SysUser::getPositionId, SysUser::getAccount,
                SysUser::getName, SysUser::getSortCode, SysUser::getGender, SysUser::getEntryDate);
        // 如果查询条件为空，则直接查询
        if(ObjectUtil.isAllEmpty(sysRoleSelectorUserParam.getOrgId(), sysRoleSelectorUserParam.getSearchKey())) {
            return sysUserService.getAllUserSelectorList();
        } else {
            if (ObjectUtil.isNotEmpty(sysRoleSelectorUserParam.getOrgId())) {
                // 如果组织id不为空，则查询该组织及其子极其子下的所有人
                List<String> childOrgIdList = CollStreamUtil.toList(sysOrgService.getChildListById(sysOrgService
                        .getAllOrgList(), sysRoleSelectorUserParam.getOrgId(), true), SysOrg::getId);
                if (ObjectUtil.isNotEmpty(childOrgIdList)) {
                    lambdaQueryWrapper.in(SysUser::getOrgId, childOrgIdList);
                } else {
                    return new Page<>();
                }
            }
            if (ObjectUtil.isNotEmpty(sysRoleSelectorUserParam.getSearchKey())) {
                lambdaQueryWrapper.like(SysUser::getName, sysRoleSelectorUserParam.getSearchKey());
            }
            lambdaQueryWrapper.orderByAsc(SysUser::getSortCode);
            return sysUserService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
        }
    }

    /* ====以下为各种递归方法==== */

    public JSONObject getParentNode(List<Tree<String>> treeList, SysMenu sysMenu) {
        List<Tree<String>> resultList = CollectionUtil.newArrayList();
        getNode(treeList, sysMenu.getId(), resultList);
        JSONObject jsonObject = JSONUtil.createObj();
        if(ObjectUtil.isNotEmpty(resultList)) {
            Tree<String> currentNode = resultList.get(0);
            if("0".equals(currentNode.getId()) || "0".equals(currentNode.getParentId())) {
                jsonObject.set("parentId", sysMenu.getId());
                jsonObject.set("parentName", sysMenu.getTitle());
            } else {
                jsonObject.set("parentId", StrUtil.join(StrUtil.DASHED, CollectionUtil.reverse(CollectionUtil
                        .removeNull(this.getParentsId(currentNode, false)))));
                jsonObject.set("parentName", StrUtil.join(StrUtil.DASHED, CollectionUtil.reverse(CollectionUtil
                        .removeNull(TreeUtil.getParentsName(currentNode, false)))));
            }
        } else {
            jsonObject.set("parentId", sysMenu.getId());
            jsonObject.set("parentName", sysMenu.getTitle());
        }
        return jsonObject;
    }

    public List<String> getParentsId(Tree<String> node, boolean includeCurrentNode) {
        final List<String> result = new ArrayList<>();
        if (null == node) {
            return result;
        }

        if (includeCurrentNode) {
            result.add(node.getId());
        }

        Tree<String> parent = node.getParent();
        while (null != parent) {
            result.add(parent.getId());
            parent = parent.getParent();
        }
        return result;
    }

    public void getNode(List<Tree<String>> treeList, String id, List<Tree<String>> resultList) {
        for (Tree<String> tree: treeList) {
            if(tree.getId().equals(id)) {
                resultList.add(tree);
                break;
            } else {
                List<Tree<String>> children = tree.getChildren();
                if(ObjectUtil.isNotEmpty(children)) {
                    getNode(children, id, resultList);
                }
            }
        }
    }

    public List<SysMenu> getChildListById(List<SysMenu> originDataList, String id, boolean includeSelf) {
        List<SysMenu> sysResourceList = CollectionUtil.newArrayList();
        execRecursionFindChild(originDataList, id, sysResourceList);
        if(includeSelf) {
            SysMenu self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                sysResourceList.add(self);
            }
        }
        return sysResourceList;
    }

    public List<SysMenu> getParentListById(List<SysMenu> originDataList, String id, boolean includeSelf) {
        List<SysMenu> sysResourceList = CollectionUtil.newArrayList();
        execRecursionFindParent(originDataList, id, sysResourceList);
        if(includeSelf) {
            SysMenu self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                sysResourceList.add(self);
            }
        }
        return sysResourceList;
    }

    public void execRecursionFindChild(List<SysMenu> originDataList, String id, List<SysMenu> resultList) {
        originDataList.forEach(item -> {
            if(item.getParentId().equals(id)) {
                resultList.add(item);
                execRecursionFindChild(originDataList, item.getId(), resultList);
            }
        });
    }

    public void execRecursionFindParent(List<SysMenu> originDataList, String id, List<SysMenu> resultList) {
        originDataList.forEach(item -> {
            if(item.getId().equals(id)) {
                SysMenu parent = this.getById(originDataList, item.getParentId());
                if(ObjectUtil.isNotEmpty(parent)) {
                    resultList.add(parent);
                }
                execRecursionFindParent(originDataList, item.getParentId(), resultList);
            }
        });
    }

    public SysMenu getById(List<SysMenu> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, SysMenu::getId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }

    public SysMenu getParentById(List<SysMenu> originDataList, String id) {
        SysMenu self = this.getById(originDataList, id);
        return ObjectUtil.isNotEmpty(self)?self:this.getById(originDataList, self.getParentId());
    }

    public SysMenu getChildById(List<SysMenu> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, SysMenu::getParentId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }
}
