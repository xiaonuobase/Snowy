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
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

import javax.annotation.Resource;
import java.util.List;
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
        this.save(mobileMenu);
    }

    @Override
    public void edit(MobileMenuEditParam mobileMenuEditParam) {
        MobileMenu mobileMenu = this.queryEntity(mobileMenuEditParam.getId());
        BeanUtil.copyProperties(mobileMenuEditParam, mobileMenu);
        this.updateById(mobileMenu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<MobileMenuIdParam> mobileMenuIdParamList) {
        // 执行删除
        this.removeBatchByIds(CollStreamUtil.toList(mobileMenuIdParamList, MobileMenuIdParam::getId));
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
        List<TreeNode<String>> treeNodeList = resourceList.stream().map(sysMenu ->
                        new TreeNode<>(sysMenu.getId(), sysMenu.getParentId(),
                                sysMenu.getTitle(), sysMenu.getSortCode()).setExtra(JSONUtil.parseObj(sysMenu)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }
}
