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
package vip.xiaonuo.dev.modular.log.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dev.modular.log.entity.DevLog;
import vip.xiaonuo.dev.modular.log.param.DevLogDeleteParam;
import vip.xiaonuo.dev.modular.log.param.DevLogIdParam;
import vip.xiaonuo.dev.modular.log.param.DevLogPageParam;
import vip.xiaonuo.dev.modular.log.result.DevLogOpBarChartDataResult;
import vip.xiaonuo.dev.modular.log.result.DevLogOpPieChartDataResult;
import vip.xiaonuo.dev.modular.log.result.DevLogVisLineChartDataResult;
import vip.xiaonuo.dev.modular.log.result.DevLogVisPieChartDataResult;

import java.util.List;

/**
 * 日志Service接口
 *
 * @author xuyuxiang
 * @date 2022/9/2 15:04
 */
public interface DevLogService extends IService<DevLog> {

    /**
     * 获取日志分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<DevLog> page(DevLogPageParam devLogPageParam);

    /**
     * 获取单条日志详情
     */
    DevLog detail(DevLogIdParam devLogIdParam);

    /**
     * 清空日志
     *
     * @author xuyuxiang
     * @date 2022/9/6 13:17
     */
    void delete(DevLogDeleteParam devLogDeleteParam);

    /**
     * 获取访问日志折线图数据
     *
     * @author xuyuxiang
     * @date 2022/9/4 21:18
     */
    List<DevLogVisLineChartDataResult> visLogLineChartData();

    /**
     * 获取访问日志饼状图数据
     *
     * @author xuyuxiang
     * @date 2022/9/4 21:18
     */
    List<DevLogVisPieChartDataResult> visLogPieChartData();

    /**
     * 获取操作日志柱状图数据
     *
     * @author xuyuxiang
     * @date 2022/9/4 21:18
     */
    List<DevLogOpBarChartDataResult> opLogBarChartData();

    /**
     * 获取操作日志饼状图数据
     *
     * @author xuyuxiang
     * @date 2022/9/4 21:18
     */
    List<DevLogOpPieChartDataResult> opLogPieChartData();
}
