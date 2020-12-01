package com.cn.xiaonuo.core.sms;

import java.util.Map;

/**
 * 短信发送服务
 *
 * @author xuyuxiang
 * @date 2018-07-06-下午2:14
 */
public interface SmsSender {

    /**
     * 发送短信
     * <p>
     * 如果是腾讯云，params要用LinkedHashMap，保证顺序
     *
     * @param phone        电话号码
     * @param templateCode 模板号码
     * @param params       模板里参数的集合
     * @author xuyuxiang
     * @date 2018/7/6 下午2:32
     */
    void sendSms(String phone, String templateCode, Map<String, Object> params);

}
