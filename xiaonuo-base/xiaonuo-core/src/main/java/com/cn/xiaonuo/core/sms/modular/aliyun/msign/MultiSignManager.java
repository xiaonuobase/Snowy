package com.cn.xiaonuo.core.sms.modular.aliyun.msign;

/**
 * 多个签名的缓存管理，为了打破一个签名发送次数的限制
 *
 * @author xuyuxiang
 * @date 2018-09-21-上午10:47
 */
public interface MultiSignManager {

    /**
     * 获取签名
     *
     * @param phone    电话
     * @param signName 发送短信用的签名，是一个以逗号隔开的字符串
     * @return 签名
     * @author xuyuxiang
     * @date 2018/9/21 上午10:51
     */
    String getSign(String phone, String signName);

}
