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
package vip.xiaonuo.dev.modular.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import jakarta.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.modular.file.enums.DevFileBucketAuthEnum;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 腾讯云文件工具类
 * 参考文档：https://cloud.tencent.com/document/product/436/10199
 *
 * @author xuyuxiang
 * @date 2022/1/2 18:13
 */
@Slf4j
public class DevFileTencentUtil {

    private static COSClient client;

    private static String defaultBucketName;

    private static TransferManager transferManager;

    private static final String SNOWY_FILE_TENCENT_SECRET_ID_KEY = "SNOWY_FILE_TENCENT_SECRET_ID";
    private static final String SNOWY_FILE_TENCENT_SECRET_KEY_KEY = "SNOWY_FILE_TENCENT_SECRET_KEY";
    private static final String SNOWY_FILE_TENCENT_REGION_ID_KEY = "SNOWY_FILE_TENCENT_REGION_ID";
    private static final String SNOWY_FILE_TENCENT_DEFAULT_BUCKET_NAME = "SNOWY_FILE_TENCENT_DEFAULT_BUCKET_NAME";

    /**
     * 初始化操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    private static void initClient() {

        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);

        /* secretId */
        String secretId = devConfigApi.getValueByKey(SNOWY_FILE_TENCENT_SECRET_ID_KEY);

        if(ObjectUtil.isEmpty(secretId)) {
            throw new CommonException("腾讯云文件操作客户端未正确配置：secretId为空");
        }

        /* secretKey */
        String secretKey = devConfigApi.getValueByKey(SNOWY_FILE_TENCENT_SECRET_KEY_KEY);

        if(ObjectUtil.isEmpty(secretKey)) {
            throw new CommonException("腾讯云文件操作客户端未正确配置：secretKey为空");
        }

        /* regionId */
        String regionId = devConfigApi.getValueByKey(SNOWY_FILE_TENCENT_REGION_ID_KEY);

        if(ObjectUtil.isEmpty(regionId)) {
            throw new CommonException("腾讯云文件操作客户端未正确配置：regionId为空");
        }

        /* 默认BucketName */
        defaultBucketName = devConfigApi.getValueByKey(SNOWY_FILE_TENCENT_DEFAULT_BUCKET_NAME);

        if(ObjectUtil.isEmpty(defaultBucketName)) {
            throw new CommonException("腾讯云文件操作客户端未正确配置：defaultBucketName为空");
        }


        // 1.初始化用户身份信息
        Region region = new Region(regionId);
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        // 2.设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224

        ClientConfig clientConfig = new ClientConfig(region);

        // 3.生成 cos 客户端。
        client = new COSClient(cred, clientConfig);

        // 4.线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(32);

        // 5.传入一个线程池, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        transferManager = new TransferManager(client, threadPool);

