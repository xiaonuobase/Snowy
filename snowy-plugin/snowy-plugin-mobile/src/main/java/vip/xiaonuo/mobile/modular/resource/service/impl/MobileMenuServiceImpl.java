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
package vip.xiaonuo.mobile.modular.resource.service.impl;

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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.mobile.modular.resource.entity.MobileMenu;
import vip.xiaonuo.mobile.modular.resource.entity.MobileModule;
import vip.xiaonuo.mobile.modular.resource.enums.MobileResourceCategoryEnum;
import vip.xiaonuo.mobile.modular.resource.mapper.MobileMenuMapper;
import vip.xiaonuo.mobile.modular.resource.param.menu.*;
import vip.xiaonuo.mobile.modular.resource.service.MobileMenuService;
import vip.xiaonuo.mobile.modular.resource.service.MobileModuleService;
import vip.xiaonuo.sys.api.SysRelationApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 移动端菜单Service接口实现类
 *
 * @author yubaoshan
 * @date  2023/01/28 22:42
 **/
@Service
public class MobileMenuServiceImpl extends ServiceImpl<MobileMenuMapper, MobileMenu> implements MobileMenuService {

    @Resource
    private MobileModuleService mobileModuleService;

    @Resource
    private SysRelationApi sysRelationApi;

