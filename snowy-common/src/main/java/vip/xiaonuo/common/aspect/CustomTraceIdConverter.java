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
package vip.xiaonuo.common.aspect;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.stereotype.Component;
import vip.xiaonuo.common.util.CommonTraceIdUtil;

/**
 * 自定义traceId转换器
 *
 * @author dongxiayu
 * @date 2025/1/9 15:36
 */
@Component
public class CustomTraceIdConverter extends ClassicConverter {

    // 使用常量避免重复创建字符串
    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";

    // 缓存StringBuilder以减少对象创建
    private static final ThreadLocal<StringBuilder> bufferHolder =
            ThreadLocal.withInitial(() -> new StringBuilder(64));

    @Override
    public String convert(ILoggingEvent event) {
        StringBuilder buffer = bufferHolder.get();
        buffer.setLength(0);
        String traceId = CommonTraceIdUtil.getTraceId();

        buffer.append(LEFT_BRACKET);
        if (traceId != null && !traceId.isEmpty()) {
            buffer.append(traceId);
        } else {
            buffer.append(event.getThreadName());
        }
        buffer.append(RIGHT_BRACKET);
        return buffer.toString();
    }
}