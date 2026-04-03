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
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.modular.file.enums.DevFileBucketAuthEnum;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URI;
import java.time.Duration;

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
    private static S3Client client;

    private static String defaultBucketName;

    private static String endpoint;

    private static final String SNOWY_FILE_RUSTFS_ACCESS_KEY_KEY = "SNOWY_FILE_RUSTFS_ACCESS_KEY";
    private static final String SNOWY_FILE_RUSTFS_SECRET_KEY_KEY = "SNOWY_FILE_RUSTFS_SECRET_KEY";
    private static final String SNOWY_FILE_RUSTFS_END_POINT_KEY = "SNOWY_FILE_RUSTFS_END_POINT";
    private static final String SNOWY_FILE_RUSTFS_DEFAULT_BUCKET_NAME = "SNOWY_FILE_RUSTFS_DEFAULT_BUCKET_NAME";
    private static final String SNOWY_FILE_RUSTFS_REGION_KEY = "SNOWY_FILE_RUSTFS_REGION";

    /**
     * 初始化操作的客户端
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
        endpoint = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_END_POINT_KEY);

        if(ObjectUtil.isEmpty(endpoint)) {
            throw new CommonException("RUSTFS文件操作客户端未正确配置：endpoint为空");
        }

        /* 默认BucketName */
        defaultBucketName = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_DEFAULT_BUCKET_NAME);

        if(ObjectUtil.isEmpty(defaultBucketName)) {
            throw new CommonException("RUSTFS文件操作客户端未正确配置：defaultBucketName为空");
        }

        /* region */
        String region = devConfigApi.getValueByKey(SNOWY_FILE_RUSTFS_REGION_KEY);
        if(ObjectUtil.isEmpty(region)) {
            region = "us-east-1";
        }

        client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .forcePathStyle(true)
                .build();
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
        if (ObjectUtil.isNotEmpty(client)) {
            client.close();
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

            // 使用presigner生成临时URL
            software.amazon.awssdk.services.s3.presigner.S3Presigner presigner =
                    software.amazon.awssdk.services.s3.presigner.S3Presigner.builder()
                            .endpointOverride(URI.create(endpoint))
                            .credentialsProvider(client.serviceClientConfiguration().credentialsProvider().get())
                            .region(client.serviceClientConfiguration().region())
                            .build();

            software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest presignRequest =
                    software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest.builder()
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
