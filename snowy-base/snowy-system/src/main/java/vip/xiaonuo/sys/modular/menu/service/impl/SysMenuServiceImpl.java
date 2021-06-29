/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.sys.modular.menu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.consts.SymbolConstant;
import vip.xiaonuo.core.context.login.LoginContextHolder;
import vip.xiaonuo.core.enums.CommonStatusEnum;
import vip.xiaonuo.core.enums.YesOrNotEnum;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.factory.TreeBuildFactory;
import vip.xiaonuo.core.pojo.node.LoginMenuTreeNode;
import vip.xiaonuo.sys.core.cache.ResourceCache;
import vip.xiaonuo.sys.core.enums.AdminTypeEnum;
import vip.xiaonuo.sys.core.enums.MenuOpenTypeEnum;
import vip.xiaonuo.sys.core.enums.MenuTypeEnum;
import vip.xiaonuo.sys.core.enums.MenuWeightEnum;
import vip.xiaonuo.sys.modular.menu.entity.SysMenu;
import vip.xiaonuo.sys.modular.menu.enums.SysMenuExceptionEnum;
import vip.xiaonuo.sys.modular.menu.mapper.SysMenuMapper;
import vip.xiaonuo.sys.modular.menu.node.MenuBaseTreeNode;
import vip.xiaonuo.sys.modular.menu.param.SysMenuParam;
import vip.xiaonuo.sys.modular.menu.service.SysMenuService;
import vip.xiaonuo.sys.modular.role.service.SysRoleMenuService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.service.SysUserRoleService;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统菜单service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 16:05
 */
