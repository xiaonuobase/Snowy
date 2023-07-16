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
package vip.xiaonuo.gen.modular.basic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.pojo.CommonValidList;
import vip.xiaonuo.gen.modular.basic.entity.GenBasic;
import vip.xiaonuo.gen.modular.basic.param.*;
import vip.xiaonuo.gen.modular.basic.result.GenBasicMobileModuleSelectorResult;
import vip.xiaonuo.gen.modular.basic.result.GenBasicPreviewResult;
import vip.xiaonuo.gen.modular.basic.result.GenBasicTableColumnResult;
import vip.xiaonuo.gen.modular.basic.result.GenBasicTableResult;
import vip.xiaonuo.gen.modular.basic.service.GenBasicService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

/**
 * 代码生成基础控制器
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Api(tags = "代码生成基础控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class GenBasicController {

    @Resource
    private GenBasicService genBasicService;

    /**
     * 获取代码生成基础分页
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取代码生成基础分页")
    @GetMapping("/gen/basic/page")
    public CommonResult<Page<GenBasic>> page(GenBasicPageParam genBasicPageParam) {
        return CommonResult.data(genBasicService.page(genBasicPageParam));
    }

    /**
     * 添加代码生成基础
     *
     * @author yubaoshan
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加代码生成基础")
    @CommonLog("添加代码生成基础")
    @PostMapping("/gen/basic/add")
    public CommonResult<GenBasic> add(@RequestBody @Valid GenBasicAddParam genBasicAddParam) {
        return CommonResult.data(genBasicService.add(genBasicAddParam));
    }

    /**
     * 编辑代码生成基础
     *
     * @author yubaoshan
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑代码生成基础")
    @CommonLog("编辑代码生成基础")
    @PostMapping("/gen/basic/edit")
    public CommonResult<GenBasic> edit(@RequestBody @Valid GenBasicEditParam genBasicEditParam) {
        return CommonResult.data(genBasicService.edit(genBasicEditParam));
    }

    /**
     * 删除代码生成基础
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除代码生成基础")
    @CommonLog("删除代码生成基础")
    @PostMapping("/gen/basic/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<GenBasicIdParam> genBasicIdParamList) {
        genBasicService.delete(genBasicIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取代码生成基础详情
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取代码生成基础详情")
    @GetMapping("/gen/basic/detail")
    public CommonResult<GenBasic> detail(@Valid GenBasicIdParam genBasicIdParam) {
        return CommonResult.data(genBasicService.detail(genBasicIdParam));
    }

    /**
     * 获取所有表信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("获取所有表信息")
    @GetMapping("/gen/basic/tables")
    public CommonResult<List<GenBasicTableResult>> dbsTable() {
        return CommonResult.data(genBasicService.tables());
    }

    /**
     * 获取表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("获取表内所有字段信息")
    @GetMapping("/gen/basic/tableColumns")
    public CommonResult<List<GenBasicTableColumnResult>> tableColumns(GenBasicTableColumnParam genBasicTableColumnParam) {
        return CommonResult.data(genBasicService.tableColumns(genBasicTableColumnParam));
    }

    /**
     * 执行代码生成
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    @ApiOperationSupport(order = 8)
    @ApiOperation("执行代码生成（压缩包）")
    @CommonLog("执行代码生成（压缩包）")
    @GetMapping(value = "/gen/basic/execGenZip", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void execGenZip(@Valid GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException {
        genBasicService.execGenZip(genBasicIdParam, response);
    }

    /**
     * 执行代码生成
     *
     * @author yubaoshan
     * @date 2022/10/31 02:17
     **/
    @ApiOperationSupport(order = 9)
    @ApiOperation("执行代码生成（项目内）")
    @CommonLog("执行代码生成（项目内）")
    @PostMapping(value = "/gen/basic/execGenPro")
    public CommonResult<String> execGenPro(@RequestBody @Valid GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException {
        genBasicService.execGenPro(genBasicIdParam, response);
        return CommonResult.ok();
    }

    /**
     * 预览代码生成
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    @ApiOperationSupport(order = 10)
    @ApiOperation("预览代码生成")
    @CommonLog("预览代码生成")
    @GetMapping(value = "/gen/basic/previewGen")
    public CommonResult<GenBasicPreviewResult> previewGen(@Valid GenBasicIdParam genBasicIdParam) {
        return CommonResult.data(genBasicService.previewGen(genBasicIdParam));
    }

    /**
     * 获取所有移动端模块
     *
     * @author 每天一点
     * @date 2023/7/15 22:36
     */
    @ApiOperationSupport(order = 11)
    @ApiOperation("获取所有移动端模块")
    @GetMapping("/gen/basic/mobileModuleSelector")
    public CommonResult<List<GenBasicMobileModuleSelectorResult>> mobileModuleSelector() {
        return CommonResult.data(genBasicService.mobileModuleSelector());
    }
}

