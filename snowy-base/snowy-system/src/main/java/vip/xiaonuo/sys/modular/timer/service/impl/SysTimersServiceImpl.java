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
package vip.xiaonuo.sys.modular.timer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.core.timer.TimerTaskRunner;
import vip.xiaonuo.sys.modular.timer.entity.SysTimers;
import vip.xiaonuo.sys.modular.timer.enums.TimerJobStatusEnum;
import vip.xiaonuo.sys.modular.timer.enums.exp.SysTimersExceptionEnum;
import vip.xiaonuo.sys.modular.timer.mapper.SysTimersMapper;
import vip.xiaonuo.sys.modular.timer.param.SysTimersParam;
import vip.xiaonuo.sys.modular.timer.service.SysTimersService;
import vip.xiaonuo.sys.modular.timer.service.TimerExeService;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定时任务 服务实现类
 *
 * @author yubaoshan
 * @date 2020/6/30 18:26
 */
@Service
public class SysTimersServiceImpl extends ServiceImpl<SysTimersMapper, SysTimers> implements SysTimersService {

    @Resource
    private TimerExeService timerExeService;

    @Override
    public PageResult<SysTimers> page(SysTimersParam sysTimersParam) {

        // 构造条件
        LambdaQueryWrapper<SysTimers> queryWrapper = new LambdaQueryWrapper<>();

        if (ObjectUtil.isNotNull(sysTimersParam)) {

            // 拼接查询条件-任务名称
            if (ObjectUtil.isNotEmpty(sysTimersParam.getTimerName())) {
                queryWrapper.like(SysTimers::getTimerName, sysTimersParam.getTimerName());
            }

            // 拼接查询条件-状态（字典 1运行  2停止）
            if (ObjectUtil.isNotEmpty(sysTimersParam.getJobStatus())) {
                queryWrapper.eq(SysTimers::getJobStatus, sysTimersParam.getJobStatus());
            }
        }

        // 查询分页结果
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public List<SysTimers> list(SysTimersParam sysTimersParam) {

        // 构造条件
        LambdaQueryWrapper<SysTimers> queryWrapper = new LambdaQueryWrapper<>();

        if (ObjectUtil.isNotNull(sysTimersParam)) {

            // 拼接查询条件-任务名称
            if (ObjectUtil.isNotEmpty(sysTimersParam.getTimerName())) {
                queryWrapper.like(SysTimers::getTimerName, sysTimersParam.getTimerName());
            }

            // 拼接查询条件-状态（字典 1运行  2停止）
            if (ObjectUtil.isNotEmpty(sysTimersParam.getJobStatus())) {
                queryWrapper.eq(SysTimers::getJobStatus, sysTimersParam.getJobStatus());
            }
        }

        return this.list(queryWrapper);
    }

    @Override
    public void add(SysTimersParam sysTimersParam) {

        // 将dto转为实体
        SysTimers sysTimers = new SysTimers();
        BeanUtil.copyProperties(sysTimersParam, sysTimers);

        // 设置为停止状态，点击启动时启动任务
        sysTimers.setJobStatus(TimerJobStatusEnum.STOP.getCode());

        this.save(sysTimers);
    }

    @Override
    public void delete(SysTimersParam sysTimersParam) {

        // 先停止id为参数id的定时器
        CronUtil.remove(String.valueOf(sysTimersParam.getId()));

        this.removeById(sysTimersParam.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysTimersParam sysTimersParam) {

        // 更新库中记录
        SysTimers oldTimer = this.querySysTimers(sysTimersParam);
        // 查看被编辑的任务的状态
        Integer jobStatus = oldTimer.getJobStatus();
        BeanUtil.copyProperties(sysTimersParam, oldTimer);
        oldTimer.setJobStatus(jobStatus);
        this.updateById(oldTimer);

        // 如果任务正在运行，则停掉这个任务，重新运行任务
        if (jobStatus.equals(TimerJobStatusEnum.RUNNING.getCode())) {
            CronUtil.remove(String.valueOf(oldTimer.getId()));
            timerExeService.startTimer(
                    String.valueOf(sysTimersParam.getId()),
                    sysTimersParam.getCron(),
                    sysTimersParam.getActionClass());
        }
    }

    @Override
    public SysTimers detail(SysTimersParam sysTimersParam) {
        return this.querySysTimers(sysTimersParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void start(SysTimersParam sysTimersParam) {

        // 更新库中的状态
        LambdaUpdateWrapper<SysTimers> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysTimers::getJobStatus, TimerJobStatusEnum.RUNNING.getCode())
                .eq(SysTimers::getId, sysTimersParam.getId());
        this.update(wrapper);

        // 添加定时任务调度
        SysTimers sysTimers = this.querySysTimers(sysTimersParam);
        timerExeService.startTimer(String.valueOf(sysTimers.getId()), sysTimers.getCron(), sysTimers.getActionClass());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void stop(SysTimersParam sysTimersParam) {

        // 更新库中的状态
        LambdaUpdateWrapper<SysTimers> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(SysTimers::getJobStatus, TimerJobStatusEnum.STOP.getCode())
                .eq(SysTimers::getId, sysTimersParam.getId());
        this.update(wrapper);

        // 关闭定时任务调度
        SysTimers sysTimers = this.querySysTimers(sysTimersParam);
        timerExeService.stopTimer(String.valueOf(sysTimers.getId()));
    }

    @Override
    public List<String> getActionClasses() {
        Map<String, TimerTaskRunner> timerTaskRunnerMap = SpringUtil.getBeansOfType(TimerTaskRunner.class);
        if (ObjectUtil.isNotEmpty(timerTaskRunnerMap)) {
            Collection<TimerTaskRunner> values = timerTaskRunnerMap.values();
            return values.stream().map(i -> i.getClass().getName()).collect(Collectors.toList());
        } else {
            return CollectionUtil.newArrayList();
        }
    }

    /**
     * 获取定时任务
     *
     * @author yubaoshan
     * @date 2020/6/30 18:26
     */
    private SysTimers querySysTimers(SysTimersParam sysTimersParam) {
        SysTimers sysTimers = this.getById(sysTimersParam.getId());
        if (ObjectUtil.isEmpty(sysTimers)) {
            throw new ServiceException(SysTimersExceptionEnum.NOT_EXISTED);
        }
        return sysTimers;
    }

}
