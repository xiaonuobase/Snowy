package vip.xiaonuo.im.modular.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.im.modular.user.service.ImSysUserService;

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
     * 分页获取用户列表
     *
     * @author chengchuanyao
     * @date 2024/7/19 10:26
     */
    @Operation(summary = "获取IM-分页获取用户列表")
    @SaCheckPermission("/im/user/page")
    @GetMapping("/im/user/page")
    public CommonResult<Page<JSONObject>> page() {
        return CommonResult.data(imSysUserService.list());
    }
}
