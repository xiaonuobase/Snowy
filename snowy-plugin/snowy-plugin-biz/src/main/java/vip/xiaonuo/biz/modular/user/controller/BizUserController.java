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
package vip.xiaonuo.biz.modular.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.position.entity.BizPosition;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.biz.modular.user.param.*;
import vip.xiaonuo.biz.modular.user.result.BizUserRoleResult;
import vip.xiaonuo.biz.modular.user.service.BizUserService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;

import java.io.IOException;
import java.util.List;

/**
 * 人员控制器
 *
 * @author xuyuxiang
 * @date 2022/4/22 9:34
 **/
@Tag(name = "人员控制器")
@RestController
@Validated
public class BizUserController {

    @Resource
    private BizUserService bizUserService;

    /**
     * 获取人员分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取人员分页")
    @SaCheckPermission("/biz/user/page")
    @GetMapping("/biz/user/page")
    public CommonResult<Page<BizUser>> page(BizUserPageParam bizUserPageParam) {
        return CommonResult.data(bizUserService.page(bizUserPageParam));
    }

    /**
     * 添加人员
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "添加人员")
    @CommonLog("添加人员")
    @SaCheckPermission("/biz/user/add")
    @PostMapping("/biz/user/add")
    public CommonResult<String> add(@RequestBody @Valid BizUserAddParam bizUserAddParam) {
        bizUserService.add(bizUserAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑人员
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "编辑人员")
    @CommonLog("编辑人员")
    @SaCheckPermission("/biz/user/edit")
    @PostMapping("/biz/user/edit")
    public CommonResult<String> edit(@RequestBody @Valid BizUserEditParam bizUserEditParam) {
        bizUserService.edit(bizUserEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除人员
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "删除人员")
    @CommonLog("删除人员")
    @SaCheckPermission("/biz/user/delete")
    @PostMapping("/biz/user/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<BizUserIdParam> bizUserIdParamList) {
        bizUserService.delete(bizUserIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取人员详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取人员详情")
    @SaCheckPermission("/biz/user/detail")
    @GetMapping("/biz/user/detail")
    public CommonResult<BizUser> detail(@Valid BizUserIdParam bizUserIdParam) {
        return CommonResult.data(bizUserService.detail(bizUserIdParam));
    }

    /**
     * 禁用人员
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @Operation(summary = "禁用人员")
    @CommonLog("禁用人员")
    @SaCheckPermission("/biz/user/disableUser")
    @PostMapping("/biz/user/disableUser")
    public CommonResult<String> disableUser(@RequestBody BizUserIdParam bizUserIdParam) {
        bizUserService.disableUser(bizUserIdParam);
        return CommonResult.ok();
    }

    /**
     * 启用人员
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @Operation(summary = "启用人员")
    @CommonLog("启用人员")
    @SaCheckPermission("/biz/user/enableUser")
    @PostMapping("/biz/user/enableUser")
    public CommonResult<String> enableUser(@RequestBody @Valid BizUserIdParam bizUserIdParam) {
        bizUserService.enableUser(bizUserIdParam);
        return CommonResult.ok();
    }

    /**
     * 重置人员密码
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @Operation(summary = "重置人员密码")
    @CommonLog("重置人员密码")
    @SaCheckPermission("/biz/user/resetPassword")
    @PostMapping("/biz/user/resetPassword")
    public CommonResult<String> resetPassword(@RequestBody @Valid BizUserIdParam bizUserIdParam) {
        bizUserService.resetPassword(bizUserIdParam);
        return CommonResult.ok();
    }

    /**
     * 人员拥有角色
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取人员拥有角色")
    @SaCheckPermission("/biz/user/ownRole")
    @GetMapping("/biz/user/ownRole")
    public CommonResult<List<String>> ownRole(@Valid BizUserIdParam bizUserIdParam) {
        return CommonResult.data(bizUserService.ownRole(bizUserIdParam));
    }

    /**
     * 给人员授权角色
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "给人员授权角色")
    @CommonLog("给人员授权角色")
    @SaCheckPermission("/biz/user/grantRole")
    @PostMapping("/biz/user/grantRole")
    public CommonResult<String> grantRole(@RequestBody @Valid BizUserGrantRoleParam bizUserGrantRoleParam) {
        bizUserService.grantRole(bizUserGrantRoleParam);
        return CommonResult.ok();
    }

    /**
     * 人员导出
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "人员导出")
    @CommonLog("人员导出")
    @SaCheckPermission("/biz/user/export")
    @GetMapping(value = "/biz/user/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportUser(BizUserExportParam bizUserExportParam, HttpServletResponse response) throws IOException {
        bizUserService.exportUser(bizUserExportParam, response);
    }

    /**
     * 按模板导出人员个人信息
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "导出人员个人信息")
    @CommonLog("导出人员个人信息")
    @SaCheckPermission("/biz/user/exportUserInfo")
    @GetMapping(value = "/biz/user/exportUserInfo", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportUserInfo(BizUserIdParam bizUserIdParam, HttpServletResponse response) throws IOException {
        bizUserService.exportUserInfo(bizUserIdParam, response);
    }

    /* ====人员部分所需要用到的选择器==== */

    /**
     * 获取机构树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取机构树选择器")
    @SaCheckPermission("/biz/user/orgTreeSelector")
    @GetMapping("/biz/user/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(bizUserService.orgTreeSelector());
    }

    /**
     * 获取机构列表选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取机构列表选择器")
    @SaCheckPermission("/biz/user/orgListSelector")
    @GetMapping("/biz/user/orgListSelector")
    public CommonResult<Page<BizOrg>> orgListSelector(BizUserSelectorOrgListParam bizUserSelectorOrgListParam) {
        return CommonResult.data(bizUserService.orgListSelector(bizUserSelectorOrgListParam));
    }

    /**
     * 获取岗位选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取岗位选择器")
    @SaCheckPermission("/biz/user/positionSelector")
    @GetMapping("/biz/user/positionSelector")
    public CommonResult<Page<BizPosition>> positionSelector(BizUserSelectorPositionParam bizUserSelectorPositionParam) {
        return CommonResult.data(bizUserService.positionSelector(bizUserSelectorPositionParam));
    }

    /**
     * 获取角色选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取角色选择器")
    @SaCheckPermission("/biz/user/roleSelector")
    @GetMapping("/biz/user/roleSelector")
    public CommonResult<Page<BizUserRoleResult>> roleSelector(BizUserSelectorRoleParam bizUserSelectorRoleParam) {
        return CommonResult.data(bizUserService.roleSelector(bizUserSelectorRoleParam));
    }

    /**
     * 获取人员选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取人员选择器")
    @SaCheckPermission("/biz/user/userSelector")
    @GetMapping("/biz/user/userSelector")
    public CommonResult<Page<BizUser>> userSelector(BizUserSelectorUserParam bizUserSelectorUserParam) {
        return CommonResult.data(bizUserService.userSelector(bizUserSelectorUserParam));
    }
}
