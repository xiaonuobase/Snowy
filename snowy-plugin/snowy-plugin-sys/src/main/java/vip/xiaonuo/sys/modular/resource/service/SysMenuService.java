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
package vip.xiaonuo.sys.modular.resource.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.sys.modular.resource.entity.SysMenu;
import vip.xiaonuo.sys.modular.resource.entity.SysModule;
import vip.xiaonuo.sys.modular.resource.param.menu.*;

import java.util.List;

/**
 * 菜单Service接口
 *
 * @author xuyuxiang
 * @date 2022/6/27 14:02
 **/
public interface SysMenuService extends IService<SysMenu> {


    /**
     * 获取菜单分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<SysMenu> page(SysMenuPageParam sysMenuPageParam);

    /**
     * 获取菜单树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<Tree<String>> tree(SysMenuTreeParam sysMenuTreeParam);

    /**
     * 添加菜单
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(SysMenuAddParam sysMenuAddParam);

    /**
     * 代码生成菜单插入
     *
     * @author xuyuxiang
     * @date 2022/11/1 14:06
     **/
    String addForGenMenu(String parentId, String busName, String title, String module, String path);

    /**
     * 编辑菜单
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(SysMenuEditParam sysMenuEditParam);

    /**
     * 更改菜单所属模块
     *
     * @author xuyuxiang
     * @date 2022/9/26 13:09
     **/
    void changeModule(SysMenuChangeModuleParam sysMenuChangeModuleParam);

    /**
     * 删除菜单
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<SysMenuIdParam> sysMenuIdParamList);

    /**
     * 获取菜单详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysMenu detail(SysMenuIdParam sysMenuIdParam);

    /**
     * 获取菜单详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysMenu queryEntity(String id);

    /* ====以下为各种递归方法==== */

    /**
     * 根据id获取所有的子数据列表
     *
     * @author xuyuxiang
     * @date 2022/8/2 14:52
     */
    List<SysMenu> getChildListById(List<SysMenu> originDataList, String id, boolean includeSelf);

    /**
     * 根据id获取所有的父数据列表
     *
     * @author xuyuxiang
     * @date 2022/8/2 14:52
     */
    List<SysMenu> getParentListById(List<SysMenu> originDataList, String id, boolean includeSelf);

    /* ====菜单部分所需要用到的选择器==== */

    /**
     * 获取模块选择器
     *
     * @author xuyuxiang
     * @date 2022/8/2 14:53
     */
    List<SysModule> moduleSelector(SysMenuSelectorModuleParam sysMenuSelectorModuleParam);

    /**
     * 获取菜单树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<Tree<String>> menuTreeSelector(SysMenuSelectorMenuParam sysMenuSelectorMenuParam);
}
