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
package vip.xiaonuo.auth.core.exception;

import lombok.Getter;

/**
 * 屏幕锁定异常，当前会话处于锁屏状态时访问业务接口抛出
 *
 * @author xuyuxiang
 * @date 2026/8/6 10:20
 **/
@Getter
public class AuthLockedException extends RuntimeException {

    /** 屏幕锁定的状态码，前端据此弹出解锁界面，与二级认证的4011区分 */
    public static final int LOCKED_CODE = 4012;

    /** 屏幕锁定的提示语 */
    public static final String LOCKED_MESSAGE = "屏幕已锁定，请先解锁";

    private final Integer code;

    private final String msg;

    public AuthLockedException() {
        super(LOCKED_MESSAGE);
        this.code = LOCKED_CODE;
        this.msg = LOCKED_MESSAGE;
    }
}
