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
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.sys.api.SysTransferApi;

import java.util.List;

/**
 * 人员权限调整控制器
 *
 * @author yubaoshan
 * @date 2026/05/14
 */
@Tag(name = "人员权限调整控制器")
@RestController
@Validated
public class BizTransferController {

    @Resource
    private SysTransferApi sysTransferApi;

    @Operation(summary = "获取用户可转移资源列表")
    @SaCheckPermission("/biz/user/transfer/resourceList")
    @GetMapping("/biz/user/transfer/resourceList")
    public CommonResult<List<JSONObject>> resourceList(@NotBlank(message = "sourceUserId不能为空") String sourceUserId) {
        return CommonResult.data(sysTransferApi.getTransferResourceList(sourceUserId));
    }

    @Operation(summary = "获取资源明细列表")
    @SaCheckPermission("/biz/user/transfer/resourceDetail")
    @GetMapping("/biz/user/transfer/resourceDetail")
    public CommonResult<List<JSONObject>> resourceDetail(@NotBlank(message = "sourceUserId不能为空") String sourceUserId,
                                                         @NotBlank(message = "resourceType不能为空") String resourceType) {
        return CommonResult.data(sysTransferApi.getTransferResourceDetail(sourceUserId, resourceType));
    }

    @Operation(summary = "执行权限转移")
    @CommonLog("执行权限转移")
    @SaCheckPermission("/biz/user/transfer/execute")
    @PostMapping("/biz/user/transfer/execute")
    public CommonResult<String> execute(@RequestBody JSONObject executeParam) {
        sysTransferApi.executeTransfer(executeParam);
        return CommonResult.ok();
    }

    @Operation(summary = "获取机构树选择器")
    @SaCheckPermission("/biz/user/transfer/orgTreeSelector")
    @GetMapping("/biz/user/transfer/orgTreeSelector")
    public CommonResult<List<JSONObject>> orgTreeSelector(JSONObject param) {
        return CommonResult.data(sysTransferApi.getTransferOrgTreeSelector(param));
    }

    @Operation(summary = "获取用户选择器")
    @SaCheckPermission("/biz/user/transfer/userSelector")
    @GetMapping("/biz/user/transfer/userSelector")
    public CommonResult<Page<JSONObject>> userSelector(JSONObject param) {
        return CommonResult.data(sysTransferApi.getTransferUserSelector(param));
    }
}
