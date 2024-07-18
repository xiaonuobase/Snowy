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
package vip.xiaonuo.sys.modular.index.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import vip.xiaonuo.sys.modular.index.param.*;
import vip.xiaonuo.sys.modular.index.result.*;

import java.util.List;

/**
 * 系统首页Service接口
 *
 * @author xuyuxiang
 * @date 2022/9/2 10:45
 */
public interface SysIndexService {

    /**
     * 添加当前用户日程
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:13
     */
    void addSchedule(SysIndexScheduleAddParam sysIndexScheduleAddParam);

    /**
     * 删除日程
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:32
     */
    void deleteSchedule(List<SysIndexScheduleIdParam> sysIndexScheduleIdParamList);

    /**
     * 获取当前用户日程列表
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:23
     */
    List<SysIndexScheduleListResult> scheduleList(SysIndexScheduleListParam sysIndexScheduleListParam);

    /**
     * 获取当前用户站内信列表
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:36
     */
    List<SysIndexMessageListResult> messageList(SysIndexMessageListParam sysIndexMessageListParam);

    /**
     * 获取站内信详情
     *
     * @author xuyuxiang
     * @date 2022/9/2 11:44
     */
    SysIndexMessageDetailResult messageDetail(SysIndexMessageIdParam sysIndexMessageIdParam);

    /**
     * 站内信全部标记已读
     *
     * @author diantu
     * @date 2023/7/10
     */
    void allMessageMarkRead();

    /**
     * 获取当前用户访问日志列表
     *
     * @author xuyuxiang
     * @date 2022/9/4 15:11
     */
    List<SysIndexVisLogListResult> visLogList();

    /**
     * 获取当前用户操作日志列表
     *
     * @author xuyuxiang
     * @date 2022/9/4 15:11
     */
    List<SysIndexOpLogListResult> opLogList();

    /**
     * 创建连接
     *
     * @author diantu
     * @date 2023/7/10
     **/
    SseEmitter createSseConnect(String clientId);

    /**
     * 获取基础系统业务数据
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    SysBizDataCountResult getBizDataCount();

    /**
     * 获取基础系统业务数据
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    SysOpDataCountResult getOpDataCount();

    /**
     * 获取基础系统业务数据
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    SysToolDataCountResult getToolDataCount();
}
