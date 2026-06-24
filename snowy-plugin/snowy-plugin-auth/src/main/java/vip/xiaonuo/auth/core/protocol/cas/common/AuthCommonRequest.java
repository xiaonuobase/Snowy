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
package vip.xiaonuo.auth.core.protocol.cas.common;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import me.zhyd.oauth.enums.AuthResponseStatus;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.utils.AuthStateUtils;
import vip.xiaonuo.auth.core.enums.AuthCasVersionEnum;
import vip.xiaonuo.auth.core.protocol.base.AuthBaseRequest;
import vip.xiaonuo.auth.core.protocol.cas.AuthCasBaseJson;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;

import java.util.Map;

import static vip.xiaonuo.auth.core.protocol.base.AuthBaseClient.CONFIG_CACHE_KEY;

/**
 * CAS认证源通用请求
 *
 * @author xuyuxiang
 * @date 2025/1/24 15:09
 **/
public record AuthCommonRequest(AuthCasBaseJson authCasBaseJson) implements AuthBaseRequest {

    @Override
    public String authorize(String clientType) {
        // 获取登录地址
        String casServerLoginUrl = authCasBaseJson.getCasServerLoginUrl();
        // 获取serviceUrl
        String serviceUrl = authCasBaseJson.getServiceUrl();
        // 获取状态
        String state = AuthStateUtils.createState();
        // 获取缓存操作类
        CommonCacheOperator commonCacheOperator = SpringUtil.getBean(CommonCacheOperator.class);
        // 放入缓存
        commonCacheOperator.put(CONFIG_CACHE_KEY + state, JSONUtil.createObj().set("clientType", clientType), 300);
        // 追加state
        serviceUrl = SaFoxUtil.joinParam(serviceUrl, SaOAuth2Consts.Param.state, state);
        // 编码serviceUrl
        serviceUrl = URLUtil.encodeAll(serviceUrl);
        // 追加service
        casServerLoginUrl = SaFoxUtil.joinParam(casServerLoginUrl, "service", serviceUrl);
        // 返回授权地址
        return casServerLoginUrl;
    }

    @Override
    public AuthResponse<AuthUser> login() {
        AuthResponse<AuthUser> authResponse = new AuthResponse<>();
        Map<String, String> paramMap = SaHolder.getRequest().getParamMap();
        String ticket = paramMap.get("ticket");
        if(ObjectUtil.isEmpty(ticket)) {
            throw new CommonException("ticket不能为空");
        }
        // CAS的state放在service的参数中
        String state = paramMap.get(SaOAuth2Consts.Param.state);
        if(ObjectUtil.isEmpty(ticket)) {
            throw new CommonException("state不能为空");
        }
        // 获取CAS认证服务验证地址
        String casServerValidateUrl = authCasBaseJson.getCasServerValidateUrl();
        // 获取服务地址
        String serviceUrl = authCasBaseJson.getServiceUrl();
        // 服务地址追加state
        serviceUrl = SaFoxUtil.joinParam(serviceUrl, SaOAuth2Consts.Param.state, state);
        // 编码serviceUrl
        serviceUrl = URLUtil.encodeAll(serviceUrl);
        // 验证地址追加ticket和service
        casServerValidateUrl = SaFoxUtil.joinParam(casServerValidateUrl, "ticket", ticket);
        casServerValidateUrl = SaFoxUtil.joinParam(casServerValidateUrl, "service", serviceUrl);
        // 获取CAS认证服务协议版本
        String casServerProtocolVersion = authCasBaseJson.getCasServerProtocolVersion();
        String responseUuid;
        if(AuthCasVersionEnum.V1.getValue().equals(casServerProtocolVersion)) {
            String responseText = HttpUtil.get(casServerValidateUrl);
            if(ObjectUtil.isNotEmpty(responseText)) {
                if(responseText.startsWith("yes")) {
                    responseUuid = responseText.substring(4);
                } else {
                    throw new CommonException("ticket验证结果失败");
                }
            } else {
                throw new CommonException("结果解析错误：{}", responseText);
            }
        } else {
            String responseXml = HttpUtil.get(casServerValidateUrl);
            Map<String, Object> stringObjectMap;
            try {
                stringObjectMap = XmlUtil.xmlToMap(responseXml);
            } catch (Exception e) {
                throw new CommonException("结果解析错误：{}", e.getMessage());
            }
            if(ObjectUtil.isNotEmpty(stringObjectMap)) {
                if(stringObjectMap.containsKey("cas:authenticationSuccess")) {
                    Object authenticationSuccess = BeanUtil.beanToMap(stringObjectMap).get("cas:authenticationSuccess");
                    responseUuid = Convert.toStr(BeanUtil.beanToMap(authenticationSuccess).get(authCasBaseJson.getSourceProperty()));
                } else if(stringObjectMap.containsKey("cas:authenticationFailure")) {
                    Object authenticationFailure = BeanUtil.beanToMap(stringObjectMap).get("cas:authenticationFailure");
                    throw new CommonException("ticket验证结果失败：{}", Convert.toStr(authenticationFailure));
                } else {
                    throw new CommonException("结果解析错误：{}", responseXml);
                }
            } else {
                throw new CommonException("结果解析错误：{}", responseXml);
            }
        }
        authResponse.setCode(AuthResponseStatus.SUCCESS.getCode());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(authCasBaseJson.getSourceProperty(), responseUuid);
        authResponse.setData(AuthUser.builder().rawUserInfo(jsonObject).uuid(responseUuid).build());
        return authResponse;
    }
}
