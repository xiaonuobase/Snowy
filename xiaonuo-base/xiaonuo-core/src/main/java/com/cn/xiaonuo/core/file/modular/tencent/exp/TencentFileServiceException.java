package com.cn.xiaonuo.core.file.modular.tencent.exp;

import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import lombok.Getter;

/**
 * 腾讯文件操作异常
 *
 * @author xuyuxiang
 * @date 2020-05-23-2:42 下午
 */
@Getter
public class TencentFileServiceException extends RuntimeException {

    /**
     * 客户端异常
     * <p>
     * 是由于客户端原因导致无法和服务端完成正常的交互而导致的失败，如客户端无法连接到服务端，无法解析服务端返回的数据
     */
    private CosClientException cosClientException;

    /**
     * 服务端异常
     * <p>
     * 用于指交互正常完成，但是操作失败的场景
     * <p>
     * 例如客户端访问一个不存在 Bucket，删除一个不存在的文件，没有权限进行某个操作， 服务端故障异常等
     */
    private CosServiceException cosServiceException;

    public TencentFileServiceException(String message) {
        super(message);
    }

    public TencentFileServiceException(CosClientException cosClientException) {
        super(cosClientException.getMessage());
        this.cosClientException = cosClientException;
    }

    public TencentFileServiceException(CosServiceException cosServiceException) {
        super(cosServiceException.getMessage());
        this.cosServiceException = cosServiceException;
    }

}
