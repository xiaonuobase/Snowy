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
package vip.xiaonuo.biz.modular.user.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.position.entity.BizPosition;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.biz.modular.user.param.*;
import vip.xiaonuo.biz.modular.user.result.BizUserRoleResult;

import java.io.IOException;
import java.util.List;

/**
 * 人员Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface BizUserService extends IService<BizUser> {

    /**
     * 获取人员分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizUser> page(BizUserPageParam bizUserPageParam);

    /**
     * 添加人员
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(BizUserAddParam bizUserAddParam);

    /**
     * 编辑人员
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(BizUserEditParam bizUserEditParam);

    /**
     * 删除人员
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<BizUserIdParam> bizUserIdParamList);

    /**
     * 获取人员详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    BizUser detail(BizUserIdParam bizUserIdPara);

    /**
     * 获取人员详情
     *
     * @author xuyuxiang
     * @date 2022/7/26 17:21
     **/
    BizUser queryEntity(String id);

    /**
     * 禁用人员
     * 
     * @author xuyuxiang
     * @date 2022/7/5 18:20
     **/
    void disableUser(BizUserIdParam bizUserIdParam);

    /**
     * 启用人员
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:21
     **/
    void enableUser(BizUserIdParam bizUserIdParam);

    /**
     * 重置人员密码
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:22
     **/
    void resetPassword(BizUserIdParam bizUserIdParam);

    /**
     * 获取人员拥有角色
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    List<String> ownRole(BizUserIdParam bizUserIdParam);

    /**
     * 给人员授权角色
     *
     * @author xuyuxiang
     * @date 2022/4/29 11:13
     **/
    void grantRole(BizUserGrantRoleParam bizUserGrantRoleParam);

    /**
     * 用户导出
     *
     * @author xuyuxiang
     * @date 2022/8/8 13:16
     **/
    void exportUser(BizUserExportParam bizUserExportParam, HttpServletResponse response) throws IOException;

    /**
     * 导出用户个人信息
     *
     * @author xuyuxiang
     * @date 2022/8/8 13:16
     **/
    void exportUserInfo(BizUserIdParam bizUserIdParam, HttpServletResponse response) throws IOException;

    /* ====人员部分所需要用到的选择器==== */

    /**
     * 获取机构树选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取机构列表选择器
     *
     * @author xuyuxiang
     * @date 2022/7/22 13:34
     **/
    Page<BizOrg> orgListSelector(BizUserSelectorOrgListParam bizUserSelectorOrgListParam);

    /**
     * 获取岗位选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    Page<BizPosition> positionSelector(BizUserSelectorPositionParam bizUserSelectorPositionParam);

    /**
     * 获取角色选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    Page<BizUserRoleResult> roleSelector(BizUserSelectorRoleParam bizUserSelectorRoleParam);

    /**
     * 获取人员选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizUser> userSelector(BizUserSelectorUserParam bizUserSelectorUserParam);
}
