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
package vip.xiaonuo.sys.modular.notice.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.core.annotion.BusinessLog;
import vip.xiaonuo.core.annotion.Permission;
import vip.xiaonuo.core.enums.LogAnnotionOpTypeEnum;
import vip.xiaonuo.core.pojo.response.ResponseData;
import vip.xiaonuo.core.pojo.response.SuccessResponseData;
import vip.xiaonuo.sys.modular.notice.param.SysNoticeParam;
import vip.xiaonuo.sys.modular.notice.service.SysNoticeService;

import javax.annotation.Resource;

/**
 * 系统通知公告控制器
 *
 * @author xuyuxiang
 * @date 2020/6/28 17:18
 */
@RestController
public class SysNoticeController {

    @Resource
    private SysNoticeService sysNoticeService;

    /**
     * 查询系统通知公告
     *
     * @author xuyuxiang
     * @date 2020/6/28 17:24
     */
    @Permission
    @GetMapping("/sysNotice/page")
    @BusinessLog(title = "系统通知公告_查询", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData page(SysNoticeParam sysNoticeParam) {
        return new SuccessResponseData(sysNoticeService.page(sysNoticeParam));
    }

    /**
     * 查询我收到的系统通知公告
     *
     * @author xuyuxiang
     * @date 2020/6/29 11:59
     */
    @Permission
    @GetMapping("/sysNotice/received")
    @BusinessLog(title = "系统通知公告_已收", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData received(SysNoticeParam sysNoticeParam) {
        return new SuccessResponseData(sysNoticeService.received(sysNoticeParam));
    }

    /**
     * 添加系统通知公告
     *
     * @author xuyuxiang
     * @date 2020/6/28 17:24
     */
    @Permission
    @PostMapping("/sysNotice/add")
    @BusinessLog(title = "系统通知公告_增加", opType = LogAnnotionOpTypeEnum.ADD)
    public ResponseData add(@RequestBody @Validated(SysNoticeParam.add.class) SysNoticeParam sysNoticeParam) {
        sysNoticeService.add(sysNoticeParam);
        return new SuccessResponseData();
    }

    /**
     * 删除系统通知公告
     *
     * @author xuyuxiang
     * @date 2020/6/28 17:25
     */
    @Permission
    @PostMapping("/sysNotice/delete")
    @BusinessLog(title = "系统通知公告_删除", opType = LogAnnotionOpTypeEnum.DELETE)
    public ResponseData delete(@RequestBody @Validated(SysNoticeParam.delete.class) SysNoticeParam sysNoticeParam) {
        sysNoticeService.delete(sysNoticeParam);
        return new SuccessResponseData();
    }

    /**
     * 编辑系统通知公告
     *
     * @author xuyuxiang
     * @date 2020/6/28 17:25
     */
    @Permission
    @PostMapping("/sysNotice/edit")
    @BusinessLog(title = "系统通知公告_编辑", opType = LogAnnotionOpTypeEnum.EDIT)
    public ResponseData edit(@RequestBody @Validated(SysNoticeParam.edit.class) SysNoticeParam sysNoticeParam) {
        sysNoticeService.edit(sysNoticeParam);
        return new SuccessResponseData();
    }

    /**
     * 查看系统通知公告
     *
     * @author xuyuxiang
     * @date 2020/6/28 17:25
     */
    @Permission
    @GetMapping("/sysNotice/detail")
    @BusinessLog(title = "系统通知公告_查看", opType = LogAnnotionOpTypeEnum.DETAIL)
    public ResponseData detail(@Validated(SysNoticeParam.detail.class) SysNoticeParam sysNoticeParam) {
        return new SuccessResponseData(sysNoticeService.detail(sysNoticeParam));
    }

    /**
     * 修改状态
     *
     * @author xuyuxiang
     * @date 2020/6/29 9:44
     */
    @Permission
    @PostMapping("/sysNotice/changeStatus")
    @BusinessLog(title = "系统通知公告_修改状态", opType = LogAnnotionOpTypeEnum.CHANGE_STATUS)
    public ResponseData changeStatus(@RequestBody @Validated(SysNoticeParam.changeStatus.class) SysNoticeParam sysNoticeParam) {
        sysNoticeService.changeStatus(sysNoticeParam);
        return new SuccessResponseData();
    }
}
