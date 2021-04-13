/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.sys.modular.user.controller;

import vip.xiaonuo.core.annotion.BusinessLog;
import vip.xiaonuo.core.annotion.DataScope;
import vip.xiaonuo.core.annotion.Permission;
import vip.xiaonuo.core.enums.LogAnnotionOpTypeEnum;
import vip.xiaonuo.core.pojo.response.ResponseData;
import vip.xiaonuo.core.pojo.response.SuccessResponseData;
import vip.xiaonuo.sys.modular.user.param.SysUserParam;
import vip.xiaonuo.sys.modular.user.service.SysUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 系统用户控制器
 *
 * @author xuyuxiang
 * @date 2020/3/19 21:14
 */
@RestController
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 查询系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/20 21:00
     */
    @Permission
    @DataScope
    @GetMapping("/sysUser/page")
    @BusinessLog(title = "系统用户_查询", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData page(SysUserParam sysUserParam) {
        return new SuccessResponseData(sysUserService.page(sysUserParam));
    }

    /**
     * 增加系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/add")
    @BusinessLog(title = "系统用户_增加", opType = LogAnnotionOpTypeEnum.ADD)
    public ResponseData add(@RequestBody @Validated(SysUserParam.add.class) SysUserParam sysUserParam) {
        sysUserService.add(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 删除系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/delete")
    @BusinessLog(title = "系统用户_删除", opType = LogAnnotionOpTypeEnum.DELETE)
    public ResponseData delete(@RequestBody @Validated(SysUserParam.delete.class) SysUserParam sysUserParam) {
        sysUserService.delete(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 编辑系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/edit")
    @BusinessLog(title = "系统用户_编辑", opType = LogAnnotionOpTypeEnum.EDIT)
    public ResponseData edit(@RequestBody @Validated(SysUserParam.edit.class) SysUserParam sysUserParam) {
        sysUserService.edit(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 查看系统用户
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @GetMapping("/sysUser/detail")
    @BusinessLog(title = "系统用户_查看", opType = LogAnnotionOpTypeEnum.DETAIL)
    public ResponseData detail(@Validated(SysUserParam.detail.class) SysUserParam sysUserParam) {
        return new SuccessResponseData(sysUserService.detail(sysUserParam));
    }

    /**
     * 修改状态
     *
     * @author xuyuxiang
     * @date 2020/5/25 14:32
     */
    @Permission
    @PostMapping("/sysUser/changeStatus")
    @BusinessLog(title = "系统用户_修改状态", opType = LogAnnotionOpTypeEnum.CHANGE_STATUS)
    public ResponseData changeStatus(@RequestBody @Validated(SysUserParam.changeStatus.class) SysUserParam sysUserParam) {
        sysUserService.changeStatus(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 授权角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:05
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/grantRole")
    @BusinessLog(title = "系统用户_授权角色", opType = LogAnnotionOpTypeEnum.GRANT)
    public ResponseData grantRole(@RequestBody @Validated(SysUserParam.grantRole.class) SysUserParam sysUserParam) {
        sysUserService.grantRole(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 授权数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 16:05
     */
    @Permission
    @DataScope
    @PostMapping("/sysUser/grantData")
    @BusinessLog(title = "系统用户_授权数据", opType = LogAnnotionOpTypeEnum.GRANT)
    public ResponseData grantData(@RequestBody @Validated(SysUserParam.grantData.class) SysUserParam sysUserParam) {
        sysUserService.grantData(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 更新信息
     *
     * @author xuyuxiang
     * @date 2020/4/1 14:27
     */
    @PostMapping("/sysUser/updateInfo")
    @BusinessLog(title = "系统用户_更新信息", opType = LogAnnotionOpTypeEnum.UPDATE)
    public ResponseData updateInfo(@RequestBody @Validated(SysUserParam.updateInfo.class) SysUserParam sysUserParam) {
        sysUserService.updateInfo(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 修改密码
     *
     * @author xuyuxiang
     * @date 2020/4/1 14:42
     */
    @PostMapping("/sysUser/updatePwd")
    @BusinessLog(title = "系统用户_修改密码", opType = LogAnnotionOpTypeEnum.UPDATE)
    public ResponseData updatePwd(@RequestBody @Validated(SysUserParam.updatePwd.class) SysUserParam sysUserParam) {
        sysUserService.updatePwd(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 拥有角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:46
     */
    @Permission
    @GetMapping("/sysUser/ownRole")
    @BusinessLog(title = "系统用户_拥有角色", opType = LogAnnotionOpTypeEnum.DETAIL)
    public ResponseData ownRole(@Validated(SysUserParam.detail.class) SysUserParam sysUserParam) {
        return new SuccessResponseData(sysUserService.ownRole(sysUserParam));
    }

    /**
     * 拥有数据
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:46
     */
    @Permission
    @GetMapping("/sysUser/ownData")
    @BusinessLog(title = "系统用户_拥有数据", opType = LogAnnotionOpTypeEnum.DETAIL)
    public ResponseData ownData(@Validated(SysUserParam.detail.class) SysUserParam sysUserParam) {
        return new SuccessResponseData(sysUserService.ownData(sysUserParam));
    }

    /**
     * 重置密码
     *
     * @author xuyuxiang
     * @date 2020/4/1 14:42
     */
    @Permission
    @PostMapping("/sysUser/resetPwd")
    @BusinessLog(title = "系统用户_重置密码", opType = LogAnnotionOpTypeEnum.UPDATE)
    public ResponseData resetPwd(@RequestBody @Validated(SysUserParam.resetPwd.class) SysUserParam sysUserParam) {
        sysUserService.resetPwd(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 修改头像
     *
     * @author xuyuxiang
     * @date 2020/6/28 15:19
     */
    @PostMapping("/sysUser/updateAvatar")
    @BusinessLog(title = "系统用户_修改头像", opType = LogAnnotionOpTypeEnum.UPDATE)
    public ResponseData updateAvatar(@RequestBody @Validated(SysUserParam.updateAvatar.class) SysUserParam sysUserParam) {
        sysUserService.updateAvatar(sysUserParam);
        return new SuccessResponseData();
    }

    /**
     * 导出系统用户
     *
     * @author xuyuxiang
     * @date 2020/6/30 16:07
     */
    @Permission
    @GetMapping("/sysUser/export")
    @BusinessLog(title = "系统用户_导出", opType = LogAnnotionOpTypeEnum.EXPORT)
    public void export(SysUserParam sysUserParam) {
        sysUserService.export(sysUserParam);
    }


    /**
     * 用户选择器
     *
     * @author xuyuxiang
     * @date 2020/7/3 13:17
     */
    @Permission
    @GetMapping("/sysUser/selector")
    @BusinessLog(title = "系统用户_选择器", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData selector(SysUserParam sysUserParam) {
        return new SuccessResponseData(sysUserService.selector(sysUserParam));
    }
}
