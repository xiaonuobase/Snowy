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
package vip.xiaonuo.im.core.config;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.antherd.smcrypto.sm2.Sm2;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.im.core.Interceptor.WebSocketInterceptor;
import vip.xiaonuo.im.core.handler.ImWebSocketHandler;

import java.util.List;
import java.util.Optional;

/**
 * web套接字配置
 *
 * @author liuchunming
 * @date 2024/5/21 17:35
 */
@EnableWebSocket
@Configuration
@EnableConfigurationProperties(WebSocketConfig.class)
//@ConditionalOnProperty(prefix = "snowy.websocket", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketConfig webSocketConfig;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        if (ObjectUtil.isEmpty(webSocketConfig) || ObjectUtil.isEmpty(webSocketConfig.getPath())) {
            webSocketConfig.setPath(List.of("/ws/im"));
        }
        ALLATORIxDEMO();
        registry.addHandler(new ImWebSocketHandler(), webSocketConfig.getPath().toArray(String[]::new))
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins(Optional.ofNullable(webSocketConfig.getAllowedOrigins()).orElse("*"));
    }

    /**
     * 验签方法
     *
     * @author xuyuxiang
     * @date 2024/9/20 13:39
     **/
    public void ALLATORIxDEMO() {
        String authCode = SpringUtil.getApplicationContext().getEnvironment().getProperty("snowy.config.im.auth.code");
        if(ObjectUtil.isEmpty(authCode)) {
            throw new CommonException("snowy.config.im.auth.code配置为空");
        }
        boolean verifyResult;
        try {
            // 解密
            String signData = Base64.decodeStr(authCode);
            // 最后一个【-】出现的索引
            int lastIndex = signData.lastIndexOf(StrUtil.DASHED);
            // 原文（最后一个【-】出现之前的数据）
            String dataValue = signData.substring(0, lastIndex);
            // 获取签名（最后一个【-】出现之后的数据）
            String signValue = signData.substring(lastIndex + 1);
            // 执行验签
            verifyResult = Sm2.doVerifySignature(dataValue, signValue, "04aad74fd8f24e945334ab92bd12152e5654c84e2a74fb9250b9cae01a32a9218"
                    + "39b8cc8502ed4a90157ed39c8e9b06f3a3fd867cc95da4356ceb0aa345535c772");
        } catch (Exception e) {
            throw new CommonException("snowy.config.im.auth.code配置错误");
        }
        if(!verifyResult) {
            throw new CommonException("snowy.config.im.auth.code配置错误");
        }
    }
}
