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
package vip.xiaonuo.client.core.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import vip.xiaonuo.dev.api.DevConfigApi;

/**
 * 系统相关邮件格式化工具类
 *
 * @author xuyuxiang
 * @date 2025/3/21 19:07
 **/
public class ClientEmailFormatUtl {

    /** 系统名称 */
    private static final String SNOWY_SYS_NAME_KEY = "SNOWY_SYS_NAME";

    /**
     * 格式化邮件内容
     *
     * @author xuyuxiang
     * @date 2025/3/21 19:08
     **/
    public static String format(String content, JSONObject paramMap) {
        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
        // 获取系统名称
        String sysName = devConfigApi.getValueByKey(SNOWY_SYS_NAME_KEY);
        // 系统名称
        paramMap.set("sysName", sysName);
        // 当前时间
        paramMap.set("sysNowTime", DateUtil.now());
        return StrUtil.format(content, paramMap);
    }
}
