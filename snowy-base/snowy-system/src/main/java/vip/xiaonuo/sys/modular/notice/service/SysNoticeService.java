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
package vip.xiaonuo.sys.modular.notice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.modular.notice.entity.SysNotice;
import vip.xiaonuo.sys.modular.notice.param.SysNoticeParam;
import vip.xiaonuo.sys.modular.notice.result.SysNoticeDetailResult;
import vip.xiaonuo.sys.modular.notice.result.SysNoticeReceiveResult;

/**
 * 系统通知公告service接口
 *
 * @author xuyuxiang
 * @date 2020/6/28 17:16
 */
public interface SysNoticeService extends IService<SysNotice> {

    /**
     * 查询系统通知公告
     *
     * @param sysNoticeParam 查询参数
     * @return 查询分页结果
     * @author xuyuxiang
     * @date 2020/6/28 17:16
     */
    PageResult<SysNotice> page(SysNoticeParam sysNoticeParam);

    /**
     * 添加系统通知公告
     *
     * @param sysNoticeParam 添加参数
     * @author xuyuxiang
     * @date 2020/6/28 17:21
     */
    void add(SysNoticeParam sysNoticeParam);

    /**
     * 删除系统通知公告
     *
     * @param sysNoticeParam 删除参数
     * @author xuyuxiang
     * @date 2020/6/28 17:22
     */
    void delete(SysNoticeParam sysNoticeParam);

    /**
     * 编辑系统通知公告
     *
     * @param sysNoticeParam 编辑参数
     * @author xuyuxiang
     * @date 2020/6/28 17:22
     */
    void edit(SysNoticeParam sysNoticeParam);

    /**
     * 查看系统通知公告
     *
     * @param sysNoticeParam 查看参数
     * @return 通知公告详情结果
     * @author xuyuxiang
     * @date 2020/6/28 17:22
     */
    SysNoticeDetailResult detail(SysNoticeParam sysNoticeParam);

    /**
     * 修改状态
     *
     * @param sysNoticeParam 修改参数
     * @author xuyuxiang
     * @date 2020/6/29 9:45
     */
    void changeStatus(SysNoticeParam sysNoticeParam);

    /**
     * 查询当前登陆用户已收通知公告
     *
     * @param sysNoticeParam 查询参数
     * @return 查询分页结果
     * @author xuyuxiang
     * @date 2020/6/29 12:01
     */
    PageResult<SysNoticeReceiveResult> received(SysNoticeParam sysNoticeParam);
}
