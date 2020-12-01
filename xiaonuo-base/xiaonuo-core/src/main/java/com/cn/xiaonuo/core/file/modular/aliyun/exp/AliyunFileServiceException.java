package com.cn.xiaonuo.core.file.modular.aliyun.exp;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import lombok.Getter;

/**
 * 腾讯文件操作异常
 *
 * @author xuyuxiang
 * @date 2020-05-23-2:42 下午
 */
@Getter
public class AliyunFileServiceException extends RuntimeException {

    /**
     * 客户端异常
     * <p>
     * 是由于客户端原因导致无法和服务端完成正常的交互而导致的失败，如客户端无法连接到服务端，无法解析服务端返回的数据
     */
    private ClientException clientException;

    /**
     * 服务端异常
     * <p>
     * 用于指交互正常完成，但是操作失败的场景
     * <p>
     * 例如客户端访问一个不存在 Bucket，删除一个不存在的文件，没有权限进行某个操作， 服务端故障异常等
     */
    private OSSException ossException;

    public AliyunFileServiceException(String message) {
        super(message);
    }

    public AliyunFileServiceException(ClientException clientException) {
        super(clientException.getMessage());
        this.clientException = clientException;
    }

    public AliyunFileServiceException(OSSException ossException) {
        super(ossException.getMessage());
        this.ossException = ossException;
    }

}
