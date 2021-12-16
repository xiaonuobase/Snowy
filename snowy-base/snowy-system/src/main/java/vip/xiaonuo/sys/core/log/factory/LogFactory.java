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
package vip.xiaonuo.sys.core.log.factory;

import cn.hutool.core.date.DateTime;
import org.aspectj.lang.JoinPoint;
import vip.xiaonuo.core.annotion.BusinessLog;
import vip.xiaonuo.core.consts.SymbolConstant;
import vip.xiaonuo.core.context.constant.ConstantContextHolder;
import vip.xiaonuo.core.util.CryptogramUtil;
import vip.xiaonuo.core.util.JoinPointUtil;
import vip.xiaonuo.sys.core.enums.LogSuccessStatusEnum;
import vip.xiaonuo.sys.core.enums.VisLogTypeEnum;
import vip.xiaonuo.sys.modular.log.entity.SysOpLog;
import vip.xiaonuo.sys.modular.log.entity.SysVisLog;

import java.util.Arrays;

/**
 * 日志对象创建工厂
 *
 * @author xuyuxiang
 * @date 2020/3/12 14:31
 */
public class LogFactory {

    /**
     * 创建登录日志
     *
     * @author xuyuxiang
     * @date 2020/3/12 16:09
     */
    static void createSysLoginLog(SysVisLog sysVisLog, String account, String successCode, String failMessage) {
        sysVisLog.setName(VisLogTypeEnum.LOGIN.getMessage());
        sysVisLog.setSuccess(successCode);

        sysVisLog.setVisType(VisLogTypeEnum.LOGIN.getCode());
        sysVisLog.setVisTime(DateTime.now());
        sysVisLog.setAccount(account);

        if (LogSuccessStatusEnum.SUCCESS.getCode().equals(successCode)) {
            sysVisLog.setMessage(VisLogTypeEnum.LOGIN.getMessage() + LogSuccessStatusEnum.SUCCESS.getMessage());
        }
        if (LogSuccessStatusEnum.FAIL.getCode().equals(successCode)) {
            sysVisLog.setMessage(VisLogTypeEnum.LOGIN.getMessage() +
                    LogSuccessStatusEnum.FAIL.getMessage() + SymbolConstant.COLON + failMessage);
        }
        if (ConstantContextHolder.getCryptogramConfigs().getVisLogEnc()) {
            SysVisLogSign(sysVisLog);
        }
    }

    /**
     * 创建登出日志
     *
     * @author xuyuxiang
     * @date 2020/3/12 16:09
     */
    static void createSysExitLog(SysVisLog sysVisLog, String account) {
        sysVisLog.setName(VisLogTypeEnum.EXIT.getMessage());
        sysVisLog.setSuccess(LogSuccessStatusEnum.SUCCESS.getCode());
        sysVisLog.setMessage(VisLogTypeEnum.EXIT.getMessage() + LogSuccessStatusEnum.SUCCESS.getMessage());
        sysVisLog.setVisType(VisLogTypeEnum.EXIT.getCode());
        sysVisLog.setVisTime(DateTime.now());
        sysVisLog.setAccount(account);
        if (ConstantContextHolder.getCryptogramConfigs().getVisLogEnc()) {
            SysVisLogSign(sysVisLog);
        }
    }

    /**
     * 创建操作日志
     *
     * @author xuyuxiang
     * @date 2020/3/12 16:09
     */
    static void createSysOperationLog(SysOpLog sysOpLog, String account, BusinessLog businessLog, JoinPoint joinPoint, String result) {
        fillCommonSysOpLog(sysOpLog, account, businessLog, joinPoint);
        sysOpLog.setSuccess(LogSuccessStatusEnum.SUCCESS.getCode());
        sysOpLog.setResult(result);
        sysOpLog.setMessage(LogSuccessStatusEnum.SUCCESS.getMessage());
        if (ConstantContextHolder.getCryptogramConfigs().getOpLogEnc()) {
            SysOpLogSign(sysOpLog);
        }
    }

    /**
     * 创建异常日志
     *
     * @author xuyuxiang
     * @date 2020/3/16 17:18
     */
    static void createSysExceptionLog(SysOpLog sysOpLog, String account, BusinessLog businessLog, JoinPoint joinPoint, Exception exception) {
        fillCommonSysOpLog(sysOpLog, account, businessLog, joinPoint);
        sysOpLog.setSuccess(LogSuccessStatusEnum.FAIL.getCode());
        sysOpLog.setMessage(Arrays.toString(exception.getStackTrace()));
        if (ConstantContextHolder.getCryptogramConfigs().getOpLogEnc()) {
            SysOpLogSign(sysOpLog);
        }
    }

    /**
     * 生成通用操作日志字段
     *
     * @author xuyuxiang
     * @date 2020/3/16 17:20
     */
    private static void fillCommonSysOpLog(SysOpLog sysOpLog, String account, BusinessLog businessLog, JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();

        String methodName = joinPoint.getSignature().getName();

        String param = JoinPointUtil.getArgsJsonString(joinPoint);

        sysOpLog.setName(businessLog.title());
        sysOpLog.setOpType(businessLog.opType().ordinal());
        sysOpLog.setClassName(className);
        sysOpLog.setMethodName(methodName);
        sysOpLog.setParam(param);
        sysOpLog.setOpTime(DateTime.now());
        sysOpLog.setAccount(account);
    }

    /**
     * 构建基础访问日志
     *
     * @author xuyuxiang
     * @date 2020/3/19 14:36
     */
    public static SysVisLog genBaseSysVisLog(String ip, String location, String browser, String os) {
        SysVisLog sysVisLog = new SysVisLog();
        sysVisLog.setIp(ip);
        sysVisLog.setLocation(location);
        sysVisLog.setBrowser(browser);
        sysVisLog.setOs(os);
        return sysVisLog;
    }

    /**
     * 构建基础操作日志
     *
     * @author xuyuxiang
     * @date 2020/3/19 14:36
     */
    public static SysOpLog genBaseSysOpLog(String ip, String location, String browser, String os, String url, String method) {
        SysOpLog sysOpLog = new SysOpLog();
        sysOpLog.setIp(ip);
        sysOpLog.setLocation(location);
        sysOpLog.setBrowser(browser);
        sysOpLog.setOs(os);
        sysOpLog.setUrl(url);
        sysOpLog.setReqMethod(method);
        return sysOpLog;
    }

    /**
     * 构建登录登出日志完整性保护（数据签名）
     */
    private static SysVisLog SysVisLogSign (SysVisLog sysVisLog) {
        String sysVisLogStr = sysVisLog.toString().replaceAll(" +","");
        sysVisLog.setSignValue(CryptogramUtil.doSignature(sysVisLogStr));
        return sysVisLog;
    }

    /**
     * 构建操作日志完整性保护（摘要）
     */
    private static SysOpLog SysOpLogSign (SysOpLog sysOpLog) {
        String sysVisLogStr = sysOpLog.toString();
        sysOpLog.setSignValue(CryptogramUtil.doHashValue(sysVisLogStr));
        return sysOpLog;
    }

}
