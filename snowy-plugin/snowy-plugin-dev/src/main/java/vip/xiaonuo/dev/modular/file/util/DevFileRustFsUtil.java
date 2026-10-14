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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.modular.file.enums.DevFileBucketAuthEnum;

import javax.activation.MimetypesFileTypeMap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.Duration;
import java.util.List;

/**
 * RUSTFS文件工具类
 * RustFS完全兼容S3协议，使用AWS S3 SDK访问
 * 参考文档：<a href="https://rustfs.com">RustFS官网</a>
 *
 * @author xuyuxiang
 * @date 2025/12/5 12:30
 */
@Slf4j
public class DevFileRustFsUtil {

    /**
     * -- GETTER --
     *  获取操作的客户端
     *
     */
    @Getter
    private static volatile S3Client client;

    private static volatile String defaultBucketName;

    private static volatile String endpoint;

    /** 当前缓存客户端对应的连接配置（endpoint、accessKey、secretKey、region），任一变化时重建客户端 */
    private static List<String> clientConfig;

    private static final String SNOWY_FILE_RUSTFS_ACCESS_KEY_KEY = "SNOWY_FILE_RUSTFS_ACCESS_KEY";
    private static final String SNOWY_FILE_RUSTFS_SECRET_KEY_KEY = "SNOWY_FILE_RUSTFS_SECRET_KEY";
    private static final String SNOWY_FILE_RUSTFS_END_POINT_KEY = "SNOWY_FILE_RUSTFS_END_POINT";
    private static final String SNOWY_FILE_RUSTFS_DEFAULT_BUCKET_NAME = "SNOWY_FILE_RUSTFS_DEFAULT_BUCKET_NAME";
    private static final String SNOWY_FILE_RUSTFS_REGION_KEY = "SNOWY_FILE_RUSTFS_REGION";

    /**
     * 初始化操作的客户端：每次读取最新配置，配置未变时复用缓存客户端，变化时关闭旧客户端并重建
     *
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     */
    private static void initClient() {

        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);

