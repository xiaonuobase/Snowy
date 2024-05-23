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
package vip.xiaonuo.dev.modular.log.util;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.util.*;
import vip.xiaonuo.dev.modular.log.entity.DevLog;
import vip.xiaonuo.dev.modular.log.enums.DevLogCategoryEnum;
import vip.xiaonuo.dev.modular.log.enums.DevLogExeStatusEnum;
import vip.xiaonuo.dev.modular.log.service.DevLogService;

/**
 * 日志工具类
 *
 * @author xuyuxiang
 * @date 2022/9/2 15:26
 */
public class DevLogUtil {

    private static final DevLogService devLogService = SpringUtil.getBean(DevLogService.class);

    /**
     * 记录操作日志
     *
     * @author xuyuxiang
     * @date 2022/9/2 15:31
     */
    public static void executeOperationLog(CommonLog commonLog, String userName, JoinPoint joinPoint, String resultJson) {
        HttpServletRequest request = CommonServletUtil.getRequest();
        String requestURI = request.getRequestURI();
        String method = request.getRequestURI();
        DevLog devLog = genBasOpLog();
        ThreadUtil.execute(() -> {
            devLog.setCategory(DevLogCategoryEnum.OPERATE.getValue());
            devLog.setName(commonLog.value());
            devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
            devLog.setClassName(joinPoint.getTarget().getClass().getName());
            devLog.setMethodName(joinPoint.getSignature().getName());
            devLog.setReqMethod(method);
            devLog.setReqUrl(requestURI);
            devLog.setParamJson(CommonJoinPointUtil.getArgsJsonString(joinPoint));
            devLog.setResultJson(resultJson);
            devLog.setOpTime(DateTime.now());
            devLog.setOpUser(userName);
            creatLogSignValue(devLog);
            devLogService.save(devLog);
        });
    }

    /**
     * 记录异常日志
     *
     * @author xuyuxiang
     * @date 2022/9/2 15:31
     */
    public static void executeExceptionLog(CommonLog commonLog, String userName, JoinPoint joinPoint, Exception exception) {
        HttpServletRequest request = CommonServletUtil.getRequest();
        String requestURI = request.getRequestURI();
        String method = request.getRequestURI();
        DevLog devLog = genBasOpLog();
        ThreadUtil.execute(() -> {
            devLog.setCategory(DevLogCategoryEnum.EXCEPTION.getValue());
            devLog.setName(commonLog.value());
            devLog.setExeStatus(DevLogExeStatusEnum.FAIL.getValue());
            devLog.setExeMessage(ExceptionUtil.stacktraceToString(exception, Integer.MAX_VALUE));
            devLog.setClassName(joinPoint.getTarget().getClass().getName());
            devLog.setMethodName(joinPoint.getSignature().getName());
            devLog.setReqMethod(method);
            devLog.setReqUrl(requestURI);
            devLog.setParamJson(CommonJoinPointUtil.getArgsJsonString(joinPoint));
            devLog.setOpTime(DateTime.now());
            devLog.setOpUser(userName);
            creatLogSignValue(devLog);
            devLogService.save(devLog);
        });
    }

    /**
     * 记录登录日志
     *
     * @author xuyuxiang
     * @date 2022/9/2 16:08
     */
    public static void executeLoginLog(String userName) {
        DevLog devLog = genBasOpLog();
        ThreadUtil.execute(() -> {
            devLog.setCategory(DevLogCategoryEnum.LOGIN.getValue());
            devLog.setName("用户登录");
            devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
            devLog.setOpTime(DateTime.now());
            devLog.setOpUser(userName);
            creatLogSignValue(devLog);
            devLogService.save(devLog);
        });
    }

    /**
     * 记录登出日志
     *
     * @author xuyuxiang
     * @date 2022/9/2 16:08
     */
    public static void executeLogoutLog(String userName) {
        DevLog devLog = genBasOpLog();
        ThreadUtil.execute(() -> {
            devLog.setCategory(DevLogCategoryEnum.LOGOUT.getValue());
            devLog.setName("用户登出");
            devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
            devLog.setOpTime(DateTime.now());
            devLog.setOpUser(userName);
            creatLogSignValue(devLog);
            devLogService.save(devLog);
        });
    }

    /**
     * 构建基础操作日志
     *
     * @author xuyuxiang
     * @date 2020/3/19 14:44
     */
    private static DevLog genBasOpLog() {
        HttpServletRequest request = CommonServletUtil.getRequest();
        String ip = CommonIpAddressUtil.getIp(request);
        String loginId;
        try {
            loginId = StpUtil.getLoginIdAsString();
            if (ObjectUtil.isEmpty(loginId)) {
                loginId = "-1";
            }
        } catch (Exception e) {
            loginId = "-1";
        }
        DevLog devLog = new DevLog();
        devLog.setOpIp(CommonIpAddressUtil.getIp(request));
        devLog.setOpAddress(CommonIpAddressUtil.getCityInfo(ip));
        devLog.setOpBrowser(CommonUaUtil.getBrowser(request));
        devLog.setOpOs(CommonUaUtil.getOs(request));
        devLog.setCreateUser(loginId);
        return devLog;
    }

    /**
     * 构建日志完整性保护签名数据
     */
    private static void creatLogSignValue (DevLog devLog) {
        String logStr = devLog.toString().replaceAll(" +","");
        devLog.setSignData(CommonCryptogramUtil.doSignature(logStr));
    }
}
