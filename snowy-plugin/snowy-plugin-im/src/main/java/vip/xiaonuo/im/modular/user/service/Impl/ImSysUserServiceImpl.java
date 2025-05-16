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
package vip.xiaonuo.im.modular.user.service.Impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vip.xiaonuo.im.modular.user.param.ImSysUserIdListParam;
import vip.xiaonuo.im.modular.user.param.ImSysUserSelectorUserParam;
import vip.xiaonuo.im.modular.user.service.ImSysUserService;
import vip.xiaonuo.sys.api.SysOrgApi;
import vip.xiaonuo.sys.api.SysUserApi;

import java.util.List;

/**
 * IM-系统用户service实现类
 *
 * @author chengchuanyao
 * @date 2024/7/19 10:24
 */
@RequiredArgsConstructor
@Service
public class ImSysUserServiceImpl implements ImSysUserService {

    private final SysUserApi sysUserApi;

    private final SysOrgApi sysOrgApi;

    /**
     * 获取用户列表
     *
     * @return 用户列表
     */
    @Override
    public List<JSONObject> list() {
        return sysUserApi.listUserWithoutCurrent();
    }


    /**
     * 获取组织树选择器
     *
     * @author chengchuanyao
     * @date 2025/5/16 15:23
     */
    @Override
    public List<Tree<String>> orgTreeSelector() {
        return sysOrgApi.orgTreeSelector();
    }

    /**
     * 根据id集合获取用户集合
     *
     * @author chengchuanyao
     * @date 2025/5/16 15:23
     */
    @Override
    public List<JSONObject> getUserListByIdList(ImSysUserIdListParam imSysUserIdListParam) {
        if (imSysUserIdListParam == null) {
            return List.of();
        }
        List<JSONObject> userIdList = sysUserApi.getUserListByIdListWithoutException(imSysUserIdListParam.getIdList());
        return userIdList;
    }

    /**
     * 获取用户选择器
     *
     * @author chengchuanyao
     * @date 2025/5/16 15:23
     */
    @Override
    public Page<JSONObject> userSelector(ImSysUserSelectorUserParam imSysUserSelectorUserParam) {
        Page<JSONObject> jsonObjectPage = sysUserApi.userSelector(imSysUserSelectorUserParam.getOrgId(), imSysUserSelectorUserParam.getSearchKey());
        if (jsonObjectPage == null) {
            return new Page<>();
        }
        return jsonObjectPage;
    }
}
