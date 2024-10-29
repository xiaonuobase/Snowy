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
package vip.xiaonuo.sys.modular.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.sys.core.enums.SysDataTypeEnum;
import vip.xiaonuo.sys.modular.relation.entity.SysRelation;
import vip.xiaonuo.sys.modular.relation.enums.SysRelationCategoryEnum;
import vip.xiaonuo.sys.modular.relation.service.SysRelationService;
import vip.xiaonuo.sys.modular.resource.entity.SysMenu;
import vip.xiaonuo.sys.modular.resource.entity.SysModule;
import vip.xiaonuo.sys.modular.resource.enums.SysResourceCategoryEnum;
import vip.xiaonuo.sys.modular.resource.enums.SysResourceMenuTypeEnum;
import vip.xiaonuo.sys.modular.resource.mapper.SysMenuMapper;
import vip.xiaonuo.sys.modular.resource.param.menu.*;
import vip.xiaonuo.sys.modular.resource.service.SysMenuService;
import vip.xiaonuo.sys.modular.resource.service.SysModuleService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 菜单Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/6/27 14:25
 **/
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private SysModuleService sysModuleService;

    @Override
    public Page<SysMenu> page(SysMenuPageParam sysMenuPageParam) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<SysMenu>().checkSqlInjection();
        queryWrapper.lambda().eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue());
        if(ObjectUtil.isNotEmpty(sysMenuPageParam.getSearchKey())) {
            queryWrapper.lambda().like(SysMenu::getTitle, sysMenuPageParam.getSearchKey());
        }
        if(ObjectUtil.isNotEmpty(sysMenuPageParam.getModule())) {
            queryWrapper.lambda().like(SysMenu::getModule, sysMenuPageParam.getModule());
        }
        if(ObjectUtil.isAllNotEmpty(sysMenuPageParam.getSortField(), sysMenuPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(sysMenuPageParam.getSortOrder());
            queryWrapper.orderBy(true, sysMenuPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(sysMenuPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(SysMenu::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<Tree<String>> tree(SysMenuTreeParam sysMenuTreeParam) {
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue())
                .orderByAsc(SysMenu::getSortCode);
        if(ObjectUtil.isNotEmpty(sysMenuTreeParam.getModule())) {
            lambdaQueryWrapper.eq(SysMenu::getModule, sysMenuTreeParam.getModule());
        }
        if(ObjectUtil.isNotEmpty(sysMenuTreeParam.getSearchKey())) {
            lambdaQueryWrapper.like(SysMenu::getTitle, sysMenuTreeParam.getSearchKey());
        }
        List<SysMenu> resourceList = this.list(lambdaQueryWrapper);

        // 填充上层的父级菜单
        this.fillParentSysMenuInfo(resourceList);

        List<TreeNode<String>> treeNodeList = resourceList.stream().map(sysMenu ->
                new TreeNode<>(sysMenu.getId(), sysMenu.getParentId(),
                        sysMenu.getTitle(), sysMenu.getSortCode()).setExtra(JSONUtil.parseObj(sysMenu)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    private void fillParentSysMenuInfo(List<SysMenu> resourceList) {
        if(CollUtil.isNotEmpty(resourceList)){
            List<SysMenu> parentRelationSysMenus = resourceList.stream().filter(distinctByKey(SysMenu::getParentId)).collect(Collectors.toList());

            List<String> parentIds = null;
            if(CollUtil.isNotEmpty(parentRelationSysMenus)){
                parentIds = CollUtil.newArrayList();
                for(SysMenu parentRelationSysMenu : parentRelationSysMenus){
                    if(!StrUtil.equals(parentRelationSysMenu.getParentId(),"0")){
                        parentIds.add(parentRelationSysMenu.getParentId());
                    }
                }
            }
            if(CollUtil.isNotEmpty(parentIds)){
                LambdaQueryWrapper<SysMenu> parentSysMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
                parentSysMenuLambdaQueryWrapper.in(SysMenu::getId,parentIds);
                List<SysMenu> parentSysMenus = this.list(parentSysMenuLambdaQueryWrapper);
                if(CollUtil.isNotEmpty(parentSysMenus)){
                    this.fillParentSysMenuInfo(parentSysMenus);
                    resourceList.addAll(parentSysMenus);
                }
            }
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SysMenuAddParam sysMenuAddParam) {
        checkParam(sysMenuAddParam);
        SysMenu sysMenu = BeanUtil.toBean(sysMenuAddParam, SysMenu.class);
        boolean repeatTitle = this.count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, sysMenu.getParentId())
                .eq(SysMenu::getModule, sysMenu.getModule()).eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue())
                .eq(SysMenu::getTitle, sysMenu.getTitle())) > 0;
        if(repeatTitle) {
            throw new CommonException("同一模块中，相同父菜单下存在重复的子菜单，名称为：{}", sysMenu.getTitle());
        }
        List<SysMenu> originDataList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getCategory,
                SysResourceCategoryEnum.MENU.getValue()));
        if(!"0".equals(sysMenuAddParam.getParentId())) {
            SysMenu parentMenu = this.getById(originDataList, sysMenuAddParam.getParentId());
            if(ObjectUtil.isEmpty(parentMenu)) {
                throw new CommonException("上级菜单不存在，id值为：{}", sysMenuAddParam.getParentId());
            }
            if(!parentMenu.getModule().equals(sysMenuAddParam.getModule())) {
                throw new CommonException("module与上级菜单不一致");
            }
        }
        sysMenu.setCategory(SysResourceCategoryEnum.MENU.getValue());
        this.save(sysMenu);

        // 发布增加事件
        CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.RESOURCE.getValue(), JSONUtil.createArray().put(sysMenu));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addForGenMenu(String parentId, String busName, String title, String module, String path) {
        // 参数校验
        if(!"0".equals(parentId)) {
            SysMenu parentMenu = this.queryEntity(parentId);
            if(ObjectUtil.isEmpty(parentMenu)) {
                throw new CommonException("上级菜单不存在，id值为：{}", parentId);
            }
            if(!parentMenu.getModule().equals(module)) {
                throw new CommonException("module与上级菜单不一致");
            }
        }
        // 删除老菜单（同时删除其下面的菜单、按钮，清除对应的角色与资源信息
        SysMenu sysOldMenu = this.getOne(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getTitle, title)
                .eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue())
                .eq(SysMenu::getMenuType, SysResourceMenuTypeEnum.MENU.getValue()).eq(SysMenu::getPath, path));
        if(ObjectUtil.isNotEmpty(sysOldMenu)) {
            SysMenuIdParam sysMenuIdParam = new SysMenuIdParam();
            sysMenuIdParam.setId(sysOldMenu.getId());
            this.delete(CollectionUtil.newArrayList(sysMenuIdParam));
        }
        // 插入新菜单
        SysMenu sysMenu = new SysMenu();
        sysMenu.setParentId(parentId);
        sysMenu.setTitle(title);
        sysMenu.setName(busName);
        sysMenu.setCode(RandomUtil.randomString(10));
        sysMenu.setCategory(SysResourceCategoryEnum.MENU.getValue());
        sysMenu.setModule(module);
        sysMenu.setMenuType(SysResourceMenuTypeEnum.MENU.getValue());
        sysMenu.setPath(path);
        sysMenu.setComponent(StrUtil.removePrefix(path, StrUtil.SLASH) + StrUtil.SLASH + "index");
        sysMenu.setIcon("appstore-outlined");
        sysMenu.setSortCode(99);
        this.save(sysMenu);
        return sysMenu.getId();
    }

    private void checkParam(SysMenuAddParam sysMenuAddParam) {
        SysResourceMenuTypeEnum.validate(sysMenuAddParam.getMenuType());
        if(sysMenuAddParam.getTitle().contains(StrUtil.DASHED)) {
            throw new CommonException("title不可包含特殊字符【-】");
        }
        if(SysResourceMenuTypeEnum.MENU.getValue().equals(sysMenuAddParam.getMenuType())) {
            if(ObjectUtil.isEmpty(sysMenuAddParam.getName())) {
                throw new CommonException("name不能为空");
            }
            if(ObjectUtil.isEmpty(sysMenuAddParam.getComponent())) {
                throw new CommonException("component不能为空");
            }
        } else if(SysResourceMenuTypeEnum.IFRAME.getValue().equals(sysMenuAddParam.getMenuType()) ||
                SysResourceMenuTypeEnum.LINK.getValue().equals(sysMenuAddParam.getMenuType())) {
            sysMenuAddParam.setName(RandomUtil.randomNumbers(10));
            sysMenuAddParam.setComponent(null);
        } else {
            sysMenuAddParam.setName(null);
            sysMenuAddParam.setComponent(null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysMenuEditParam sysMenuEditParam) {
        SysMenu sysMenu = this.queryEntity(sysMenuEditParam.getId());
        checkParam(sysMenuEditParam);
        BeanUtil.copyProperties(sysMenuEditParam, sysMenu);
        boolean repeatTitle = this.count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, sysMenu.getParentId())
                .eq(SysMenu::getModule, sysMenu.getModule()).eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue())
                .eq(SysMenu::getTitle, sysMenu.getTitle())
                .ne(SysMenu::getId, sysMenu.getId())) > 0;
        // 不管是哪个改为菜单，设置组件为空
        if(sysMenuEditParam.getMenuType().equals(SysResourceMenuTypeEnum.MENU.getValue())) {
            sysMenuEditParam.setComponent(null);
            sysMenuEditParam.setName(null);
        }
        if(repeatTitle) {
            throw new CommonException("同一模块中，相同父菜单下存在重复的子菜单，名称为：{}", sysMenu.getTitle());
        }
        List<SysMenu> originDataList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getCategory,
                SysResourceCategoryEnum.MENU.getValue()));
        boolean errorLevel = this.getChildListById(originDataList, sysMenu.getId(), true).stream()
                .map(SysMenu::getId).collect(Collectors.toList()).contains(sysMenu.getParentId());
        if(errorLevel) {
            throw new CommonException("不可选择上级菜单：{}", this.getById(originDataList, sysMenu.getParentId()).getName());
        }
        if(!"0".equals(sysMenuEditParam.getParentId())) {
            SysMenu parentMenu = this.getById(originDataList, sysMenuEditParam.getParentId());
            if(ObjectUtil.isEmpty(parentMenu)) {
                throw new CommonException("上级菜单不存在，id值为：{}", sysMenuEditParam.getParentId());
            }
            if(!parentMenu.getModule().equals(sysMenuEditParam.getModule())) {
                throw new CommonException("module与上级菜单不一致");
            }
        }
        this.updateById(sysMenu);

        // 发布更新事件
        CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.RESOURCE.getValue(), JSONUtil.createArray().put(sysMenu));
    }

    @Override
    public void changeModule(SysMenuChangeModuleParam sysMenuChangeModuleParam) {
        SysMenu sysMenu = this.queryEntity(sysMenuChangeModuleParam.getId());
        if(!"0".equals(sysMenu.getParentId())) {
            throw new CommonException("非顶级菜单不可修改所属模块");
        }
        List<SysMenu> sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getCategory,
                SysResourceCategoryEnum.MENU.getValue()));
        List<SysMenu> sysMenuChildList = this.getChildListById(sysMenuList, sysMenu.getId(), true).stream()
                .peek(sysMenuTemp -> sysMenuTemp.setModule(sysMenuChangeModuleParam.getModule())).collect(Collectors.toList());
        sysMenuChildList.forEach(sysMenuTemp -> {
            boolean repeatTitle = this.count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, sysMenuTemp.getParentId())
                    .eq(SysMenu::getModule, sysMenuTemp.getModule()).eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue())
                    .eq(SysMenu::getTitle, sysMenuTemp.getTitle())
                    .ne(SysMenu::getId, sysMenuTemp.getId())) > 0;
            if(repeatTitle) {
                throw new CommonException("同一模块中，相同父菜单下存在重复的子菜单，名称为：{}", sysMenuTemp.getTitle());
            }
        });
        this.updateBatchById(sysMenuChildList);
    }

    private void checkParam(SysMenuEditParam sysMenuEditParam) {
        SysResourceMenuTypeEnum.validate(sysMenuEditParam.getMenuType());
        if(sysMenuEditParam.getTitle().contains(StrUtil.DASHED)) {
            throw new CommonException("title不可包含特殊字符【-】");
        }
        if(SysResourceMenuTypeEnum.MENU.getValue().equals(sysMenuEditParam.getMenuType())) {
            if(ObjectUtil.isEmpty(sysMenuEditParam.getName())) {
                throw new CommonException("name不能为空");
            }
            if(ObjectUtil.isEmpty(sysMenuEditParam.getComponent())) {
                throw new CommonException("component不能为空");
            }
        } else if(SysResourceMenuTypeEnum.IFRAME.getValue().equals(sysMenuEditParam.getMenuType()) ||
                SysResourceMenuTypeEnum.LINK.getValue().equals(sysMenuEditParam.getMenuType())) {
            if(ObjectUtil.isEmpty(sysMenuEditParam.getName())) {
                sysMenuEditParam.setName(RandomUtil.randomNumbers(10));
            }
            sysMenuEditParam.setComponent(null);
        } else {
            sysMenuEditParam.setName(null);
            sysMenuEditParam.setComponent(null);
        }
    }

    @Override
    public void delete(List<SysMenuIdParam> sysMenuIdParamList) {
        List<String> sysMenuIdList = CollStreamUtil.toList(sysMenuIdParamList, SysMenuIdParam::getId);
        if(ObjectUtil.isNotEmpty(sysMenuIdList)) {
            // 获取菜单下的菜单、按钮
            List<SysMenu> allMenuList = this.list(new LambdaQueryWrapper<SysMenu>()
                    .in(SysMenu::getCategory, CollectionUtil.newArrayList(SysResourceCategoryEnum.MENU.getValue(),
                            SysResourceCategoryEnum.BUTTON.getValue())));
            List<String> toDeleteMenuIdList = CollectionUtil.newArrayList();
            sysMenuIdList.forEach(menuId -> toDeleteMenuIdList.addAll(this.getChildListById(allMenuList, menuId, true).stream()
                    .map(SysMenu::getId).collect(Collectors.toList())));
            if(ObjectUtil.isNotEmpty(toDeleteMenuIdList)) {
                // 清除对应的角色与资源信息
                sysRelationService.remove(new LambdaUpdateWrapper<SysRelation>().in(SysRelation::getTargetId, toDeleteMenuIdList)
                        .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_RESOURCE.getValue()));
                // 执行删除
                this.removeByIds(toDeleteMenuIdList);

                // 发布删除事件
                CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.RESOURCE.getValue(), toDeleteMenuIdList);
            }
        }
    }

    @Override
    public SysMenu detail(SysMenuIdParam sysMenuIdParam) {
        return this.queryEntity(sysMenuIdParam.getId());
    }

    @Override
    public SysMenu queryEntity(String id) {
        SysMenu sysMenu = this.getById(id);
        if(ObjectUtil.isEmpty(sysMenu)) {
            throw new CommonException("菜单不存在，id值为：{}", id);
        }
        return sysMenu;
    }

    @Override
    public List<SysModule> moduleSelector(SysMenuSelectorModuleParam sysMenuSelectorModuleParam) {
        LambdaQueryWrapper<SysModule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(sysMenuSelectorModuleParam.getSearchKey())) {
            lambdaQueryWrapper.like(SysModule::getTitle, sysMenuSelectorModuleParam.getSearchKey());
        }
        lambdaQueryWrapper.eq(SysModule::getCategory, SysResourceCategoryEnum.MODULE.getValue());
        return sysModuleService.list(lambdaQueryWrapper);
    }

    @Override
    public List<Tree<String>> menuTreeSelector(SysMenuSelectorMenuParam sysMenuSelectorMenuParam) {
        LambdaQueryWrapper<SysMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询类型为菜单的
        lambdaQueryWrapper.eq(SysMenu::getCategory, SysResourceCategoryEnum.MENU.getValue());
        if(ObjectUtil.isNotEmpty(sysMenuSelectorMenuParam.getModule())) {
            lambdaQueryWrapper.eq(SysMenu::getModule, sysMenuSelectorMenuParam.getModule());
        }
        List<SysMenu> resourceList = this.list(lambdaQueryWrapper);
        List<TreeNode<String>> treeNodeList = resourceList.stream().map(sysMenu ->
                new TreeNode<>(sysMenu.getId(), sysMenu.getParentId(),
                        sysMenu.getTitle(), sysMenu.getSortCode()).setExtra(JSONUtil.parseObj(sysMenu)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    /* ====以下为各种递归方法==== */

    @Override
    public List<SysMenu> getChildListById(List<SysMenu> originDataList, String id, boolean includeSelf) {
        List<SysMenu> resultList = CollectionUtil.newArrayList();
        execRecursionFindChild(originDataList, id, resultList);
        if(includeSelf) {
            SysMenu self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
    }

    @Override
    public List<SysMenu> getParentListById(List<SysMenu> originDataList, String id, boolean includeSelf) {
        List<SysMenu> resultList = CollectionUtil.newArrayList();
        execRecursionFindParent(originDataList, id, resultList);
        if(includeSelf) {
            SysMenu self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
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
