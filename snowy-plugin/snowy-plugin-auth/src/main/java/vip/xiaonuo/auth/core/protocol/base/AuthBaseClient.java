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
package vip.xiaonuo.auth.core.protocol.base;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.json.JSONObject;
import lombok.Getter;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import vip.xiaonuo.auth.core.util.AuthPhoneUtil;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 认证源客户端抽象类
 *
 * @author xuyuxiang
 * @date 2024/11/19 15:01
 **/
@Getter
public abstract class AuthBaseClient<T extends AuthBaseJson> {

    // 前端页面地址
    public static final String FRONT_CALLBACK_URL = "/iam/auth/callback";

    /** 缓存前缀 */
    public static final String CONFIG_CACHE_KEY = "auth-source-state:";

    private final JSONObject authSource;

    private final String authPlatform;

    public AuthBaseClient(JSONObject authSource, String authPlatform) {
        this.authSource = authSource;
        this.authPlatform = authPlatform;
    }

    /**
     * 获取基础配置
     *
     * @author xuyuxiang
     * @date 2024/11/19 15:17
     **/
    public abstract T getAuthBaseJson();

    /**
     * 验证认证源
     *
     * @author xuyuxiang
     * @date 2024/12/30 10:52
     **/
    public abstract void valid();

    /**
     * 获取认证url
     *
     * @author xuyuxiang
     * @date 2025/2/6 16:28
     **/
    public abstract String getAuthorizeUrl(String clientType);

    /**
     * 执行登录
     *
     * @author xuyuxiang
     * @date 2025/2/11 14:07
     **/
    public abstract AuthResponse<AuthUser> doLogin();

    /**
     * 处理响应结果
     *
     * @author xuyuxiang
     * @date 2025/2/11 16:39
     **/
    public AuthResponse<AuthUser> handleAuthResponse(AuthResponse<AuthUser> authResponse) {

        AuthBaseJson authBaseJson = this.getAuthBaseJson();
        if(authResponse.ok()) {
            String uuid;
            String sourceProperty = authBaseJson.getSourceProperty();
            AuthUser authUser = authResponse.getData();
            authUser.setSource(this.authPlatform);
            com.alibaba.fastjson.JSONObject rawUserInfo = authUser.getRawUserInfo();
            if (ObjectUtil.isEmpty(rawUserInfo)) {
                throw new CommonException("第三方原始用户信息rawUserInfo为空");
            }
            Object rawUserInfoData = rawUserInfo.get("data");
            if(ObjectUtil.isNotEmpty(rawUserInfoData)) {
                rawUserInfo = com.alibaba.fastjson.JSONObject.parseObject(rawUserInfoData.toString());
            }
            Object sourcePropertyValue = rawUserInfo.get(sourceProperty);
            if (ObjectUtil.isEmpty(sourcePropertyValue)) {
                throw new CommonException("第三方原始用户信息rawUserInfo中映射源属性{}为空", sourceProperty);
            }
            uuid = Convert.toStr(sourcePropertyValue);
            if(PhoneUtil.isMobile(uuid)) {
                uuid = AuthPhoneUtil.trimTelNum(uuid);
            }
            authUser.setUuid(uuid);
            return authResponse;
        } else {
            throw new CommonException(authResponse.getMsg());
        }
    }
}