@Service
@SuppressWarnings("unchecked")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Resource
    private SysRoleMenuService sysRoleMenuService;

    @Resource
    private ResourceCache resourceCache;

    @Override
    public List<String> getLoginPermissions(Long userId, List<Long> menuIdList) {
        Set<String> permissions = CollectionUtil.newHashSet();
        if (ObjectUtil.isNotEmpty(menuIdList)) {
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysMenu::getId, menuIdList).ne(SysMenu::getType, MenuTypeEnum.DIR.getCode())
                    .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());

            this.list(queryWrapper).forEach(sysMenu -> {
                if(MenuTypeEnum.BTN.getCode().equals(sysMenu.getType())) {
                    permissions.add(sysMenu.getPermission());
                } else {
                    String removePrefix = StrUtil.removePrefix(sysMenu.getRouter(), SymbolConstant.LEFT_DIVIDE);
                    String permission = removePrefix.replaceAll(SymbolConstant.LEFT_DIVIDE, SymbolConstant.COLON);
                    permissions.add(permission);
                }
            });
        }
        return CollectionUtil.newArrayList(permissions);
    }

    @Override
    public List<SysMenu> getLoginMenus(Long userId, String appCode, List<Long> menuIdList) {
        //如果是超级管理员则展示所有系统权重菜单，不能展示业务权重菜单
        SysUser sysUser = sysUserService.getById(userId);
        Integer adminType = sysUser.getAdminType();

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();

        if (AdminTypeEnum.SUPER_ADMIN.getCode().equals(adminType)) {

            //查询权重不为业务权重的且类型不是按钮的
            queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode())
                    .eq(SysMenu::getApplication, appCode)
                    .notIn(SysMenu::getType, MenuTypeEnum.BTN.getCode())
                    .notIn(SysMenu::getWeight, MenuWeightEnum.DEFAULT_WEIGHT.getCode())
                    .orderByAsc(SysMenu::getSort);
        } else {

            //非超级管理员则获取自己角色所拥有的菜单集合
            if (ObjectUtil.isNotEmpty(menuIdList)) {
                queryWrapper.in(SysMenu::getId, menuIdList)
                        .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode())
                        .eq(SysMenu::getApplication, appCode)
                        .notIn(SysMenu::getType, MenuTypeEnum.BTN.getCode())
                        .orderByAsc(SysMenu::getSort);
            } else {
                //如果角色的菜单为空，则查不到菜单
                return CollectionUtil.newArrayList();
            }
        }
        //查询列表
        return this.list(queryWrapper);
    }

    /**
     * 将SysMenu格式菜单转换为LoginMenuTreeNode菜单
     *
     * @author xuyuxiang
     * @date 2020/4/17 17:53
     */
    @Override
    public List<LoginMenuTreeNode> convertSysMenuToLoginMenu(List<SysMenu> sysMenuList) {
        List<LoginMenuTreeNode> antDesignMenuTreeNodeList = CollectionUtil.newArrayList();
        sysMenuList.forEach(sysMenu -> {
            LoginMenuTreeNode loginMenuTreeNode = new LoginMenuTreeNode();
            loginMenuTreeNode.setComponent(sysMenu.getComponent());
            loginMenuTreeNode.setId(sysMenu.getId());
            loginMenuTreeNode.setName(sysMenu.getCode());
            loginMenuTreeNode.setPath(sysMenu.getRouter());
            loginMenuTreeNode.setPid(sysMenu.getPid());
            LoginMenuTreeNode.Meta mateItem = new LoginMenuTreeNode().new Meta();
            mateItem.setIcon(sysMenu.getIcon());
            mateItem.setTitle(sysMenu.getName());
            mateItem.setLink(sysMenu.getLink());
            //是否可见
            mateItem.setShow(!YesOrNotEnum.N.getCode().equals(sysMenu.getVisible()));
            //设置的首页，默认打开此链接
            loginMenuTreeNode.setRedirect(sysMenu.getRedirect());
            //是否是外链
            if (MenuOpenTypeEnum.OUTER.getCode().equals(sysMenu.getOpenType())) {
                //打开外链
                mateItem.setTarget("_blank");
                loginMenuTreeNode.setPath(sysMenu.getLink());
                loginMenuTreeNode.setRedirect(sysMenu.getLink());
            }
            loginMenuTreeNode.setMeta(mateItem);
            antDesignMenuTreeNodeList.add(loginMenuTreeNode);
        });
        return antDesignMenuTreeNodeList;
    }

    @Override
    public List<String> getUserMenuAppCodeList(Long userId, List<Long> roleIdList) {
        Set<String> appCodeSet = CollectionUtil.newHashSet();

        if (ObjectUtil.isNotEmpty(roleIdList)) {
            List<Long> menuIdList = sysRoleMenuService.getRoleMenuIdList(roleIdList);

            if (ObjectUtil.isNotEmpty(menuIdList)) {
                LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(SysMenu::getId, menuIdList)
                        .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
                appCodeSet = this.list(queryWrapper).stream().map(SysMenu::getApplication).collect(Collectors.toSet());
            }
        }

        return CollectionUtil.newArrayList(appCodeSet);
    }

    @Override
    public List<SysMenu> list(SysMenuParam sysMenuParam) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysMenuParam)) {
            //根据所属应用查询
            if (ObjectUtil.isNotEmpty(sysMenuParam.getApplication())) {
                queryWrapper.eq(SysMenu::getApplication, sysMenuParam.getApplication());
            }
            //根据菜单名称模糊查询
            if (ObjectUtil.isNotEmpty(sysMenuParam.getName())) {
                queryWrapper.like(SysMenu::getName, sysMenuParam.getName());
            }
        }
        queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> sysMenuList = this.list(queryWrapper);
        //将结果集处理成树
        return new TreeBuildFactory<SysMenu>().doTreeBuild(sysMenuList);
    }

    @Override
    public void add(SysMenuParam sysMenuParam) {
        // 校验参数
        checkParam(sysMenuParam, false);

        SysMenu sysMenu = new SysMenu();
        BeanUtil.copyProperties(sysMenuParam, sysMenu);

        // 设置新的pid
        String newPids = createNewPids(sysMenuParam.getPid());
        sysMenu.setPids(newPids);

        // 设置启用状态
        sysMenu.setStatus(CommonStatusEnum.ENABLE.getCode());

        this.save(sysMenu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(SysMenuParam sysMenuParam) {
        Long id = sysMenuParam.getId();
        //级联删除子节点
        List<Long> childIdList = this.getChildIdListById(id);
        childIdList.add(id);
        LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SysMenu::getId, childIdList)
                .set(SysMenu::getStatus, CommonStatusEnum.DELETED.getCode());
        this.update(updateWrapper);
        //级联删除该菜单及子菜单对应的角色-菜单表信息
        sysRoleMenuService.deleteRoleMenuListByMenuIdList(childIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(SysMenuParam sysMenuParam) {

        // 校验参数
        checkParam(sysMenuParam, true);

        // 获取修改的菜单的旧数据（库中的）
        SysMenu oldMenu = this.querySysMenu(sysMenuParam);

        // 本菜单旧的pids
        Long oldPid = oldMenu.getPid();
        String oldPids = oldMenu.getPids();

        // 生成新的pid和pids
        Long newPid = sysMenuParam.getPid();
        String newPids = this.createNewPids(sysMenuParam.getPid());

        // 是否更新子应用的标识
        boolean updateSubAppsFlag = false;

        // 是否更新子节点的pids的标识
        boolean updateSubPidsFlag = false;

        // 如果应用有变化
        if (!sysMenuParam.getApplication().equals(oldMenu.getApplication())) {
            // 父节点不是根节点不能移动应用
            if (!oldPid.equals(0L)) {
                throw new ServiceException(SysMenuExceptionEnum.CANT_MOVE_APP);
            }
            updateSubAppsFlag = true;
        }

        // 父节点有变化
        if (!newPid.equals(oldPid)) {
            updateSubPidsFlag = true;
        }

        // 开始更新所有子节点的配置
        if (updateSubAppsFlag || updateSubPidsFlag) {

            // 查找所有叶子节点，包含子节点的子节点
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(SysMenu::getPids, oldMenu.getId());
            List<SysMenu> list = this.list(queryWrapper);

            // 更新所有子节点的应用为当前菜单的应用
            if (ObjectUtil.isNotEmpty(list)) {

                // 更新所有子节点的application
                if (updateSubAppsFlag) {
                    list.forEach(child -> child.setApplication(sysMenuParam.getApplication()));
                }
                // 更新所有子节点的pids
                if (updateSubPidsFlag) {
                    list.forEach(child -> {
                        // 子节点pids组成 = 当前菜单新pids + 当前菜单id + 子节点自己的pids后缀
                        String oldParentCodesPrefix = oldPids + SymbolConstant.LEFT_SQUARE_BRACKETS + oldMenu.getId()
                                + SymbolConstant.RIGHT_SQUARE_BRACKETS + SymbolConstant.COMMA;
                        String oldParentCodesSuffix = child.getPids().substring(oldParentCodesPrefix.length());
                        String menuParentCodes = newPids + SymbolConstant.LEFT_SQUARE_BRACKETS + oldMenu.getId()
                                + SymbolConstant.RIGHT_SQUARE_BRACKETS + SymbolConstant.COMMA + oldParentCodesSuffix;
                        child.setPids(menuParentCodes);
                    });
                }

                this.updateBatchById(list);
            }
        }

        // 拷贝参数到实体中
        BeanUtil.copyProperties(sysMenuParam, oldMenu);

        // 设置新的pids
        oldMenu.setPids(newPids);
        //不能修改状态，用修改状态接口修改状态
        oldMenu.setStatus(null);
        this.updateById(oldMenu);
    }

    @Override
    public SysMenu detail(SysMenuParam sysMenuParam) {
        return this.querySysMenu(sysMenuParam);
    }

    @Override
    public List<MenuBaseTreeNode> tree(SysMenuParam sysMenuParam) {
        List<MenuBaseTreeNode> menuTreeNodeList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();

        if (ObjectUtil.isNotNull(sysMenuParam)) {
            if (ObjectUtil.isNotEmpty(sysMenuParam.getApplication())) {
                queryWrapper.eq(SysMenu::getApplication, sysMenuParam.getApplication());
            }
        }
        queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode())
                .in(SysMenu::getType, CollectionUtil.newArrayList(MenuTypeEnum.DIR.getCode(), MenuTypeEnum.MENU.getCode()));
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysMenu::getSort);
        this.list(queryWrapper).forEach(sysMenu -> {
            MenuBaseTreeNode menuTreeNode = new MenuBaseTreeNode();
            menuTreeNode.setId(sysMenu.getId());
            menuTreeNode.setParentId(sysMenu.getPid());
            menuTreeNode.setValue(String.valueOf(sysMenu.getId()));
            menuTreeNode.setTitle(sysMenu.getName());
            menuTreeNode.setWeight(sysMenu.getSort());
            menuTreeNodeList.add(menuTreeNode);
        });
        return new TreeBuildFactory<MenuBaseTreeNode>().doTreeBuild(menuTreeNodeList);
    }

    @Override
    public List<MenuBaseTreeNode> treeForGrant(SysMenuParam sysMenuParam) {
        List<MenuBaseTreeNode> menuTreeNodeList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        //根据应用查询
        if (ObjectUtil.isNotNull(sysMenuParam)) {
            if (ObjectUtil.isNotEmpty(sysMenuParam.getApplication())) {
                queryWrapper.eq(SysMenu::getApplication, sysMenuParam.getApplication());
            }
        }
        //如果是超级管理员给角色授权菜单时可选择所有菜单
        if (LoginContextHolder.me().isSuperAdmin()) {
            queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
        } else {
            //非超级管理员则获取自己拥有的菜单，分配给人员，防止越级授权
            Long userId = LoginContextHolder.me().getSysLoginUserId();
            List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
            if (ObjectUtil.isNotEmpty(roleIdList)) {
                List<Long> menuIdList = sysRoleMenuService.getRoleMenuIdList(roleIdList);
                if (ObjectUtil.isNotEmpty(menuIdList)) {
                    queryWrapper.in(SysMenu::getId, menuIdList)
                            .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
                } else {
                    //如果角色的菜单为空，则查不到菜单
                    return CollectionUtil.newArrayList();
                }
            } else {
                //如果角色为空，则根本没菜单
                return CollectionUtil.newArrayList();
            }
        }
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysMenu::getSort);
        this.list(queryWrapper).forEach(sysMenu -> {
            MenuBaseTreeNode menuTreeNode = new MenuBaseTreeNode();
            menuTreeNode.setId(sysMenu.getId());
            menuTreeNode.setParentId(sysMenu.getPid());
            menuTreeNode.setValue(String.valueOf(sysMenu.getId()));
            menuTreeNode.setTitle(sysMenu.getName());
            menuTreeNode.setWeight(sysMenu.getSort());
            menuTreeNodeList.add(menuTreeNode);
        });
        return new TreeBuildFactory<MenuBaseTreeNode>().doTreeBuild(menuTreeNodeList);
    }

    @Override
    public boolean hasMenu(String appCode) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getApplication, appCode)
                .ne(SysMenu::getStatus, CommonStatusEnum.DELETED.getCode());
        return this.list(queryWrapper).size() != 0;
    }

    /**
     * 校验参数
     *
     * @author xuyuxiang
     * @date 2020/3/27 9:15
     */
    private void checkParam(SysMenuParam sysMenuParam, boolean isExcludeSelf) {
        //菜单类型（字典 0目录 1菜单 2按钮）
        Integer type = sysMenuParam.getType();

        String router = sysMenuParam.getRouter();

        String permission = sysMenuParam.getPermission();

        Integer openType = sysMenuParam.getOpenType();

        if (MenuTypeEnum.DIR.getCode().equals(type)) {
            if (ObjectUtil.isEmpty(router)) {
                throw new ServiceException(SysMenuExceptionEnum.MENU_ROUTER_EMPTY);
            }
        }

        if (MenuTypeEnum.MENU.getCode().equals(type)) {
            if (ObjectUtil.isEmpty(router)) {
                throw new ServiceException(SysMenuExceptionEnum.MENU_ROUTER_EMPTY);
            }
            if (ObjectUtil.isEmpty(openType)) {
                throw new ServiceException(SysMenuExceptionEnum.MENU_OPEN_TYPE_EMPTY);
            }
        }

        if (MenuTypeEnum.BTN.getCode().equals(type)) {
            if (ObjectUtil.isEmpty(permission)) {
                throw new ServiceException(SysMenuExceptionEnum.MENU_PERMISSION_EMPTY);
            } else {
                Set<String> urlSet = resourceCache.getAllResources();

                if (!permission.contains(SymbolConstant.COLON)) {
                    throw new ServiceException(SysMenuExceptionEnum.MENU_PERMISSION_ERROR);
                }
                permission = SymbolConstant.COLON + permission;
                if (!urlSet.contains(permission.replaceAll(SymbolConstant.COLON, SymbolConstant.LEFT_DIVIDE))) {
                    throw new ServiceException(SysMenuExceptionEnum.MENU_PERMISSION_NOT_EXIST);
                }
            }
        }

        // 如果是编辑菜单时候，pid和id不能一致，一致会导致无限递归
        if (isExcludeSelf) {
            if (sysMenuParam.getId().equals(sysMenuParam.getPid())) {
                throw new ServiceException(SysMenuExceptionEnum.PID_CANT_EQ_ID);
            }

            // 如果是编辑，父id不能为自己的子节点
            List<Long> childIdListById = this.getChildIdListById(sysMenuParam.getId());
            if(ObjectUtil.isNotEmpty(childIdListById)) {
                if(childIdListById.contains(sysMenuParam.getPid())) {
                    throw new ServiceException(SysMenuExceptionEnum.PID_CANT_EQ_CHILD_ID);
                }
            }
        }

        Long id = sysMenuParam.getId();
        String name = sysMenuParam.getName();
        String code = sysMenuParam.getCode();

        LambdaQueryWrapper<SysMenu> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysMenu::getName, name)
                .ne(SysMenu::getStatus, CommonStatusEnum.DELETED.getCode());

        LambdaQueryWrapper<SysMenu> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysMenu::getCode, code)
                .ne(SysMenu::getStatus, CommonStatusEnum.DELETED.getCode());

        if (isExcludeSelf) {
            queryWrapperByName.ne(SysMenu::getId, id);
            queryWrapperByCode.ne(SysMenu::getId, id);
        }
        int countByName = this.count(queryWrapperByName);
        int countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new ServiceException(SysMenuExceptionEnum.MENU_NAME_REPEAT);
        }
        if (countByCode >= 1) {
            throw new ServiceException(SysMenuExceptionEnum.MENU_CODE_REPEAT);
        }
    }

    /**
     * 获取系统菜单
     *
     * @author xuyuxiang
     * @date 2020/3/27 9:13
     */
    private SysMenu querySysMenu(SysMenuParam sysMenuParam) {
        SysMenu sysMenu = this.getById(sysMenuParam.getId());
        if (ObjectUtil.isNull(sysMenu)) {
            throw new ServiceException(SysMenuExceptionEnum.MENU_NOT_EXIST);
        }
        return sysMenu;
    }

    /**
     * 创建pids的值
     * <p>
     * 如果pid是0顶级节点，pids就是 [0],
     * <p>
     * 如果pid不是顶级节点，pids就是 pid菜单的pids + [pid] + ,
     *
     * @author xuyuxiang
     * @date 2020/3/26 11:28
     */
    private String createNewPids(Long pid) {
        if (pid.equals(0L)) {
            return SymbolConstant.LEFT_SQUARE_BRACKETS + 0 + SymbolConstant.RIGHT_SQUARE_BRACKETS
                    + SymbolConstant.COMMA;
        } else {
            //获取父菜单
            SysMenu parentMenu = this.getById(pid);
            return parentMenu.getPids()
                    + SymbolConstant.LEFT_SQUARE_BRACKETS + pid + SymbolConstant.RIGHT_SQUARE_BRACKETS
                    + SymbolConstant.COMMA;
        }
    }

    /**
     * 根据节点id获取所有子节点id集合
     *
     * @author xuyuxiang
     * @date 2020/3/26 11:31
     */
    private List<Long> getChildIdListById(Long id) {
        List<Long> childIdList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SysMenu::getPids, SymbolConstant.LEFT_SQUARE_BRACKETS + id +
                SymbolConstant.RIGHT_SQUARE_BRACKETS);
        this.list(queryWrapper).forEach(sysMenu -> childIdList.add(sysMenu.getId()));
        return childIdList;
    }
}
