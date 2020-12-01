package com.cn.xiaonuo.core.file.common.exp;

import lombok.Getter;

/**
 * 文件操作业务异常
 *
 * @author xuyuxiang
 * @date 2020-05-23-2:42 下午
 */
@Getter
public class FileServiceException extends RuntimeException {

    public FileServiceException(String message) {
        super(message);
    }

}
