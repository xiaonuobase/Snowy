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
package vip.xiaonuo.biz.modular.group.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.group.entity.BizGroup;
import vip.xiaonuo.biz.modular.group.param.*;
import vip.xiaonuo.biz.modular.user.entity.BizUser;

import java.util.List;

/**
 * 用户组Service接口
 *
 * @author yubaoshan
 * @date  2024/12/24 03:33
 **/
public interface BizGroupService extends IService<BizGroup> {

    /**
     * 获取用户组分页
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    Page<BizGroup> page(BizGroupPageParam bizGroupPageParam);

    /**
     * 添加用户组
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    void add(BizGroupAddParam bizGroupAddParam);

    /**
     * 编辑用户组
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    void edit(BizGroupEditParam bizGroupEditParam);

    /**
     * 删除用户组
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    void delete(List<BizGroupIdParam> bizGroupIdParamList);

    /**
     * 获取用户组详情
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     */
    BizGroup detail(BizGroupIdParam bizGroupIdParam);

    /**
     * 获取用户组详情
     *
     * @author yubaoshan
     * @date  2024/12/24 03:33
     **/
    BizGroup queryEntity(String id);

    /**
     * 获取用户组下的用户
     *
     * @author yubaoshan
     * @date 2024/12/21 01:25
     */
    List<String> ownUser(BizGroupIdParam sysGroupIdParam);

    /**
     * 获取机构树选择器
     *
     * @author yubaoshan
     * @date 2024/12/21 01:25
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取用户选择器
     *
     * @author yubaoshan
     * @date 2024/12/21 01:25
     */
    Page<BizUser> userSelector(BizGroupSelectorUserParam bizGroupSelectorUserParam);

    /**
     * 给用户组授权用户
     *
     * @author yubaoshan
     * @date 2024/12/21 01:25
     */
    void grantUser(BizGroupGrantUserParam bizGroupGrantUserParam);
}
