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
package vip.xiaonuo.dev.modular.job.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dev.modular.job.entity.DevJob;
import vip.xiaonuo.dev.modular.job.param.*;

import java.util.List;

/**
 * 定时任务Service接口
 *
 * @author xuyuxiang
 * @date 2022/8/5 10:46
 **/
public interface DevJobService extends IService<DevJob> {

    /**
     * 获取定时任务分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<DevJob> page(DevJobPageParam devJobPageParam);

    /**
     * 获取定时任务列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<DevJob> list(DevJobListParam devJobListParam);

    /**
     * 添加定时任务
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(DevJobAddParam devJobAddParam);

    /**
     * 编辑定时任务
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(DevJobEditParam devJobEditParam);

    /**
     * 删除定时任务
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<DevJobIdParam> devJobIdParamList);

    /**
     * 获取定时任务详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevJob detail(DevJobIdParam devJobIdParam);

    /**
     * 获取定时任务详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevJob queryEntity(String id);

    /**
     * 停止定时任务
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:20
     **/
    void stopJob(DevJobIdParam devJobIdParam);

    /**
     * 运行定时任务
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:21
     **/
    void runJob(DevJobIdParam devJobIdParam);

    /**
     * 立即运行定时任务
     *
     * @author xuyuxiang
     * @date 2023/3/3 15:50
     **/
    void runJobNow(DevJobIdParam devJobIdParam);

    /**
     * 获取定时任务类
     *
     * @author xuyuxiang
     * @date 2022/8/22 9:35
     **/
    List<String> getActionClass();
}
