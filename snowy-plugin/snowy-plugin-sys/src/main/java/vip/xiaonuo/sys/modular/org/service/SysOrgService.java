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
package vip.xiaonuo.sys.modular.org.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.sys.modular.org.entity.SysOrg;
import vip.xiaonuo.sys.modular.org.param.*;
import vip.xiaonuo.sys.modular.user.entity.SysUser;

import java.util.List;

/**
 * 组织Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface SysOrgService extends IService<SysOrg> {

    /**
     * 获取组织分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<SysOrg> page(SysOrgPageParam sysOrgPageParam);

    /**
     * 获取组织树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<Tree<String>> tree();

    /**
     * 添加组织
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(SysOrgAddParam sysOrgAddParam);

    /**
     * 编辑组织
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(SysOrgEditParam sysOrgEditParam);

    /**
     * 删除组织
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<SysOrgIdParam> sysOrgIdParamList);

    /**
     * 获取组织详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysOrg detail(SysOrgIdParam sysOrgIdParam);

    /**
     * 获取组织详情
     *
     * @author xuyuxiang
     * @date 2022/7/25 19:42
     **/
    SysOrg queryEntity(String id);

    /**
     * 获取所有组织
     *
     * @author xuyuxiang
     * @date 2022/7/25 19:42
     **/
    List<SysOrg> getAllOrgList();

    /**
     * 根据组织全名称获取组织id，有则返回，无则创建
     *
     * @author xuyuxiang
     * @date 2023/3/7 15:44
     **/
    String getOrgIdByOrgFullNameWithCreate(String orgFullName);

    /**
     * 根据id获取父子数据列表
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    List<SysOrg> getParentAndChildListById(List<SysOrg> originDataList, String id, boolean includeSelf);

    /**
     * 根据id获取所有的子数据列表
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    List<SysOrg> getChildListById(List<SysOrg> originDataList, String id, boolean includeSelf);

    /**
     * 根据id获取所有的父数据列表
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    List<SysOrg> getParentListById(List<SysOrg> originDataList, String id, boolean includeSelf);

    /**
     * 根据id获取数据
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    SysOrg getById(List<SysOrg> originDataList, String id);

    /**
     * 根据id获取父数据
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    SysOrg getParentById(List<SysOrg> originDataList, String id);

    /**
     * 根据id获取子数据
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    SysOrg getChildById(List<SysOrg> originDataList, String id);

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取组织列表选择器
     *
     * @author xuyuxiang
     * @date 2022/7/22 13:34
     **/
    Page<SysOrg> orgListSelector(SysOrgSelectorOrgListParam sysOrgSelectorOrgListParam);

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<SysUser> userSelector(SysOrgSelectorUserParam sysOrgSelectorUserParam);
}
