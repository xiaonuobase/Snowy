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

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import vip.xiaonuo.common.exception.CommonException;

/**
 * 通用邮件工具类
 *
 * @author xuyuxiang
 * @date 2022/8/25 15:10
 **/
public class CommonEmailUtil {

    /**
     * 判断是否邮箱
     *
     * @author xuyuxiang
     * @date 2022/8/15 13:32
     **/
    public static boolean isEmail(String email) {
        return  Validator.isEmail(email);
    }

    /**
     * 校验邮箱格式
     *
     * @author xuyuxiang
     * @date 2022/8/15 13:32
     **/
    public static void validEmail(String emails) {
        StrUtil.split(emails, StrUtil.COMMA).forEach(email -> {
            if(!isEmail(email)) {
                throw new CommonException("邮件地址：{}格式错误", email);
            }
        });
    }
}
