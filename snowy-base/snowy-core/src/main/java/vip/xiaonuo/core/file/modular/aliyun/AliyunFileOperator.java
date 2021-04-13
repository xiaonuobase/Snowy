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
package vip.xiaonuo.core.file.modular.aliyun;

import cn.hutool.core.io.IoUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import vip.xiaonuo.core.file.FileOperator;
import vip.xiaonuo.core.file.common.enums.BucketAuthEnum;
import vip.xiaonuo.core.file.modular.aliyun.exp.AliyunFileServiceException;
import vip.xiaonuo.core.file.modular.aliyun.prop.AliyunOssProperties;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 阿里云文件操作
 *
 * @author xuyuxiang
 * @date 2020/5/25 2:33 下午
 */
public class AliyunFileOperator implements FileOperator {

    /**
     * 阿里云文件操作客户端
     */
    private OSS ossClient;

    /**
     * 阿里云oss的配置
     */
    private final AliyunOssProperties aliyunOssProperties;

    public AliyunFileOperator(AliyunOssProperties aliyunOssProperties) {
        this.aliyunOssProperties = aliyunOssProperties;
        this.initClient();
    }

    @Override
    public void initClient() {
        String endpoint = aliyunOssProperties.getEndPoint();
        String accessKeyId = aliyunOssProperties.getAccessKeyId();
        String accessKeySecret = aliyunOssProperties.getAccessKeySecret();

        // 创建OSSClient实例。
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public void destroyClient() {
        ossClient.shutdown();
    }

    @Override
    public Object getClient() {
        return ossClient;
    }

    @Override
    public boolean doesBucketExist(String bucketName) {
        try {
            return ossClient.doesBucketExist(bucketName);
        } catch (OSSException e) {
            throw new AliyunFileServiceException(e);
        } catch (ClientException e) {
            throw new AliyunFileServiceException(e);
        }
    }

    @Override
    public void setBucketAcl(String bucketName, BucketAuthEnum bucketAuthEnum) {
        try {
            if (bucketAuthEnum.equals(BucketAuthEnum.PRIVATE)) {
                ossClient.setBucketAcl(bucketName, CannedAccessControlList.Private);
            } else if (bucketAuthEnum.equals(BucketAuthEnum.PUBLIC_READ)) {
                ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            } else if (bucketAuthEnum.equals(BucketAuthEnum.PUBLIC_READ_WRITE)) {
                ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite);
            }
        } catch (OSSException e) {
            throw new AliyunFileServiceException(e);
        } catch (ClientException e) {
            throw new AliyunFileServiceException(e);
        }
    }

    @Override
    public boolean isExistingFile(String bucketName, String key) {
        try {
            return ossClient.doesObjectExist(bucketName, key);
        } catch (OSSException e) {
            throw new AliyunFileServiceException(e);
        } catch (ClientException e) {
            throw new AliyunFileServiceException(e);
        }
    }

    @Override
    public void storageFile(String bucketName, String key, byte[] bytes) {
        try {
            ossClient.putObject(bucketName, key, new ByteArrayInputStream(bytes));
        } catch (OSSException e) {
            throw new AliyunFileServiceException(e);
        } catch (ClientException e) {
            throw new AliyunFileServiceException(e);
        }
    }

    @Override
    public void storageFile(String bucketName, String key, InputStream inputStream) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream);
            ossClient.putObject(putObjectRequest);
        } catch (OSSException e) {
            throw new AliyunFileServiceException(e);
        } catch (ClientException e) {
            throw new AliyunFileServiceException(e);
        }
    }

    @Override
    public byte[] getFileBytes(String bucketName, String key) {
        InputStream objectContent = null;
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, key);
            objectContent = ossObject.getObjectContent();
            return IoUtil.readBytes(objectContent);
        } catch (OSSException e) {
            throw new AliyunFileServiceException(e);
        } catch (ClientException e) {
            throw new AliyunFileServiceException(e);
        } finally {
            IoUtil.close(objectContent);
        }

    }

    @Override
    public void setFileAcl(String bucketName, String key, BucketAuthEnum bucketAuthEnum) {
        try {
            if (bucketAuthEnum.equals(BucketAuthEnum.PRIVATE)) {
                ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.Private);
            } else if (bucketAuthEnum.equals(BucketAuthEnum.PUBLIC_READ)) {
                ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
            } else if (bucketAuthEnum.equals(BucketAuthEnum.PUBLIC_READ_WRITE)) {
                ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.PublicReadWrite);
            }
        } catch (OSSException e) {
            throw new AliyunFileServiceException(e);
        } catch (ClientException e) {
            throw new AliyunFileServiceException(e);
        }
    }

    @Override
    public void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
        try {
            ossClient.copyObject(originBucketName, originFileKey, newBucketName, newFileKey);
        } catch (OSSException e) {
            throw new AliyunFileServiceException(e);
        } catch (ClientException e) {
            throw new AliyunFileServiceException(e);
        }
    }

    @Override
    public String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
        try {
            Date expiration = new Date(new Date().getTime() + timeoutMillis);
            URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
            return url.toString();
        } catch (OSSException e) {
            throw new AliyunFileServiceException(e);
        } catch (ClientException e) {
            throw new AliyunFileServiceException(e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String key) {
        ossClient.deleteObject(bucketName, key);
    }

}
