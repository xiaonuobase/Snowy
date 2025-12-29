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
package vip.xiaonuo.auth.api;

import cn.hutool.json.JSONObject;

/**
 * 认证鉴权API
 *
 * @author yubaoshan
 * @date 2024/7/18 17:35
 */
public interface AuthApi {

    /**
     * 获取基础登录业务数据，b端在线用户，c端在线用户
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    JSONObject getUserSessionCount();

    /**
     * 获取三方用户总量
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    Long getThirdUserCount();

    /**
     * 获取B端验证码是否开启
     *
     * @author xuyuxiang
     * @date 2021/12/28 14:46
     **/
    boolean getDefaultCaptchaOpenForB();

    /**
     * 获取C端验证码是否开启
     *
     * @author xuyuxiang
     * @date 2021/12/28 14:46
     **/
    boolean getDefaultCaptchaOpenForC();

    /**
     * 校验验证码
     *
     * @author xuyuxiang
     * @date 2021/12/28 14:46
     **/
    void validValidCode(String phoneOrEmail, String validCode, String validCodeReqNo);

    /**
     * B端账号密码登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginForB(String account, String password, String validCode, String validCodeReqNo);

    /**
     * C端账号密码登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginForC(String account, String password, String validCode, String validCodeReqNo);

    /**
     * B端用户id登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginByIdForB(String userId, String device);

    /**
     * C端用户id登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginByIdForC(String userId, String device);

    /**
     * B端账号登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginByAccountForB(String account, String device);

    /**
     * C端账号登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginByAccountForC(String account, String device);

    /**
     * B端手机号登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginByPhoneForB(String phone, String device);

    /**
     * C端手机号登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginByPhoneForC(String phone, String device);

    /**
     * B端邮箱登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginByEmailForB(String email, String device);

    /**
     * C端邮箱登录
     *
     * @author yubaoshan
     * @date 2024/7/18 17:35
     */
    String doLoginByEmailForC(String email, String device);
}