        // 6.设置高级接口的分块上传阈值和分块大小为10MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(10 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(10 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
    }

    /**
     * 获取默认存储桶名称
     *
     * @author xuyuxiang
     * @date 2022/6/22 18:05
     **/
    public static String getDefaultBucketName() {
        initClient();
        return defaultBucketName;
    }

    /**
     * 销毁操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static void destroyClient() {
        initClient();
        client.shutdown();
    }

    /**
     * 获取操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static COSClient getClient() {
        initClient();
        return client;
    }

    /**
     * 查询存储桶是否存在
     * 例如：传入参数examplebucket-1250000000，返回true代表存在此桶
     *
     * @param bucketName 桶名称
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static boolean doesBucketExist(String bucketName) {
        try {
            initClient();
            return client.doesBucketExist(bucketName);
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 设置预定义策略
     * 预定义策略如公有读、公有读写、私有读
     *
     * @param bucketName 桶名称
     * @param devFileBucketAuthEnum 存储桶权限
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static void setBucketAcl(String bucketName, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        try {
            initClient();
            if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PRIVATE)) {
                client.setBucketAcl(bucketName, CannedAccessControlList.Private);
            } else if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ)) {
                client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            } else if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
                client.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite);
            }
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 判断是否存在文件
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static boolean isExistingFile(String bucketName, String key) {
        try {
            initClient();
            client.getObjectMetadata(bucketName, key);
            return true;
        } catch (CosClientException e) {
            return false;
        }
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param file      文件
     * @author xuyuxiang
     * @date 2022/1/5 23:45
     */
    public static void storageFile(String bucketName, String key, File file) {
        BufferedInputStream inputStream;
        try {
            inputStream = FileUtil.getInputStream(file);
        } catch (IORuntimeException e) {
            throw new CommonException("获取文件流异常，名称是：{}", file.getName());
        }
        storageFile(bucketName, key, inputStream);
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param multipartFile      文件
     * @author xuyuxiang
     * @date 2022/1/5 23:45
     */
    public static void storageFile(String bucketName, String key, MultipartFile multipartFile) {
        InputStream inputStream;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {
            throw new CommonException("获取文件流异常，名称是：{}", multipartFile.getName());
        }
        storageFile(bucketName, key, inputStream);
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param bytes      文件字节数组
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static void storageFile(String bucketName, String key, byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            initClient();
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(getFileContentType(key));
            client.putObject(bucketName, key, byteArrayInputStream, objectMetadata);
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        } finally {
            IoUtil.close(byteArrayInputStream);
        }
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName  桶名称
     * @param key         唯一标示id，例如a.txt, doc/a.txt
     * @param inputStream 文件流
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static void storageFile(String bucketName, String key, InputStream inputStream) {
        try {
            initClient();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(getFileContentType(key));
            client.putObject(bucketName, key, inputStream, objectMetadata);
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }
    }

    /**
     * 存储文件，返回外网地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param file      文件
     * @author xuyuxiang
     * @date 2022/1/5 23:45
     */
    public static String storageFileWithReturnUrl(String bucketName, String key, File file) {
        storageFile(bucketName, key, file);
        setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
        return getFileAuthUrl(bucketName, key);
    }

    /**
     * 存储文件，返回外网地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param multipartFile      文件
     * @author xuyuxiang
     * @date 2022/1/5 23:45
     */
    public static String storageFileWithReturnUrl(String bucketName, String key, MultipartFile multipartFile) {
        storageFile(bucketName, key, multipartFile);
        setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
        return getFileAuthUrl(bucketName, key);
    }

    /**
     * 存储文件，返回外网地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param bytes      文件字节数组
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static String storageFileWithReturnUrl(String bucketName, String key, byte[] bytes) {
        storageFile(bucketName, key, bytes);
        setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
        return getFileAuthUrl(bucketName, key);
    }

    /**
     * 存储文件，返回外网地址
     *
     * @param bucketName  桶名称
     * @param key         唯一标示id，例如a.txt, doc/a.txt
     * @param inputStream 文件流
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static String storageFileWithReturnUrl(String bucketName, String key, InputStream inputStream) {
        storageFile(bucketName, key, inputStream);
        setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
        return getFileAuthUrl(bucketName, key);
    }

    /**
     * 获取某个bucket下的文件字节
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static byte[] getFileBytes(String bucketName, String key) {
        COSObjectInputStream cosObjectInput = null;
        try {
            initClient();
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            COSObject cosObject = client.getObject(getObjectRequest);
            cosObjectInput = cosObject.getObjectContent();
            return IoUtil.readBytes(cosObjectInput);
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        } finally {
            IoUtil.close(cosObjectInput);
        }
    }

    /**
     * 设置文件访问权限管理
     *
     * @param bucketName     桶名称
     * @param key            唯一标示id，例如a.txt, doc/a.txt
     * @param devFileBucketAuthEnum 文件权限
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static void setFileAcl(String bucketName, String key, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        try {
            initClient();
            if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PRIVATE)) {
                client.setObjectAcl(bucketName, key, CannedAccessControlList.Private);
            } else if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ)) {
                client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
            } else if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
                client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicReadWrite);
            }
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 拷贝文件
     *
     * @param originBucketName 源文件桶
     * @param originFileKey    源文件名称
     * @param newBucketName    新文件桶
     * @param newFileKey       新文件名称
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
        try {
            initClient();
            transferManager.copy(originBucketName, originFileKey, newBucketName, newFileKey);
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取文件的下载地址（带鉴权和有效时间的），生成外网地址
     *
     * @param bucketName 文件桶
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param timeoutMillis 时效
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
        Date expirationDate = new Date(System.currentTimeMillis() + timeoutMillis);
        request.setExpiration(expirationDate);
        URL url;
        try {
            initClient();
            url = client.generatePresignedUrl(request);
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
        return url.toString();
    }

    /**
     * 获取文件的下载地址（永久的，文件必须为公有读），生成外网地址
     *
     * @param bucketName 文件桶
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static String getFileAuthUrl(String bucketName, String key) {
        URL url;
        try {
            initClient();
            url = client.getObjectUrl(bucketName, key);
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
        return url.toString();
    }

    /**
     * 删除文件
     *
     * @param bucketName 文件桶
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static void deleteFile(String bucketName, String key) {
        try{
            initClient();
            client.deleteObject(bucketName, key);
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 根据文件名获取ContentType
     *
     * @author xuyuxiang
     * @date 2022/1/6 11:27
     **/
    private static String getFileContentType(String key) {
        // 根据文件名获取contentType
        String contentType = "application/octet-stream";
        if (key.contains(".")) {
            contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(key);
        }
        return contentType;
    }
}
