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
package vip.xiaonuo.dev.modular.config.controller;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.modular.config.entity.DevConfig;
import vip.xiaonuo.dev.modular.config.param.*;
import vip.xiaonuo.dev.modular.config.service.DevConfigService;
import vip.xiaonuo.sys.api.SysOrgApi;
import vip.xiaonuo.sys.api.SysPositionApi;
import vip.xiaonuo.sys.api.SysRoleApi;

import javax.validation.Valid;
import java.util.List;

/**
 * 配置控制器
 *
 * @author xuyuxiang
 * @date 2022/4/22 10:56
 **/
@Tag(name = "配置控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class DevConfigController {

    @Resource
    private DevConfigService devConfigService;

    @Resource
    private SysPositionApi sysPositionApi;

    @Resource
    private SysOrgApi sysOrgApi;

    @Resource
    private SysRoleApi sysRoleApi;

    /**
     * 获取配置分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @Operation(summary = "获取配置分页")
    @GetMapping("/dev/config/page")
    public CommonResult<Page<DevConfig>> page(DevConfigPageParam devConfigPageParam) {
        return CommonResult.data(devConfigService.page(devConfigPageParam));
    }

    /**
     * 获取系统基础配置
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @Operation(summary = "获取系统基础配置")
    @GetMapping("/dev/config/sysBaseList")
    public CommonResult<List<DevConfig>> sysBaseList() {
        return CommonResult.data(devConfigService.sysBaseList());
    }

    /**
     * 获取配置列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @Operation(summary = "获取配置列表")
    @GetMapping("/dev/config/list")
    public CommonResult<List<DevConfig>> list(DevConfigListParam devConfigListParam) {
        return CommonResult.data(devConfigService.list(devConfigListParam));
    }

    /**
     * 添加配置
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @Operation(summary = "添加配置")
    @CommonLog("添加配置")
    @PostMapping("/dev/config/add")
    public CommonResult<String> add(@RequestBody @Valid DevConfigAddParam devConfigAddParam) {
        devConfigService.add(devConfigAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑配置
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 4)
    @Operation(summary = "编辑配置")
    @CommonLog("编辑配置")
    @PostMapping("/dev/config/edit")
    public CommonResult<String> edit(@RequestBody @Valid DevConfigEditParam devConfigEditParam) {
        devConfigService.edit(devConfigEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除配置
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @Operation(summary = "删除配置")
    @CommonLog("删除配置")
    @PostMapping("/dev/config/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                           List<DevConfigIdParam> devConfigIdParamList) {
        devConfigService.delete(devConfigIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取配置详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @Operation(summary = "获取配置详情")
    @GetMapping("/dev/config/detail")
    public CommonResult<DevConfig> detail(@Valid DevConfigIdParam devConfigIdParam) {
        return CommonResult.data(devConfigService.detail(devConfigIdParam));
    }

    /**
     * 配置批量更新
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @Operation(summary = "配置批量更新")
    @CommonLog("配置批量更新")
    @PostMapping("/dev/config/editBatch")
    public CommonResult<String> editBatch(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                      List<DevConfigBatchParam> devConfigBatchParamList) {
        devConfigService.editBatch(devConfigBatchParamList);
        return CommonResult.ok();
    }

    /**
     * 获取机构选树
     *
     * @author yubaoshan
     * @date 2025/4/23 20:00
     */
    @Operation(summary = "获取机构选树")
    @GetMapping("/dev/config/orgTree")
    public CommonResult<List<Tree<String>>> orgTree() {
        return CommonResult.data(sysOrgApi.orgTreeSelector());
    }

    /**
     * 获取角色选择器
     *
     * @author yubaoshan
     * @date 2025/4/23 20:00
     */
    @Operation(summary = "获取角色选择器")
    @GetMapping("/dev/config/roleSelector")
    public CommonResult<Page<JSONObject>> roleSelector(DevConfigSelectorRoleParam devConfigSelectorRoleParam) {
        return CommonResult.data(sysRoleApi.roleSelector(devConfigSelectorRoleParam.getOrgId(), devConfigSelectorRoleParam.getCategory(),
                devConfigSelectorRoleParam.getSearchKey(), null , false));
    }

    /**
     * 获取机构选择器
     *
     * @author yubaoshan
     * @date 2025/4/23 20:00
     */
    @Operation(summary = "获取机构选择器")
    @GetMapping("/dev/config/orgSelector")
    public CommonResult<Page<JSONObject>> orgSelector(DevConfigSelectorOrgListParam devConfigSelectorOrgListParam) {
        return CommonResult.data(sysOrgApi.orgListSelector(devConfigSelectorOrgListParam.getParentId()));
    }

    /**
     * 获取职位选择器
     *
     * @author yubaoshan
     * @date 2025/4/23 20:00
     */
    @Operation(summary = "获取职位选择器")
    @GetMapping("/dev/config/positionSelector")
    public CommonResult<Page<JSONObject>> positionSelector(@Valid DevConfigSelectorPositionParam devConfigSelectorPositionParam) {
        return CommonResult.data(sysPositionApi.positionSelector(devConfigSelectorPositionParam.getOrgId(), devConfigSelectorPositionParam.getSearchKey(),
                devConfigSelectorPositionParam.getCurrent(), devConfigSelectorPositionParam.getSize()));
    }

}
