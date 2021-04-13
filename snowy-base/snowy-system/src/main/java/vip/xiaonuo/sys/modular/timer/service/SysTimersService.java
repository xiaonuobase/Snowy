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
package vip.xiaonuo.sys.modular.timer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.modular.timer.entity.SysTimers;
import vip.xiaonuo.sys.modular.timer.param.SysTimersParam;

import java.util.List;

/**
 * 定时任务 服务类
 *
 * @author yubaoshan
 * @date 2020/6/30 18:26
 */
public interface SysTimersService extends IService<SysTimers> {

    /**
     * 分页查询定时任务
     *
     * @param sysTimersParam 查询参数
     * @return 查询分页结果
     * @author yubaoshan
     * @date 2020/6/30 18:26
     */
    PageResult<SysTimers> page(SysTimersParam sysTimersParam);

    /**
     * 查询所有定时任务
     *
     * @param sysTimersParam 查询参数
     * @return 定时任务列表
     * @author yubaoshan
     * @date 2020/6/30 18:26
     */
    List<SysTimers> list(SysTimersParam sysTimersParam);

    /**
     * 添加定时任务
     *
     * @param sysTimersParam 添加参数
     * @author yubaoshan
     * @date 2020/6/30 18:26
     */
    void add(SysTimersParam sysTimersParam);

    /**
     * 删除定时任务
     *
     * @param sysTimersParam 删除参数
     * @author yubaoshan
     * @date 2020/6/30 18:26
     */
    void delete(SysTimersParam sysTimersParam);

    /**
     * 编辑定时任务
     *
     * @param sysTimersParam 编辑参数
     * @author yubaoshan
     * @date 2020/6/30 18:26
     */
    void edit(SysTimersParam sysTimersParam);

    /**
     * 查看详情定时任务
     *
     * @param sysTimersParam 查看参数
     * @return 定时任务
     * @author yubaoshan
     * @date 2020/6/30 18:26
     */
    SysTimers detail(SysTimersParam sysTimersParam);

    /**
     * 启动任务
     *
     * @param sysTimersParam 启动参数
     * @author yubaoshan
     * @date 2020/7/1 14:36
     */
    void start(SysTimersParam sysTimersParam);

    /**
     * 停止任务
     *
     * @param sysTimersParam 停止参数
     * @author yubaoshan
     * @date 2020/7/1 14:36
     */
    void stop(SysTimersParam sysTimersParam);

    /**
     * 获取所有可执行的任务列表
     *
     * @return TimerTaskRunner的所有子类名称集合
     * @author yubaoshan
     * @date 2020/7/1 14:36
     */
    List<String> getActionClasses();

}
