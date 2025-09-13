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
package vip.xiaonuo.auth.modular.sso.service.impl;

import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.core.enums.SaClientTypeEnum;
import vip.xiaonuo.auth.modular.login.enums.AuthDeviceTypeEnum;
import vip.xiaonuo.auth.modular.login.enums.AuthStrategyWhenNoUserWithPhoneOrEmailEnum;
import vip.xiaonuo.auth.modular.login.service.AuthService;
import vip.xiaonuo.auth.modular.sso.param.AuthGetSsoAuthUrlParam;
import vip.xiaonuo.auth.modular.sso.param.AuthSsoTicketLoginParam;
import vip.xiaonuo.auth.modular.sso.service.AuthSsoService;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 单点登录Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/8/30 9:21
 **/
@Service
public class AuthSsoServiceImpl implements AuthSsoService {

    @Resource
    private AuthService authService;

    @Override
    public String getSsoAuthUrl(AuthGetSsoAuthUrlParam authGetSsoAuthUrlParam, String type) {
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            return SaSsoClientUtil.buildServerAuthUrl(authGetSsoAuthUrlParam.getRedirectUrl(), "");
        } else {
            throw new CommonException("不支持的客户端类型：{}", type);
        }
    }

    @Override
    public String doLoginByTicket(AuthSsoTicketLoginParam authSsoTicketLoginParam, String type) {
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            SaCheckTicketResult saCheckTicketResult = SaSsoClientProcessor.instance.checkTicket(authSsoTicketLoginParam.getTicket());
            // 获取用户信息
            SaResult result = saCheckTicketResult.result;
            Object account = result.get("account");
            Object phone = result.get("phone");
            Object email = result.get("email");
            if(ObjectUtil.isNotEmpty(account)) {
                return authService.doLoginByAccount(Convert.toStr(account),
                        ObjectUtil.isEmpty(authSsoTicketLoginParam.getDevice()) ? AuthDeviceTypeEnum.PC.getValue() : authSsoTicketLoginParam.getDevice(),
                        SaClientTypeEnum.B.getValue());
            } else if(ObjectUtil.isNotEmpty(phone)) {
                return authService.doLoginByPhone(Convert.toStr(phone),
                        ObjectUtil.isEmpty(authSsoTicketLoginParam.getDevice()) ? AuthDeviceTypeEnum.PC.getValue() : authSsoTicketLoginParam.getDevice(),
                        SaClientTypeEnum.B.getValue(), AuthStrategyWhenNoUserWithPhoneOrEmailEnum.NOT_ALLOW_LOGIN.getValue());
            } else if(ObjectUtil.isNotEmpty(email)) {
                return authService.doLoginByEmail(Convert.toStr(email),
                        ObjectUtil.isEmpty(authSsoTicketLoginParam.getDevice()) ? AuthDeviceTypeEnum.PC.getValue() : authSsoTicketLoginParam.getDevice(),
                        SaClientTypeEnum.B.getValue(), AuthStrategyWhenNoUserWithPhoneOrEmailEnum.NOT_ALLOW_LOGIN.getValue());
            } else {
                throw new CommonException("登录失败，根据账号、手机号、邮箱未匹配到用户");
            }
        } else {
            throw new CommonException("不支持的客户端类型：{}", type);
        }
    }

    @Override
    public Object logoutCall(String type) {
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            return SaSsoClientProcessor.instance.ssoLogoutCall();
        } else {
            throw new CommonException("不支持的客户端类型：{}", type);
        }
    }

    @Override
    public Object pushClient(String type) {
        if(SaClientTypeEnum.B.getValue().equals(type)) {
            return SaSsoClientProcessor.instance.ssoPushC();
        } else {
            throw new CommonException("不支持的客户端类型：{}", type);
        }
    }
}
