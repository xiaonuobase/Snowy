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
package vip.xiaonuo.auth.core.util;

import cn.dev33.satoken.exception.*;
import cn.hutool.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.util.CommonServletUtil;

@Slf4j
public class AuthExceptionUtil {

    /**
     * 根据错误类型获取对应的CommonResult（只处理SaToken相关异常）
     *
     * @author xuyuxiang
     * @date 2021/10/11 15:52
     **/
    public static CommonResult<String> getCommonResult(Exception e) {
        CommonResult<String> commonResult;
        if (e instanceof NotLoginException notLoginException) {

            // 如果是未登录异常 401
            commonResult = CommonResult.get(HttpStatus.HTTP_UNAUTHORIZED, notLoginException.getMessage(), null);
        } else if (e instanceof NotRoleException notRoleException) {

            // 如果是角色异常 403
            commonResult = CommonResult.get(HttpStatus.HTTP_FORBIDDEN, "无此角色：" + notRoleException.getRole() +
                    "，接口地址：" + CommonServletUtil.getRequest().getServletPath(), null);
        } else if (e instanceof NotPermissionException notPermissionException) {

            // 如果是权限异常 403
            commonResult = CommonResult.get(HttpStatus.HTTP_FORBIDDEN, "无此权限：" + notPermissionException.getPermission(), null);
        } else if (e instanceof DisableServiceException disableServiceException) {

            // 如果是被封禁异常 403
            commonResult = CommonResult.get(HttpStatus.HTTP_FORBIDDEN, "账号被封禁：" + disableServiceException.getDisableTime() + "秒后解封", null);
        } else if (e instanceof SaTokenException saTokenException) {

            // 如果是SaToken异常 直接返回
            commonResult = CommonResult.error(saTokenException.getMessage());
        } else {
            // 未知异常才打印
            log.error(">>> 服务器未知异常，请求地址：{}，具体信息：", CommonServletUtil.getRequest().getRequestURL(), e);
            // 未知异常返回服务器异常（此处不可能执行进入，因为本方法处理的一定是SaToken的异常，此处仅为安全性考虑）
            commonResult = CommonResult.error();
        }
        return commonResult;
    }
}
