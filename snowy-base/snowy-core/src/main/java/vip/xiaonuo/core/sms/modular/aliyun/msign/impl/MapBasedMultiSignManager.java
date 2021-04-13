/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.core.sms.modular.aliyun.msign.impl;

import cn.hutool.log.Log;
import vip.xiaonuo.core.sms.modular.aliyun.msign.MultiSignManager;

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
            log.info(">>> 发送短信，签名为：" + signNames[0] + ",电话为：" + phone);
            return signNames[0];
        } else {
            for (String name : signNames) {
                if (!name.equals(lastSignName)) {
                    cacheMap.put(phone, name);
                    log.info(">>> 发送短信，签名为：" + name + ",电话为：" + phone);
                    return name;
                }
            }
            cacheMap.put(phone, signNames[0]);
            log.info(">>> 发送短信，签名为：" + signNames[0] + ",电话为：" + phone);
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
