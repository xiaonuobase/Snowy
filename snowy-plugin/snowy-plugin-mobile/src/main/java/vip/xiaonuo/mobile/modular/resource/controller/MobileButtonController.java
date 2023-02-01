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
package vip.xiaonuo.mobile.modular.resource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.mobile.modular.resource.entity.MobileButton;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonAddParam;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonEditParam;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonIdParam;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonPageParam;
import vip.xiaonuo.mobile.modular.resource.service.MobileButtonService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 移动端按钮控制器
 *
 * @author xuyuxiang
 * @date 2022/6/27 13:56
 **/
@Api(tags = "移动端按钮控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 3)
@RestController
@Validated
public class MobileButtonController {

    @Resource
    private MobileButtonService mobileButtonService;

    /**
     * 获取移动端按钮分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取移动端按钮分页")
    @GetMapping("/mobile/button/page")
    public CommonResult<Page<MobileButton>> page(MobileButtonPageParam mobileButtonPageParam) {
        return CommonResult.data(mobileButtonService.page(mobileButtonPageParam));
    }

    /**
     * 添加移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加移动端按钮")
    @CommonLog("添加移动端按钮")
    @PostMapping("/mobile/button/add")
    public CommonResult<String> add(@RequestBody @Valid MobileButtonAddParam mobileButtonAddParam) {
        mobileButtonService.add(mobileButtonAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑移动端按钮")
    @CommonLog("编辑移动端按钮")
    @PostMapping("/mobile/button/edit")
    public CommonResult<String> edit(@RequestBody @Valid MobileButtonEditParam mobileButtonEditParam) {
        mobileButtonService.edit(mobileButtonEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除移动端按钮")
    @CommonLog("删除移动端按钮")
    @PostMapping("/mobile/button/delete")
    public CommonResult<String> delete(@RequestBody @Valid List<MobileButtonIdParam> mobileButtonIdParamList) {
        mobileButtonService.delete(mobileButtonIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取移动端按钮详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取移动端按钮详情")
    @GetMapping("/mobile/button/detail")
    public CommonResult<MobileButton> detail(@Valid MobileButtonIdParam mobileButtonIdParam) {
        return CommonResult.data(mobileButtonService.detail(mobileButtonIdParam));
    }
}
