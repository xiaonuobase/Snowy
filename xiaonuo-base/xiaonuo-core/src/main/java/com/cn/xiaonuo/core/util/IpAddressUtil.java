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

XiaoNuo采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改XiaoNuo源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
6.若您的项目无法满足以上几点，可申请商业授权，获取XiaoNuo商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cn.xiaonuo.core.util;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSONPath;
import com.cn.xiaonuo.core.consts.CommonConstant;
import com.cn.xiaonuo.core.consts.SymbolConstant;
import com.cn.xiaonuo.core.context.constant.ConstantContextHolder;
import com.cn.xiaonuo.core.context.requestno.RequestNoContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 根据ip地址定位工具类，使用高德地地图定位api
 *
 * @author xuyuxiang
 * @date 2020/3/16 11:25
 */
public class IpAddressUtil {

    private static final Log log = Log.get();

    private static final String LOCAL_IP = "127.0.0.1";

    private static final String LOCAL_REMOTE_HOST = "0:0:0:0:0:0:0:1";

    /**
     * 获取客户端ip
     *
     * @author xuyuxiang
     * @date 2020/3/19 9:32
     */
    public static String getIp(HttpServletRequest request) {
        if (ObjectUtil.isEmpty(request)) {
            return LOCAL_IP;
        } else {
            String remoteHost = ServletUtil.getClientIP(request);
            return LOCAL_REMOTE_HOST.equals(remoteHost) ? LOCAL_IP : remoteHost;
        }
    }

    /**
     * 根据ip地址定位
     *
     * @author xuyuxiang
     * @date 2020/3/16 15:17
     */
    @SuppressWarnings("unchecked")
    public static String getAddress(HttpServletRequest request) {
        String resultJson = SymbolConstant.DASH;

        String ip = getIp(request);

        //如果是本地ip或局域网ip，则直接不查询
        if (ObjectUtil.isEmpty(ip) || NetUtil.isInnerIP(ip)) {
            return resultJson;
        }

        try {
            //获取阿里云定位api接口
            String api = ConstantContextHolder.getIpGeoApi();
            //获取阿里云定位appCode
            String appCode = ConstantContextHolder.getIpGeoAppCode();
            if (ObjectUtil.isAllNotEmpty(api, appCode)) {
                String path = "$['data']['country','region','city','isp']";
                String appCodeSymbol = "APPCODE";
                HttpRequest http = HttpUtil.createGet(String.format(api, ip));
                http.header(CommonConstant.AUTHORIZATION, appCodeSymbol + " " + appCode);
                resultJson = http.timeout(3000).execute().body();
                resultJson = String.join("", (List<String>) JSONPath.read(resultJson, path));
            }
        } catch (Exception e) {
            resultJson = SymbolConstant.DASH;
            log.error(">>> 根据ip定位异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
        }
        return resultJson;
    }

}
