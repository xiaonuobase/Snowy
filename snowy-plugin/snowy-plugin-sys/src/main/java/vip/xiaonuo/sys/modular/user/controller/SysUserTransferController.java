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
package vip.xiaonuo.sys.modular.user.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.sys.modular.org.param.SysOrgSelectorTreeParam;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.param.SysUserSelectorUserParam;
import vip.xiaonuo.sys.modular.user.param.SysUserTransferExecuteParam;
import vip.xiaonuo.sys.modular.user.param.SysUserTransferResourceDetailParam;
import vip.xiaonuo.sys.modular.user.param.SysUserTransferResourceListParam;
import vip.xiaonuo.sys.modular.user.result.SysUserTransferResourceResult;
import vip.xiaonuo.sys.modular.user.service.SysUserService;
import vip.xiaonuo.sys.modular.user.service.SysUserTransferService;

import java.util.List;

/**
 * 权限转移控制器
 *
 * @author yubaoshan
 * @date 2026/05/13
 */
@Tag(name = "权限转移控制器")
@RestController
@Validated
public class SysUserTransferController {

    @Resource
    private SysUserTransferService sysUserTransferService;

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "获取用户可转移资源列表")
    @GetMapping("/sys/user/transfer/resourceList")
    public CommonResult<List<SysUserTransferResourceResult>> resourceList(@Valid SysUserTransferResourceListParam sysUserTransferResourceListParam) {
        return CommonResult.data(sysUserTransferService.resourceList(sysUserTransferResourceListParam));
    }

    @Operation(summary = "获取资源明细列表")
    @GetMapping("/sys/user/transfer/resourceDetail")
    public CommonResult<List<JSONObject>> resourceDetail(@Valid SysUserTransferResourceDetailParam sysUserTransferResourceDetailParam) {
        return CommonResult.data(sysUserTransferService.resourceDetail(sysUserTransferResourceDetailParam));
    }

    @Operation(summary = "执行权限转移")
    @CommonLog("执行权限转移")
    @PostMapping("/sys/user/transfer/execute")
    public CommonResult<String> execute(@RequestBody @Valid SysUserTransferExecuteParam sysUserTransferExecuteParam) {
        sysUserTransferService.execute(sysUserTransferExecuteParam);
        return CommonResult.ok();
    }

    @Operation(summary = "获取机构树选择器")
    @GetMapping("/sys/user/transfer/orgTreeSelector")
    public CommonResult<List<JSONObject>> orgTreeSelector(SysOrgSelectorTreeParam sysOrgSelectorTreeParam) {
        return CommonResult.data(sysOrgService.orgTreeSelector(sysOrgSelectorTreeParam));
    }

    @Operation(summary = "获取用户选择器")
    @GetMapping("/sys/user/transfer/userSelector")
    public CommonResult<Page<SysUser>> userSelector(SysUserSelectorUserParam sysUserSelectorUserParam) {
        return CommonResult.data(sysUserService.userSelector(sysUserSelectorUserParam));
    }
}
