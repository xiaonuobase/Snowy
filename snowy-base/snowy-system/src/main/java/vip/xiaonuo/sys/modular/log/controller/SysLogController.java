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
package vip.xiaonuo.sys.modular.log.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.core.annotion.BusinessLog;
import vip.xiaonuo.core.annotion.Permission;
import vip.xiaonuo.core.enums.LogAnnotionOpTypeEnum;
import vip.xiaonuo.core.pojo.response.ResponseData;
import vip.xiaonuo.core.pojo.response.SuccessResponseData;
import vip.xiaonuo.sys.modular.log.param.SysOpLogParam;
import vip.xiaonuo.sys.modular.log.param.SysVisLogParam;
import vip.xiaonuo.sys.modular.log.service.SysOpLogService;
import vip.xiaonuo.sys.modular.log.service.SysVisLogService;

import javax.annotation.Resource;

/**
 * 系统日志控制器
 *
 * @author xuyuxiang
 * @date 2020/3/19 21:14
 */
@RestController
public class SysLogController {

    @Resource
    private SysVisLogService sysVisLogService;

    @Resource
    private SysOpLogService sysOpLogService;

    /**
     * 查询访问日志
     *
     * @author xuyuxiang
     * @date 2020/3/20 18:52
     */
    @Permission
    @GetMapping("/sysVisLog/page")
    public ResponseData visLogPage(SysVisLogParam visLogParam) {
        return new SuccessResponseData(sysVisLogService.page(visLogParam));
    }

    /**
     * 查询操作日志
     *
     * @author xuyuxiang
     * @date 2020/3/20 18:52
     */
    @Permission
    @GetMapping("/sysOpLog/page")
    public ResponseData opLogPage(SysOpLogParam sysOpLogParam) {
        return new SuccessResponseData(sysOpLogService.page(sysOpLogParam));
    }

    /**
     * 清空访问日志
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @PostMapping("/sysVisLog/delete")
    @BusinessLog(title = "访问日志_清空", opType = LogAnnotionOpTypeEnum.CLEAN)
    public ResponseData visLogDelete() {
        sysVisLogService.delete();
        return new SuccessResponseData();
    }

    /**
     * 清空操作日志
     *
     * @author xuyuxiang
     * @date 2020/3/23 16:28
     */
    @Permission
    @PostMapping("/sysOpLog/delete")
    @BusinessLog(title = "操作日志_清空", opType = LogAnnotionOpTypeEnum.CLEAN)
    public ResponseData opLogDelete() {
        sysOpLogService.delete();
        return new SuccessResponseData();
    }

    /**
     * 导出系统操作日志
     *
     * @author yubaoshan
     * @date 2020/5/30 17:55
     */
    @Permission
    @GetMapping("/sysOpLog/export")
    @BusinessLog(title = "操作日志_导出", opType = LogAnnotionOpTypeEnum.EXPORT)
    public void export(SysOpLogParam sysOpLogParam) {
        sysOpLogService.export(sysOpLogParam);
    }

    /**
     * 导出系统访问日志
     *
     * @author yubaoshan
     * @date 2020/5/30 17:55
     */
    @Permission
    @GetMapping("/sysVisLog/export")
    @BusinessLog(title = "访问日志_导出", opType = LogAnnotionOpTypeEnum.EXPORT)
    public void export(SysVisLogParam sysVisLogParam) {
        sysVisLogService.export(sysVisLogParam);
    }

}
