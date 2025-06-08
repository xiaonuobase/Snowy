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
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

/**
 * TraceId工具类
 *
 * @author dongxiayu
 * @date 2025/1/9 17:07
 */
public class CommonTraceIdUtil {

    public static final String TRACE_ID_STRING = "traceId";

    private static final InheritableThreadLocal<String> TRACE_ID = new InheritableThreadLocal<>();

    public static String generateTraceId(HttpServletRequest request) {
        String header = request.getHeader(TRACE_ID_STRING);
        if (header != null) {
            return header;
        }
        return UUID.randomUUID().toString().replace(StrUtil.DASHED, StrUtil.EMPTY);
    }

    public static String getTraceId() {
        return TRACE_ID.get();
    }

    public static String getTraceId(HttpServletRequest request) {
        String traceId = getTraceId();
        if (traceId == null) {
            traceId = generateTraceId(request);
            setTraceId(traceId);
        }
        return traceId;
    }

    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static void clear() {
        TRACE_ID.remove();
    }
}
