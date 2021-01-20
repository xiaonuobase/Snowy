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

XiaoNuo采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改XiaoNuo源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
6.若您的项目无法满足以上几点，可申请商业授权，获取XiaoNuo商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cn.xiaonuo.sys.modular.org.controller;

import com.cn.xiaonuo.core.annotion.BusinessLog;
import com.cn.xiaonuo.core.annotion.DataScope;
import com.cn.xiaonuo.core.annotion.Permission;
import com.cn.xiaonuo.core.enums.LogAnnotionOpTypeEnum;
import com.cn.xiaonuo.core.pojo.base.param.BaseParam;
import com.cn.xiaonuo.core.pojo.response.ResponseData;
import com.cn.xiaonuo.core.pojo.response.SuccessResponseData;
import com.cn.xiaonuo.sys.modular.org.param.SysOrgParam;
import com.cn.xiaonuo.sys.modular.org.service.SysOrgService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 系统组织机构控制器
 *
 * @author xuyuxiang
 * @date 2020/3/20 19:47
 */
@RestController
public class SysOrgController {

    @Resource
    private SysOrgService sysOrgService;

    /**
     * 查询系统机构
     *
     * @author xuyuxiang
     * @date 2020/5/11 15:49
     */
    @Permission
    @DataScope
    @GetMapping("/sysOrg/page")
    @BusinessLog(title = "系统机构_查询", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData page(SysOrgParam sysOrgParam) {
        return new SuccessResponseData(sysOrgService.page(sysOrgParam));
    }

    /**
     * 系统组织机构列表
     *
     * @author xuyuxiang
     * @date 2020/3/26 10:20
     */
    @Permission
    @DataScope
    @GetMapping("/sysOrg/list")
    @BusinessLog(title = "系统组织机构_列表", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData list(SysOrgParam sysOrgParam) {
        return new SuccessResponseData(sysOrgService.list(sysOrgParam));
    }

    /**
     * 添加系统组织机构
     *
     * @author xuyuxiang
     * @date 2020/3/25 14:44
     */
    @Permission
    @DataScope
    @PostMapping("/sysOrg/add")
    @BusinessLog(title = "系统组织机构_增加", opType = LogAnnotionOpTypeEnum.ADD)
    public ResponseData add(@RequestBody @Validated(BaseParam.add.class) SysOrgParam sysOrgParam) {
        sysOrgService.add(sysOrgParam);
        return new SuccessResponseData();
    }

    /**
     * 删除系统组织机构
     *
     * @author xuyuxiang
     * @date 2020/3/25 14:54
     */
    @Permission
    @DataScope
    @PostMapping("/sysOrg/delete")
    @BusinessLog(title = "系统组织机构_删除", opType = LogAnnotionOpTypeEnum.DELETE)
    public ResponseData delete(@RequestBody @Validated(BaseParam.delete.class) SysOrgParam sysOrgParam) {
        sysOrgService.delete(sysOrgParam);
        return new SuccessResponseData();
    }

    /**
     * 编辑系统组织机构
     *
     * @author xuyuxiang
     * @date 2020/3/25 14:54
     */
    @Permission
    @DataScope
    @PostMapping("/sysOrg/edit")
    @BusinessLog(title = "系统组织机构_编辑", opType = LogAnnotionOpTypeEnum.EDIT)
    public ResponseData edit(@RequestBody @Validated(BaseParam.edit.class) SysOrgParam sysOrgParam) {
        sysOrgService.edit(sysOrgParam);
        return new SuccessResponseData();
    }

    /**
     * 查看系统组织机构
     *
     * @author xuyuxiang
     * @date 2020/3/26 9:49
     */
    @Permission
    @GetMapping("/sysOrg/detail")
    @BusinessLog(title = "系统组织机构_查看", opType = LogAnnotionOpTypeEnum.DETAIL)
    public ResponseData detail(@Validated(BaseParam.detail.class) SysOrgParam sysOrgParam) {
        return new SuccessResponseData(sysOrgService.detail(sysOrgParam));
    }

    /**
     * 获取组织机构树
     *
     * @author xuyuxiang
     * @date 2020/3/26 11:55
     */
    @Permission
    @DataScope
    @GetMapping("/sysOrg/tree")
    @BusinessLog(title = "系统组织机构_树", opType = LogAnnotionOpTypeEnum.TREE)
    public ResponseData tree(SysOrgParam sysOrgParam) {
        return new SuccessResponseData(sysOrgService.tree(sysOrgParam));
    }
}
