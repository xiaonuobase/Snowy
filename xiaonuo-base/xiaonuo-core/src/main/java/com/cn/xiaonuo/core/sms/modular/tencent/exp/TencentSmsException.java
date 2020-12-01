package com.cn.xiaonuo.core.sms.modular.tencent.exp;

import lombok.Getter;

/**
 * 短信发送异常
 *
 * @author xuyuxiang
 * @date 2018-07-06-下午3:00
 */
@Getter
public class TencentSmsException extends RuntimeException {

    private final String code;

    private final String errorMessage;

    public TencentSmsException(String code, String errorMessage) {
        super(errorMessage);
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
