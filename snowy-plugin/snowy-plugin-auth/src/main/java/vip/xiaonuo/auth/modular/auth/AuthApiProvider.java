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
package vip.xiaonuo.auth.modular.auth;


import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.api.AuthApi;
import vip.xiaonuo.auth.core.enums.SaClientTypeEnum;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.auth.modular.login.enums.AuthDeviceTypeEnum;
import vip.xiaonuo.auth.modular.login.enums.AuthStrategyWhenNoUserWithPhoneOrEmailEnum;
import vip.xiaonuo.auth.modular.login.param.AuthAccountPasswordLoginParam;
import vip.xiaonuo.auth.modular.login.service.AuthService;
import vip.xiaonuo.auth.modular.third.service.AuthThirdService;

import java.util.List;

/**
 * 认证鉴权API实现类
 *
 * @author yubaoshan
 * @date 2024/7/18 17:35
 **/
@Service
public class AuthApiProvider implements AuthApi {

    @Resource
    private AuthThirdService authThirdService;

    @Resource
    private AuthService authService;

    @Override
    public JSONObject getUserSessionCount() {
        JSONObject resultObj = new JSONObject();
        List<JSONObject> sessionListB = StpUtil.searchSessionId("", -1, -1, true).stream().map(sessionId -> {
            JSONObject jsonObject = JSONUtil.createObj();
            String userId = StrUtil.split(sessionId, StrUtil.COLON).get(3);
            SaSession saSession = StpUtil.getSessionByLoginId(userId, false);
            int tokenCount = saSession.getTerminalList().size();
            long createTime = saSession.getCreateTime();
            jsonObject.set("userId", userId);
            jsonObject.set("tokenCount", tokenCount);
            jsonObject.set("createTime", DateTime.of(createTime));
            return jsonObject;
        }).toList();

        List<JSONObject> sessionListC = StpClientUtil.searchSessionId("", -1, -1, true).stream().map(sessionId -> {
            JSONObject jsonObject = JSONUtil.createObj();
            String userId = StrUtil.split(sessionId, StrUtil.COLON).get(3);
            SaSession saSession = StpClientUtil.getSessionByLoginId(userId, false);
            int tokenCount = saSession.getTerminalList().size();
            long createTime = saSession.getCreateTime();
            jsonObject.set("userId", userId);
            jsonObject.set("tokenCount", tokenCount);
            jsonObject.set("createTime", DateTime.of(createTime));
            return jsonObject;
        }).toList();
        resultObj.set("backUserSessionCount" ,sessionListB.size());
        resultObj.set("clientUserSessionCount" ,sessionListC.size());
        return resultObj;
    }

    @Override
    public Long getThirdUserCount() {
        return authThirdService.count();
    }

    @Override
    public boolean getDefaultCaptchaOpenForB() {
        return authService.getDefaultCaptchaOpenForB();
    }

    @Override
    public boolean getDefaultCaptchaOpenForC() {
        return authService.getDefaultCaptchaOpenForC();
    }

    @Override
    public void validValidCode(String phoneOrEmail, String validCode, String validCodeReqNo) {
        authService.validValidCode(phoneOrEmail, validCode, validCodeReqNo);
    }

    @Override
    public String doLoginForB(String account, String password, String validCode, String validCodeReqNo) {
        AuthAccountPasswordLoginParam authAccountPasswordLoginParam = new AuthAccountPasswordLoginParam();
        authAccountPasswordLoginParam.setAccount(account);
        authAccountPasswordLoginParam.setPassword(password);
        authAccountPasswordLoginParam.setValidCode(validCode);
        authAccountPasswordLoginParam.setValidCodeReqNo(validCodeReqNo);
        return authService.doLogin(authAccountPasswordLoginParam, SaClientTypeEnum.B.getValue());
    }

    @Override
    public String doLoginForC(String account, String password, String validCode, String validCodeReqNo) {
        AuthAccountPasswordLoginParam authAccountPasswordLoginParam = new AuthAccountPasswordLoginParam();
        authAccountPasswordLoginParam.setAccount(account);
        authAccountPasswordLoginParam.setPassword(password);
        authAccountPasswordLoginParam.setValidCode(validCode);
        authAccountPasswordLoginParam.setValidCodeReqNo(validCodeReqNo);
        return authService.doLogin(authAccountPasswordLoginParam, SaClientTypeEnum.C.getValue());
    }

    @Override
    public String doLoginByIdForB(String userId, String device) {
        return authService.doLoginById(userId, ObjectUtil.isNotEmpty(device)?device:AuthDeviceTypeEnum.PC.getValue(), SaClientTypeEnum.B.getValue());
    }

    @Override
    public String doLoginByIdForC(String userId, String device) {
        return authService.doLoginById(userId, ObjectUtil.isNotEmpty(device)?device:AuthDeviceTypeEnum.PC.getValue(), SaClientTypeEnum.C.getValue());
    }

    @Override
    public String doLoginByAccountForB(String account, String device) {
        return authService.doLoginByAccount(account, ObjectUtil.isNotEmpty(device)?device:AuthDeviceTypeEnum.PC.getValue(), SaClientTypeEnum.B.getValue());
    }

    @Override
    public String doLoginByAccountForC(String account, String device) {
        return authService.doLoginByAccount(account, ObjectUtil.isNotEmpty(device)?device:AuthDeviceTypeEnum.PC.getValue(), SaClientTypeEnum.C.getValue());
    }

    @Override
    public String doLoginByPhoneForB(String phone, String device) {
        return authService.doLoginByPhone(phone, ObjectUtil.isNotEmpty(device)?device:AuthDeviceTypeEnum.PC.getValue(),
                SaClientTypeEnum.B.getValue(), AuthStrategyWhenNoUserWithPhoneOrEmailEnum.AUTO_CREATE_USER.getValue());
    }

    @Override
    public String doLoginByPhoneForC(String phone, String device) {
        return authService.doLoginByPhone(phone, ObjectUtil.isNotEmpty(device)?device:AuthDeviceTypeEnum.PC.getValue(),
                SaClientTypeEnum.C.getValue(), AuthStrategyWhenNoUserWithPhoneOrEmailEnum.AUTO_CREATE_USER.getValue());
    }

    @Override
    public String doLoginByEmailForB(String email, String device) {
        return authService.doLoginByEmail(email, ObjectUtil.isNotEmpty(device)?device:AuthDeviceTypeEnum.PC.getValue(),
                SaClientTypeEnum.B.getValue(), AuthStrategyWhenNoUserWithPhoneOrEmailEnum.AUTO_CREATE_USER.getValue());
    }

    @Override
    public String doLoginByEmailForC(String email, String device) {
        return authService.doLoginByEmail(email, ObjectUtil.isNotEmpty(device)?device:AuthDeviceTypeEnum.PC.getValue(),
                SaClientTypeEnum.C.getValue(), AuthStrategyWhenNoUserWithPhoneOrEmailEnum.AUTO_CREATE_USER.getValue());
    }
}
