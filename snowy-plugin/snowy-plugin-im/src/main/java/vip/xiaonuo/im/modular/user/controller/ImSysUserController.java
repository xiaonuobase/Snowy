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
package vip.xiaonuo.im.modular.user.controller;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.im.modular.user.param.ImSysUserIdListParam;
import vip.xiaonuo.im.modular.user.param.ImSysUserSelectorUserParam;
import vip.xiaonuo.im.modular.user.service.ImSysUserService;

import javax.validation.Valid;
import java.util.List;

/**
 * IM-系统用户控制器
 *
 * @author chengchuanyao
 * @date 2024/7/19 10:26
 */
@Tag(name = "IM-系统用户控制器")
@RestController
@Validated
@RequiredArgsConstructor
public class ImSysUserController {

    private final ImSysUserService imSysUserService;


    /**
     * 获取用户列表
     *
     * @author chengchuanyao
     * @date 2024/7/19 10:26
     */
    @Operation(summary = "获取IM-获取用户列表")
    @GetMapping("/im/user/list")
    public CommonResult<List<JSONObject>> list() {
        return CommonResult.data(imSysUserService.list());
    }



    /**
     * 获取组织树选择器
     *
     * @author chengchuanyao
     * @date 2025/5/16 15:23
     */
    @ApiOperationSupport(order = 19)
    @Operation(summary = "IM-获取组织树选择器")
    @GetMapping("/im/user/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(imSysUserService.orgTreeSelector());
    }

    /**
     * 根据id集合获取用户集合
     *
     * @author chengchuanyao
     * @date 2025/5/16 15:23
     */
    @ApiOperationSupport(order = 29)
    @Operation(summary = "IM-根据id集合获取用户集合")
    @PostMapping("/im/userCenter/getUserListByIdList")
    public CommonResult<List<JSONObject>> getUserListByIdList(@RequestBody @Valid ImSysUserIdListParam imSysUserIdListParam) {
        return CommonResult.data(imSysUserService.getUserListByIdList(imSysUserIdListParam));
    }

    /**
     * 获取用户选择器
     *
     * @author chengchuanyao
     * @date 2025/5/16 15:23
     */
    @ApiOperationSupport(order = 23)
    @Operation(summary = "IM-获取用户选择器")
    @GetMapping("/im/user/userSelector")
    public CommonResult<Page<JSONObject>> userSelector(ImSysUserSelectorUserParam imSysUserSelectorUserParam) {
        return CommonResult.data(imSysUserService.userSelector(imSysUserSelectorUserParam));
    }
}