    @Override
    public List<Tree<String>> tree(MobileMenuTreeParam mobileMenuTreeParam) {
        LambdaQueryWrapper<MobileMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(mobileMenuTreeParam.getModule())) {
            lambdaQueryWrapper.eq(MobileMenu::getModule, mobileMenuTreeParam.getModule());
        }
        if(ObjectUtil.isNotEmpty(mobileMenuTreeParam.getSearchKey())) {
            lambdaQueryWrapper.like(MobileMenu::getTitle, mobileMenuTreeParam.getSearchKey());
        }
        lambdaQueryWrapper.eq(MobileMenu::getCategory, MobileResourceCategoryEnum.MENU.getValue());
        lambdaQueryWrapper.orderByDesc(MobileMenu::getSortCode);
        List<MobileMenu> mobileMenuList = this.list(lambdaQueryWrapper);
        List<TreeNode<String>> treeNodeList = mobileMenuList.stream().map(mobileMenu ->
                        new TreeNode<>(mobileMenu.getId(), mobileMenu.getParentId(),
                                mobileMenu.getTitle(), mobileMenu.getSortCode()).setExtra(JSONUtil.parseObj(mobileMenu)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public void add(MobileMenuAddParam mobileMenuAddParam) {
        MobileMenu mobileMenu = BeanUtil.toBean(mobileMenuAddParam, MobileMenu.class);
        boolean repeatTitle = this.count(new LambdaQueryWrapper<MobileMenu>().eq(MobileMenu::getParentId, mobileMenu.getParentId())
                .eq(MobileMenu::getModule, mobileMenu.getModule()).eq(MobileMenu::getCategory, MobileResourceCategoryEnum.MENU.getValue())
                .eq(MobileMenu::getTitle, mobileMenu.getTitle())) > 0;
        if(repeatTitle) {
            throw new CommonException("同一模块中，相同父菜单下存在重复的子菜单，名称为：{}", mobileMenu.getTitle());
        }
        List<MobileMenu> originDataList = this.list(new LambdaQueryWrapper<MobileMenu>().eq(MobileMenu::getCategory,
                MobileResourceCategoryEnum.MENU.getValue()));
        if(!"0".equals(mobileMenuAddParam.getParentId())) {
            MobileMenu parentMenu = this.getById(originDataList, mobileMenuAddParam.getParentId());
            if(ObjectUtil.isEmpty(parentMenu)) {
                throw new CommonException("上级菜单不存在，id值为：{}", mobileMenuAddParam.getParentId());
            }
            if(!parentMenu.getModule().equals(mobileMenuAddParam.getModule())) {
                throw new CommonException("module与上级菜单不一致");
            }
        }
        mobileMenu.setCategory(MobileResourceCategoryEnum.MENU.getValue());
        this.save(mobileMenu);
    }

    @Override
    public void edit(MobileMenuEditParam mobileMenuEditParam) {
        MobileMenu mobileMenu = this.queryEntity(mobileMenuEditParam.getId());
        BeanUtil.copyProperties(mobileMenuEditParam, mobileMenu);
        boolean repeatTitle = this.count(new LambdaQueryWrapper<MobileMenu>().eq(MobileMenu::getParentId, mobileMenu.getParentId())
                .eq(MobileMenu::getModule, mobileMenu.getModule()).eq(MobileMenu::getCategory, MobileResourceCategoryEnum.MENU.getValue())
                .eq(MobileMenu::getTitle, mobileMenu.getTitle())
                .ne(MobileMenu::getId, mobileMenu.getId())) > 0;
        if(repeatTitle) {
            throw new CommonException("同一模块中，相同父菜单下存在重复的子菜单，名称为：{}", mobileMenu.getTitle());
        }
        List<MobileMenu> originDataList = this.list(new LambdaQueryWrapper<MobileMenu>().eq(MobileMenu::getCategory,
                MobileResourceCategoryEnum.MENU.getValue()));
        boolean errorLevel = this.getChildListById(originDataList, mobileMenu.getId(), true).stream()
                .map(MobileMenu::getId).collect(Collectors.toList()).contains(mobileMenu.getParentId());
        if(errorLevel) {
            throw new CommonException("不可选择上级菜单：{}", this.getById(originDataList, mobileMenu.getParentId()).getTitle());
        }
        if(!"0".equals(mobileMenuEditParam.getParentId())) {
            MobileMenu parentMenu = this.getById(originDataList, mobileMenuEditParam.getParentId());
            if(ObjectUtil.isEmpty(parentMenu)) {
                throw new CommonException("上级菜单不存在，id值为：{}", mobileMenuEditParam.getParentId());
            }
            if(!parentMenu.getModule().equals(mobileMenuEditParam.getModule())) {
                throw new CommonException("module与上级菜单不一致");
            }
        }
        this.updateById(mobileMenu);
    }

    @Override
    public void changeModule(MobileMenuChangeModuleParam mobileMenuChangeModuleParam) {
        MobileMenu mobileMenu = this.queryEntity(mobileMenuChangeModuleParam.getId());
        if(!"0".equals(mobileMenu.getParentId())) {
            throw new CommonException("非顶级菜单不可修改所属模块");
        }
        List<MobileMenu> mobileMenuList = this.list(new LambdaQueryWrapper<MobileMenu>().eq(MobileMenu::getCategory,
                MobileResourceCategoryEnum.MENU.getValue()));
        List<MobileMenu> mobileMenuChildList = this.getChildListById(mobileMenuList, mobileMenu.getId(), true).stream()
                .peek(mobileMenuTemp -> mobileMenuTemp.setModule(mobileMenuChangeModuleParam.getModule())).collect(Collectors.toList());
        mobileMenuChildList.forEach(mobileMenuTemp -> {
            boolean repeatTitle = this.count(new LambdaQueryWrapper<MobileMenu>().eq(MobileMenu::getParentId, mobileMenuTemp.getParentId())
                    .eq(MobileMenu::getModule, mobileMenuTemp.getModule()).eq(MobileMenu::getCategory, MobileResourceCategoryEnum.MENU.getValue())
                    .eq(MobileMenu::getTitle, mobileMenuTemp.getTitle())
                    .ne(MobileMenu::getId, mobileMenuTemp.getId())) > 0;
            if(repeatTitle) {
                throw new CommonException("同一模块中，相同父菜单下存在重复的子菜单，名称为：{}", mobileMenuTemp.getTitle());
            }
        });
        this.updateBatchById(mobileMenuChildList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<MobileMenuIdParam> mobileMenuIdParamList) {
        List<String> mobileMenuIdList = CollStreamUtil.toList(mobileMenuIdParamList, MobileMenuIdParam::getId);
        if(ObjectUtil.isNotEmpty(mobileMenuIdList)) {
            // 获取菜单下的菜单
            List<MobileMenu> allMenuList = this.list(new LambdaQueryWrapper<MobileMenu>()
                    .in(MobileMenu::getCategory, CollectionUtil.newArrayList(MobileResourceCategoryEnum.MENU.getValue())));
            List<String> toDeleteMenuIdList = CollectionUtil.newArrayList();
            mobileMenuIdList.forEach(menuId -> toDeleteMenuIdList.addAll(this.getChildListById(allMenuList, menuId, true).stream()
                    .map(MobileMenu::getId).collect(Collectors.toList())));
            if(ObjectUtil.isNotEmpty(toDeleteMenuIdList)) {
                // 清除对应的角色与移动端资源信息
                sysRelationApi.removeRoleHasMobileMenuRelation(toDeleteMenuIdList);
                // 执行删除
                this.removeByIds(toDeleteMenuIdList);
            }
        }
    }

    @Override
    public MobileMenu detail(MobileMenuIdParam mobileMenuIdParam) {
        return this.queryEntity(mobileMenuIdParam.getId());
    }

    @Override
    public MobileMenu queryEntity(String id) {
        MobileMenu mobileMenu = this.getById(id);
        if(ObjectUtil.isEmpty(mobileMenu)) {
            throw new CommonException("移动端菜单不存在，id值为：{}", id);
        }
        return mobileMenu;
    }

    @Override
    public List<MobileModule> moduleSelector(MobileMenuSelectorModuleParam mobileMenuSelectorModuleParam) {
        LambdaQueryWrapper<MobileModule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(mobileMenuSelectorModuleParam.getSearchKey())) {
            lambdaQueryWrapper.like(MobileModule::getTitle, mobileMenuSelectorModuleParam.getSearchKey());
        }
        lambdaQueryWrapper.eq(MobileModule::getCategory, MobileResourceCategoryEnum.MODULE.getValue());
        return mobileModuleService.list(lambdaQueryWrapper);
    }

    @Override
    public List<Tree<String>> menuTreeSelector(MobileMenuSelectorMenuParam mobileMenuSelectorMenuParam) {
        LambdaQueryWrapper<MobileMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询类型为菜单的
        lambdaQueryWrapper.eq(MobileMenu::getCategory, MobileResourceCategoryEnum.MENU.getValue());
        if(ObjectUtil.isNotEmpty(mobileMenuSelectorMenuParam.getModule())) {
            lambdaQueryWrapper.eq(MobileMenu::getModule, mobileMenuSelectorMenuParam.getModule());
        }
        List<MobileMenu> resourceList = this.list(lambdaQueryWrapper);
        List<TreeNode<String>> treeNodeList = resourceList.stream().map(mobileMenu ->
                        new TreeNode<>(mobileMenu.getId(), mobileMenu.getParentId(),
                                mobileMenu.getTitle(), mobileMenu.getSortCode()).setExtra(JSONUtil.parseObj(mobileMenu)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public List<JSONObject> mobileMenuTreeSelector() {
        // 模块、菜单、按钮，应该查所有的，当然这里没有已经删除过的
        List<MobileMenu> allModuleAndMenuList = this.list();
        List<MobileMenu> mobileModuleList = CollectionUtil.newArrayList();
        List<MobileMenu> mobileMenuList = CollectionUtil.newArrayList();
        List<MobileMenu> mobileButtonList = CollectionUtil.newArrayList();
        if (ObjectUtil.isEmpty(allModuleAndMenuList)) {
            // 返回空列表
            return CollectionUtil.newArrayList();
        }
        allModuleAndMenuList.forEach(mobileMenu -> {
            if (mobileMenu.getCategory().equals(MobileResourceCategoryEnum.MODULE.getValue())) {
                mobileModuleList.add(mobileMenu);
            }
            if (mobileMenu.getCategory().equals(MobileResourceCategoryEnum.MENU.getValue())) {
                mobileMenuList.add(mobileMenu);
            }
            if (mobileMenu.getCategory().equals(MobileResourceCategoryEnum.BUTTON.getValue())) {
                mobileButtonList.add(mobileMenu);
            }
        });
        List<JSONObject> leafMenuList = CollectionUtil.newArrayList();
        List<TreeNode<String>> treeNodeList = mobileMenuList.stream().map(mobileMenu ->
                new TreeNode<>(mobileMenu.getId(), mobileMenu.getParentId(),
                        mobileMenu.getTitle(), mobileMenu.getSortCode())).collect(Collectors.toList());
        List<Tree<String>> treeList = TreeUtil.build(treeNodeList, "0");
        mobileMenuList.forEach(mobileMenu -> {
            boolean isLeafMenu = this.getChildListById(mobileMenuList, mobileMenu.getId(), false).size() == 0;
            if(isLeafMenu) {
                JSONObject mobileRoleGrantResourceMenuResult = JSONUtil.createObj();
                BeanUtil.copyProperties(mobileMenu, mobileRoleGrantResourceMenuResult);
                JSONObject parentJsonObject = getParentNode(treeList, mobileMenu);
                List<String> parentIdSplitList = StrUtil.split(parentJsonObject.getStr("parentId"), StrUtil.DASHED);
                List<String> parentNameSplitList = StrUtil.split(parentJsonObject.getStr("parentName"), StrUtil.DASHED);
                if(parentNameSplitList.size() > 1) {
                    mobileRoleGrantResourceMenuResult.set("parentId", parentIdSplitList.get(3));
                    mobileRoleGrantResourceMenuResult.set("parentName", parentNameSplitList.get(0));
                    StringBuilder selfNamePrefix = new StringBuilder();
                    for(int i = 1; i< parentNameSplitList.size(); i++) {
                        selfNamePrefix.append(parentNameSplitList.get(i)).append(StrUtil.DASHED);
                    }
                    mobileRoleGrantResourceMenuResult.set("title", selfNamePrefix + mobileRoleGrantResourceMenuResult.getStr("title"));
                } else {
                    mobileRoleGrantResourceMenuResult.set("parentName", parentJsonObject.getStr("parentName"));
                }
                mobileRoleGrantResourceMenuResult.set("button", this.getChildListById(mobileButtonList, mobileMenu.getId(), false)
                        .stream().map(sysMenuItem -> JSONUtil.createObj().set("id", sysMenuItem.getId()).set("title", sysMenuItem.getTitle()))
                        .collect(Collectors.toList()));
                leafMenuList.add(mobileRoleGrantResourceMenuResult);
            }
        });
        Map<String, List<JSONObject>> menuListGroup = leafMenuList.stream()
                .collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("module")));
        return mobileModuleList.stream().map(mobileModule -> {
            JSONObject mobileRoleGrantResourceTreeResult = JSONUtil.createObj();
            mobileRoleGrantResourceTreeResult.set("id", mobileModule.getId());
            mobileRoleGrantResourceTreeResult.set("title", mobileModule.getTitle());
            mobileRoleGrantResourceTreeResult.set("icon", mobileModule.getIcon());
            mobileRoleGrantResourceTreeResult.set("menu", menuListGroup.get(mobileModule.getId()));
            return mobileRoleGrantResourceTreeResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Tree<String>> loginMobileMenuTree(List<String> menuIdList) {
        // 获取所有的菜单和模块列表，并按分类和排序码排序
        List<MobileMenu> allModuleAndMenuAndSpaList = this.list(new LambdaQueryWrapper<MobileMenu>()
                .in(MobileMenu::getCategory, MobileResourceCategoryEnum.MODULE.getValue(), MobileResourceCategoryEnum.MENU.getValue())
                .orderByAsc(CollectionUtil.newArrayList(MobileMenu::getCategory, MobileMenu::getSortCode)));
        // 全部以菜单承载
        List<MobileMenu> allModuleList = CollectionUtil.newArrayList();
        List<MobileMenu> allMenuList = CollectionUtil.newArrayList();
        // 根据类型抽取
        allModuleAndMenuAndSpaList.forEach(mobileMenu -> {
            boolean isModule = mobileMenu.getCategory().equals(MobileResourceCategoryEnum.MODULE.getValue());
            if (isModule) {
                // 抽取所有的模块列表
                allModuleList.add(mobileMenu);
            }
            boolean isMenu = mobileMenu.getCategory().equals(MobileResourceCategoryEnum.MENU.getValue());
            if (isMenu) {
                // 抽取所有的菜单列表
                allMenuList.add(mobileMenu);
            }
        });

        // 定义结果
        List<MobileMenu> resultList = CollectionUtil.newArrayList();

        // 获取拥有的菜单列表
        List<MobileMenu> menuList = allMenuList.stream().filter(mobileMenu ->
                menuIdList.contains(mobileMenu.getId())).collect(Collectors.toList());

        // 对获取到的角色对应的菜单列表进行处理，获取父列表
        menuList.forEach(mobileMenu -> execRecursionFindParent(allMenuList, mobileMenu.getId(), resultList));

        // 将拥有的菜单列表添加
        resultList.addAll(menuList);

        // 获取模块id集合
        Set<String> moduleIdSet = resultList.stream().map(MobileMenu::getModule).collect(Collectors.toSet());

        // 抽取拥有的模块列表
        List<MobileMenu> moduleList = allModuleList.stream().filter(mobileMenu ->
                moduleIdSet.contains(mobileMenu.getId())).collect(Collectors.toList());

        // 如果一个模块都没拥有
        if (ObjectUtil.isEmpty(moduleList)) {
            // 返回空列表
            return CollectionUtil.newArrayList();
        }

        // 将拥有的模块放入集合
        resultList.addAll(moduleList);

        // 最终处理，构造meta
        List<JSONObject> resultJsonObjectList = resultList.stream().map(mobileMenu -> {

            // 将模块的父id设置为0，设置随机pages
            if (mobileMenu.getCategory().equals(MobileResourceCategoryEnum.MODULE.getValue())) {
                mobileMenu.setParentId("0");
                mobileMenu.setPath(StrUtil.SLASH + RandomUtil.randomString(10));
            }
            // 将根菜单的父id设置为模块的id
            if (mobileMenu.getCategory().equals(MobileResourceCategoryEnum.MENU.getValue())) {
                if ("0".equals(mobileMenu.getParentId())) {
                    mobileMenu.setParentId(mobileMenu.getModule());
                }
            }
            JSONObject menuJsonObject = JSONUtil.parseObj(mobileMenu);
            JSONObject metaJsonObject = JSONUtil.createObj();
            metaJsonObject.set("icon", mobileMenu.getIcon());
            metaJsonObject.set("title", mobileMenu.getTitle());
            metaJsonObject.set("type", mobileMenu.getCategory().toLowerCase());
            // 如果是菜单，则设置type菜单类型为小写
            if (mobileMenu.getCategory().equals(MobileResourceCategoryEnum.MENU.getValue())) {
                metaJsonObject.set("type", mobileMenu.getMenuType().toLowerCase());
            }
            menuJsonObject.set("meta", metaJsonObject);
            return menuJsonObject;
        }).collect(Collectors.toList());

        // 执行构造树
        List<TreeNode<String>> treeNodeList = resultJsonObjectList.stream().map(jsonObject ->
                new TreeNode<>(jsonObject.getStr("id"), jsonObject.getStr("parentId"),
                        jsonObject.getStr("title"), jsonObject.getInt("sortCode")).setExtra(JSONUtil.parseObj(jsonObject)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    /* ====以下为各种递归方法==== */

    public JSONObject getParentNode(List<Tree<String>> treeList, MobileMenu mobileMenu) {
        List<Tree<String>> resultList = CollectionUtil.newArrayList();
        getNode(treeList, mobileMenu.getId(), resultList);
        JSONObject jsonObject = JSONUtil.createObj();
        if(ObjectUtil.isNotEmpty(resultList)) {
            Tree<String> currentNode = resultList.get(0);
            if("0".equals(currentNode.getId()) || "0".equals(currentNode.getParentId())) {
                jsonObject.set("parentId", mobileMenu.getId());
                jsonObject.set("parentName", mobileMenu.getTitle());
            } else {
                jsonObject.set("parentId", StrUtil.join(StrUtil.DASHED, CollectionUtil.reverse(CollectionUtil
                        .removeNull(this.getParentsId(currentNode, false)))));
                jsonObject.set("parentName", StrUtil.join(StrUtil.DASHED, CollectionUtil.reverse(CollectionUtil
                        .removeNull(TreeUtil.getParentsName(currentNode, false)))));
            }
        } else {
            jsonObject.set("parentId", mobileMenu.getId());
            jsonObject.set("parentName", mobileMenu.getTitle());
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

    @Override
    public List<MobileMenu> getChildListById(List<MobileMenu> originDataList, String id, boolean includeSelf) {
        List<MobileMenu> resultList = CollectionUtil.newArrayList();
        execRecursionFindChild(originDataList, id, resultList);
        if(includeSelf) {
            MobileMenu self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
    }

    @Override
    public List<MobileMenu> getParentListById(List<MobileMenu> originDataList, String id, boolean includeSelf) {
        List<MobileMenu> resultList = CollectionUtil.newArrayList();
        execRecursionFindParent(originDataList, id, resultList);
        if(includeSelf) {
            MobileMenu self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
    }

    public void execRecursionFindChild(List<MobileMenu> originDataList, String id, List<MobileMenu> resultList) {
        originDataList.forEach(item -> {
            if(item.getParentId().equals(id)) {
                resultList.add(item);
                execRecursionFindChild(originDataList, item.getId(), resultList);
            }
        });
    }

    public void execRecursionFindParent(List<MobileMenu> originDataList, String id, List<MobileMenu> resultList) {
        originDataList.forEach(item -> {
            if(item.getId().equals(id)) {
                MobileMenu parent = this.getById(originDataList, item.getParentId());
                if(ObjectUtil.isNotEmpty(parent)) {
                    resultList.add(parent);
                }
                execRecursionFindParent(originDataList, item.getParentId(), resultList);
            }
        });
    }

    public MobileMenu getById(List<MobileMenu> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, MobileMenu::getId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }

    public MobileMenu getParentById(List<MobileMenu> originDataList, String id) {
        MobileMenu self = this.getById(originDataList, id);
        return ObjectUtil.isNotEmpty(self)?self:this.getById(originDataList, self.getParentId());
    }

    public MobileMenu getChildById(List<MobileMenu> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, MobileMenu::getParentId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }
}
