package com.cn.xiaonuo.core.sms.modular.aliyun.msign.impl;

import cn.hutool.log.Log;
import com.cn.xiaonuo.core.sms.modular.aliyun.msign.MultiSignManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取缓存的map中的签名
 *
 * @author xuyuxiang
 * @date 2018-09-21-上午10:49
 */
public class MapBasedMultiSignManager implements MultiSignManager {

    private static final Log log = Log.get();

    private static final int CLEAR_COUNT = 1000;

    private final Map<String, String> cacheMap = new ConcurrentHashMap<>();

    @Override
    public String getSign(String phone, String signName) {

        //先清除map
        clearMap();

        //分割签名数组
        String[] signNames = signName.split(",");

        //获取上次发送的时候用的哪个签名，这次换一个
        Object lastSignName = cacheMap.get(phone);
        if (lastSignName == null) {
            cacheMap.put(phone, signNames[0]);
            log.info("发送短信，签名为：" + signNames[0] + ",电话为：" + phone);
            return signNames[0];
        } else {
            for (String name : signNames) {
                if (!name.equals(lastSignName)) {
                    cacheMap.put(phone, name);
                    log.info("发送短信，签名为：" + name + ",电话为：" + phone);
                    return name;
                }
            }
            cacheMap.put(phone, signNames[0]);
            log.info("发送短信，签名为：" + signNames[0] + ",电话为：" + phone);
            return signNames[0];
        }
    }

    /**
     * 每隔一段时间清除下map
     *
     * @author xuyuxiang
     * @date 2018/9/21 上午10:57
     */
    private void clearMap() {
        if (cacheMap.size() >= CLEAR_COUNT) {
            cacheMap.clear();
        }
    }

}
