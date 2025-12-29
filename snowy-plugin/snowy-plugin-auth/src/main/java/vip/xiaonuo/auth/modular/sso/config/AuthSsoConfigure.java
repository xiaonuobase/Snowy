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
package vip.xiaonuo.auth.modular.sso.config;

import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import vip.xiaonuo.common.prop.CommonProperties;

/**
 * 单点登录客户端配置
 *
 * @author xuyuxiang
 * @date 2021/10/9 14:24
 **/
@Configuration
public class AuthSsoConfigure {

    @Resource
    private CommonProperties commonProperties;

    /**
     * 配置SSO客户端相关参数
     */
    @Autowired
    private void configSsoClient(SaSsoClientConfig saSsoClientConfig) {
        saSsoClientConfig.setCurrSsoLogin(commonProperties.getBackendUrl() + "/auth/sso/b/doLoginByTicket");
        saSsoClientConfig.setCurrSsoLogoutCall(commonProperties.getBackendUrl() + "/auth/sso/b/logoutCall");
        saSsoClientConfig.setIsHttp(true);
        saSsoClientConfig.setIsSlo(true);
        saSsoClientConfig.setRegLogoutCall(true);
        saSsoClientConfig.setIsCheckSign(true);
    }
}
