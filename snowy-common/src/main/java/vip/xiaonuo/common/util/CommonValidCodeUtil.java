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
package vip.xiaonuo.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;

import java.util.Locale;

/**
 * 短信与邮件验证码发送频率限制工具类
 *
 * @author yubaoshan
 * @date 2026/11/09 15:00
 */
public class CommonValidCodeUtil {

    /** 同一手机号或邮箱的发送间隔（秒） */
    private static final long SEND_INTERVAL_SECONDS = 60;

    /** 同一手机号或邮箱每天最多发送次数 */
    private static final long SEND_TIMES_PER_DAY = 10;

    /** 同一IP每小时最多发送次数 */
    private static final long SEND_TIMES_PER_HOUR_FOR_IP = 30;

    private static final String SEND_INTERVAL_KEY_PREFIX = "valid-code-send-interval:";

    private static final String SEND_DAY_COUNT_KEY_PREFIX = "valid-code-send-day-count:";

    private static final String SEND_IP_COUNT_KEY_PREFIX = "valid-code-send-ip-count:";

    /**
     * 校验验证码发送频率，超出限制直接抛出异常，校验通过即计入一次发送
     *
     * @author yubaoshan
     * @date 2026/11/09 15:00
     */
    public static void checkSendFrequency(String phoneOrEmail) {
        CommonCacheOperator commonCacheOperator = SpringUtil.getBean(CommonCacheOperator.class);
        // 统一大小写并去掉手机号的 +86、86、0 前缀，避免变换写法绕过按目标的限制
        String target = StrUtil.trimToEmpty(phoneOrEmail).toLowerCase(Locale.ROOT)
                .replaceFirst("^(?:\\+?86|0)(?=1\\d{10}$)", "");
        // 同一手机号或邮箱发送间隔内只能发送一次
        if (!commonCacheOperator.putIfAbsent(SEND_INTERVAL_KEY_PREFIX + target, target, SEND_INTERVAL_SECONDS)) {
            throw new CommonException("验证码发送过于频繁，请{}秒后再试", SEND_INTERVAL_SECONDS);
        }
        // 同一手机号或邮箱每天发送次数上限
        if (commonCacheOperator.incrementCounter(SEND_DAY_COUNT_KEY_PREFIX + target, 24 * 60 * 60) > SEND_TIMES_PER_DAY) {
            throw new CommonException("该账号今日验证码发送次数已达上限，请明天再试");
        }
        // 同一IP每小时发送次数上限，非Web请求时不做IP维度限制
        if (CommonServletUtil.isWeb()) {
            String ip = CommonIpAddressUtil.getIp(CommonServletUtil.getRequest());
            if (commonCacheOperator.incrementCounter(SEND_IP_COUNT_KEY_PREFIX + ip, 60 * 60) > SEND_TIMES_PER_HOUR_FOR_IP) {
                throw new CommonException("当前网络验证码发送次数过多，请稍后再试");
            }
        }
    }
}
