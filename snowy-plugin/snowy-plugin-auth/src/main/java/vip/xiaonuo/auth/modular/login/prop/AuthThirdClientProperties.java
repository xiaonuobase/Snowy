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
package vip.xiaonuo.auth.modular.login.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 第三方系统Token交换配置属性（用于iframe嵌入免登）
 *
 * 对接规范：甲方提供一个用户信息接口，GET请求，Header传 Authorization: Bearer {accessToken}
 * 必须返回如下JSON格式：
 * {
 *     "code": 200,
 *     "data": {
 *         "account": "zhangsan",
 *         "email": "zhangsan@xxx.com",
 *         "phone": "13800138000"
 *     }
 * }
 *
 * @author yubaoshan
 * @date 2026/2/11
 **/
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "snowy.third-auth")
public class AuthThirdClientProperties {

    /**
     * Token交换登录总开关。
     * null用于兼容旧配置；显式配置false时关闭。
     */
    private Boolean enabled;

    /**
     * 兼容旧版的单个第三方用户信息接口地址。
     * 新配置请使用 providers，后续可移除此字段。
     */
    private String userInfoUrl;

    /** 未传provider时使用的默认第三方标识 */
    private String defaultProvider;

    /** 多个第三方系统配置，key为第三方标识 */
    private Map<String, Provider> providers = new LinkedHashMap<>();

    /** 单个第三方系统配置 */
    @Getter
    @Setter
    public static class Provider {

        /** 第三方用户信息接口地址 */
        private String userInfoUrl;
    }
}
