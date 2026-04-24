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
package vip.xiaonuo.biz.modular.org.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.org.param.*;
import vip.xiaonuo.biz.modular.user.entity.BizUser;

import java.util.List;

/**
 * 机构Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface BizOrgService extends IService<BizOrg> {

    /**
     * 获取机构分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizOrg> page(BizOrgPageParam bizOrgPageParam);

    /**
     * 添加机构
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(BizOrgAddParam bizOrgAddParam, String sourceFromType);

    /**
     * 编辑机构
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(BizOrgEditParam bizOrgEditParam);

    /**
     * 删除机构
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<BizOrgIdParam> bizOrgIdParamList);

    /**
     * 获取机构详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    BizOrg detail(BizOrgIdParam bizOrgIdParam);

    /**
     * 获取机构详情
     *
     * @author xuyuxiang
     * @date 2022/7/25 19:42
     **/
    BizOrg queryEntity(String id);

    /**
     * 获取所有机构
     *
     * @author xuyuxiang
     * @date 2022/7/25 19:42
     **/
    List<BizOrg> getAllOrgList();

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
    List<BizOrg> getParentAndChildListById(List<BizOrg> originDataList, String id, boolean includeSelf);

    /**
     * 根据id获取所有的子数据列表
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    List<BizOrg> getChildListById(List<BizOrg> originDataList, String id, boolean includeSelf);

    /**
     * 根据id获取所有的父数据列表
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    List<BizOrg> getParentListById(List<BizOrg> originDataList, String id, boolean includeSelf);

    /**
     * 根据id获取数据
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    BizOrg getById(List<BizOrg> originDataList, String id);

    /**
     * 根据id获取父数据
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    BizOrg getParentById(List<BizOrg> originDataList, String id);

    /**
     * 根据id获取子数据
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    BizOrg getChildById(List<BizOrg> originDataList, String id);

    /**
     * 获取机构树（懒加载）
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<JSONObject> orgTreeSelector(BizOrgSelectorTreeParam bizOrgSelectorTreeParam);

    /**
     * 获取机构列表选择器
     *
     * @author xuyuxiang
     * @date 2022/7/22 13:34
     **/
    Page<BizOrg> orgListSelector(BizOrgSelectorOrgListParam bizOrgSelectorOrgListParam);

    /**
     * 获取人员选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizUser> userSelector(BizOrgSelectorUserParam bizOrgSelectorUserParam);

    /**
     * 复制机构
     *
     * @author yubaoshan
     * @date 2025/12/24 01:30
     */
    void copy(BizOrgCopyParam bizOrgCopyParam);

    /**
     * 清除机构缓存（Redis缓存 + 版本号递增）
     * 当其他模块修改了SYS_ORG表数据时，需要调用此方法同步清除缓存
     *
     * @author yubaoshan
     * @date 2026/2/12
     */
    void clearOrgCache();

    /**
     * 根据orgId列表获取其祖先路径节点（扁平列表，含节点自身及所有祖先）
     * 用于懒加载树的回显场景
     *
     * @author yubaoshan
     * @date 2026/3/22
     */
    List<JSONObject> getAncestorNodes(List<String> orgIdList);
}
