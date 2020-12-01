package com.cn.xiaonuo.core.email.modular.exception;

import lombok.Getter;

/**
 * 邮件发送异常
 *
 * @author xuyuxiang
 * @date 2018-07-06-下午3:00
 */
@Getter
public class MailSendException extends RuntimeException {

    private final Integer code;

    private final String errorMessage;

    public MailSendException(Integer code, String errorMessage) {
        super(errorMessage);
        this.code = code;
        this.errorMessage = errorMessage;
    }

}