        /* accessKey */
        String accessKey = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_ACCESS_KEY_KEY);

        if(ObjectUtil.isEmpty(accessKey)) {
            throw new CommonException("RUSTFS文件操作客户端未正确配置：accessKey为空");
        }

        /* secretKey */
        String secretKey = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_SECRET_KEY_KEY);

        if(ObjectUtil.isEmpty(secretKey)) {
            throw new CommonException("RUSTFS文件操作客户端未正确配置：secretKey为空");
        }

        /* endpoint */
        String endpointValue = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_END_POINT_KEY);

        if(ObjectUtil.isEmpty(endpointValue)) {
            throw new CommonException("RUSTFS文件操作客户端未正确配置：endpoint为空");
        }

        /* 默认BucketName */
        String defaultBucketNameValue = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_DEFAULT_BUCKET_NAME);

        if(ObjectUtil.isEmpty(defaultBucketNameValue)) {
            throw new CommonException("RUSTFS文件操作客户端未正确配置：defaultBucketName为空");
        }

        /* region */
        String region = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_REGION_KEY);
        if(ObjectUtil.isEmpty(region)) {
            region = "us-east-1";
        }

        List<String> newClientConfig = List.of(endpointValue, accessKey, secretKey, region);
        synchronized (DevFileRustFsUtil.class) {
            endpoint = endpointValue;
            defaultBucketName = defaultBucketNameValue;
            // 连接配置未变化时复用已有客户端，避免每次调用新建客户端造成连接池泄漏
            if (client != null && newClientConfig.equals(clientConfig)) {
                return;
            }
            S3Client oldClient = client;
            client = S3Client.builder()
                    .endpointOverride(URI.create(endpointValue))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)))
                    .region(Region.of(region))
                    .forcePathStyle(true)
                    .build();
            clientConfig = newClientConfig;
            // 配置变化后关闭旧客户端，仅在管理员修改连接配置的瞬间可能影响正在进行的请求
            if (oldClient != null) {
                closeQuietly(oldClient);
            }
        }
    }

    /**
     * 关闭客户端，关闭异常只记日志
     *
     * @author yubaoshan
     * @date 2026/10/13 14:50
     */
    private static void closeQuietly(S3Client s3Client) {
        try {
            s3Client.close();
        } catch (Exception e) {
            log.warn("关闭RUSTFS旧客户端失败：{}", e.getMessage());
        }
    }

    /**
     * 获取默认存储桶名称
     *
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     **/
    public static String getDefaultBucketName() {
        initClient();
        return defaultBucketName;
    }

    /**
     * 销毁操作的客户端
     *
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     */
    public static void destroyClient() {
        synchronized (DevFileRustFsUtil.class) {
            if (ObjectUtil.isNotEmpty(client)) {
                closeQuietly(client);
            }
            // 销毁后清空缓存，下次调用按最新配置重建
            client = null;
            clientConfig = null;
        }
    }

    /**
     * 查询存储桶是否存在
     * 例如：传入参数examplebucket，返回true代表存在此桶
     *
     * @param bucketName 桶名称
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     */
    public static boolean doesBucketExist(String bucketName) {
        try {
            initClient();
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder().bucket(bucketName).build();
            client.headBucket(headBucketRequest);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 桶名称
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     */
    public static void createBucket(String bucketName) {
        try {
            initClient();
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder().bucket(bucketName).build();
            client.createBucket(createBucketRequest);
        } catch (Exception e) {
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
     * @date 2025/12/5 12:30
     */
    public static void setBucketAcl(String bucketName, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        setFileAcl(bucketName, "*", devFileBucketAuthEnum);
    }

    /**
     * 判断是否存在文件
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     */
    public static boolean isExistingFile(String bucketName, String key) {
        try {
            initClient();
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(bucketName).key(key).build();
            client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取对象实际大小（字节），对象不存在返回null，连接或鉴权异常直接报错
     *
     * @author yubaoshan
     * @date 2026/10/13 14:00
     */
    public static Long getFileSize(String bucketName, String key) {
        try {
            initClient();
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(bucketName).key(key).build();
            return client.headObject(headObjectRequest).contentLength();
        } catch (S3Exception e) {
            // 对象不存在时部分兼容实现只返回404状态码，不一定映射为NoSuchKeyException
            if (e instanceof NoSuchKeyException || e.statusCode() == 404) {
                return null;
            }
            throw new CommonException("读取RUSTFS对象信息失败：{}", e.getMessage());
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException("读取RUSTFS对象信息失败：{}", e.getMessage());
        }
    }

    /**
     * 按已知长度流式存储文件，不整读进内存，不返回地址，结束后关闭文件流
     *
     * @author yubaoshan
     * @date 2026/10/13 14:00
     */
    public static void storageFile(String bucketName, String key, InputStream inputStream, long size) {
        try {
            initClient();
            // 确保bucket存在
            if (!doesBucketExist(bucketName)) {
                createBucket(bucketName);
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(getFileContentType(key))
                    .build();
            client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size));
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param file      文件
     * @author xuyuxiang
     * @date 2025/12/5 12:30
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
     * @date 2025/12/5 12:30
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
     * @date 2025/12/5 12:30
     */
    public static void storageFile(String bucketName, String key, byte[] bytes) {
        try {
            initClient();
            // 确保bucket存在
            if (!doesBucketExist(bucketName)) {
                createBucket(bucketName);
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(getFileContentType(key))
                    .build();
            client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName  桶名称
     * @param key         唯一标示id，例如a.txt, doc/a.txt
     * @param inputStream 文件流
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     */
    public static void storageFile(String bucketName, String key, InputStream inputStream) {
        try {
            initClient();
            // 确保bucket存在
            if (!doesBucketExist(bucketName)) {
                createBucket(bucketName);
            }
            byte[] bytes = IoUtil.readBytes(inputStream);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(getFileContentType(key))
                    .build();
            client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
        } catch (Exception e) {
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
     * @date 2025/12/5 12:30
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
     * @date 2025/12/5 12:30
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
     * @date 2025/12/5 12:30
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
     * @date 2025/12/5 12:30
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
     * @date 2025/12/5 12:30
     */
    public static byte[] getFileBytes(String bucketName, String key) {
        try {
            initClient();
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
            InputStream inputStream = client.getObject(getObjectRequest);
            return IoUtil.readBytes(inputStream);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 把对象边读边写入输出流，不整读进内存，结束后关闭对象流，不关闭输出流
     *
     * @param bucketName   桶名称
     * @param key          唯一标示id，例如a.txt, doc/a.txt
     * @param outputStream 输出流
     * @author yubaoshan
     * @date 2026/10/13 23:50
     */
    public static void writeFileToStream(String bucketName, String key, OutputStream outputStream) {
        ResponseInputStream<GetObjectResponse> inputStream = null;
        try {
            initClient();
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
            inputStream = client.getObject(getObjectRequest);
            IoUtil.copy(inputStream, outputStream);
        } catch (Exception e) {
            // 中途失败（如客户端断开）时直接断开连接，否则关闭对象流会把剩余内容全部读完
            if (ObjectUtil.isNotNull(inputStream)) {
                inputStream.abort();
            }
            if (e instanceof CommonException) {
                throw (CommonException) e;
            }
            throw new CommonException("读取RUSTFS文件失败：{}", e.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }
    }

    /**
     * 设置文件访问权限管理
     *
     * @param bucketName     桶名称
     * @param key             唯一标示id，例如a.txt, doc/a.txt
     * @param devFileBucketAuthEnum 文件权限
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     */
    public static void setFileAcl(String bucketName, String key, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        try {
            initClient();
            String policy;
            if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ)) {
                policy = "public-read";
            } else if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
                policy = "public-read-write";
            } else {
                policy = "private";
            }
            // 使用bucket policy设置权限
            String policyJson = "{" +
                    "\"Version\":\"2012-10-17\"," +
                    "\"Statement\":[{" +
                    "\"Effect\":\"Allow\"," +
                    "\"Principal\":\"*\"," +
                    "\"Action\":[\"s3:GetObject\"]," +
                    "\"Resource\":\"arn:aws:s3:::" + bucketName + "/" + key + "*\"" +
                    "}]}";

            PutBucketPolicyRequest putBucketPolicyRequest = PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(policyJson)
                    .build();
            client.putBucketPolicy(putBucketPolicyRequest);
        } catch (Exception e) {
            log.warn("设置文件ACL失败，可能不影响正常使用：{}", e.getMessage());
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
     * @date 2025/12/5 12:30
     */
    public static void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
        try {
            initClient();
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(originBucketName)
                    .sourceKey(originFileKey)
                    .destinationBucket(newBucketName)
                    .destinationKey(newFileKey)
                    .build();
            client.copyObject(copyObjectRequest);
        } catch (Exception e) {
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
     * @date 2025/12/5 12:30
     */
    public static String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
        try {
            initClient();
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
        
            // 使用client的配置信息创建预签名
            DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
            String accessKey = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_ACCESS_KEY_KEY);
            String secretKey = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_SECRET_KEY_KEY);
            String regionStr = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_REGION_KEY);
            if(ObjectUtil.isEmpty(regionStr)) {
                regionStr = "us-east-1";
            }
            
            S3Presigner presigner = S3Presigner.builder()
                            .credentialsProvider(StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKey, secretKey)))
                            .region(Region.of(regionStr))
                            .endpointOverride(URI.create(endpoint))
                            .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMillis(timeoutMillis))
                            .getObjectRequest(getObjectRequest)
                            .build();

            String url = presigner.presignGetObject(presignRequest).url().toString();
            presigner.close();
            return url;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取文件的下载地址（永久的，文件必须为公有读），生成外网地址
     *
     * @param bucketName 文件桶
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     */
    public static String getFileAuthUrl(String bucketName, String key) {
        try {
            initClient();
            return endpoint + "/" + bucketName + "/" + key;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName 文件桶
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @author xuyuxiang
     * @date 2025/12/5 12:30
     */
    public static void deleteFile(String bucketName, String key) {
        try {
            initClient();
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 根据文件名获取ContentType
     *
     * @author xuyuxiang
     * @date 2025/12/5 12:30
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
