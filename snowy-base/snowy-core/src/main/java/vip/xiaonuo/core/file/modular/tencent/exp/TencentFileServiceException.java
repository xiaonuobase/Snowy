/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.core.file.modular.tencent.exp;

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
