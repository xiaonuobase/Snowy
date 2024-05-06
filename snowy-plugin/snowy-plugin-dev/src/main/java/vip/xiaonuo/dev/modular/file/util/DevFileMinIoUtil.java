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
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.minio.*;
import io.minio.http.Method;
import jakarta.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.modular.file.enums.DevFileBucketAuthEnum;

import java.io.*;

/**
 * MINIO文件工具类
 * 参考文档：http://docs.minio.org.cn/docs/master/java-client-quickstart-guide
 *
 * @author xuyuxiang
 * @date 2022/1/2 18:13
 */
@Slf4j
public class DevFileMinIoUtil {

    private static MinioClient client;

    private static String defaultBucketName;

    private static final String SNOWY_FILE_MINIO_ACCESS_KEY_KEY = "SNOWY_FILE_MINIO_ACCESS_KEY";
    private static final String SNOWY_FILE_MINIO_SECRET_KEY_KEY = "SNOWY_FILE_MINIO_SECRET_KEY";
    private static final String SNOWY_FILE_MINIO_END_POINT_KEY = "SNOWY_FILE_MINIO_END_POINT";
    private static final String SNOWY_FILE_MINIO_DEFAULT_BUCKET_NAME = "SNOWY_FILE_MINIO_DEFAULT_BUCKET_NAME";


    /**
     * 初始化操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    private static void initClient() {

        DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);

        /* accessKey */
        String accessKey = devConfigApi.getValueByKey(SNOWY_FILE_MINIO_ACCESS_KEY_KEY);

        if(ObjectUtil.isEmpty(accessKey)) {
            throw new CommonException("MINIO文件操作客户端未正确配置：accessKey为空");
        }

        /* secretKey */
        String secretKey = devConfigApi.getValueByKey(SNOWY_FILE_MINIO_SECRET_KEY_KEY);

        if(ObjectUtil.isEmpty(secretKey)) {
            throw new CommonException("MINIO文件操作客户端未正确配置：secretKey为空");
        }

        /* endpoint */
        String endpoint = devConfigApi.getValueByKey(SNOWY_FILE_MINIO_END_POINT_KEY);

        if(ObjectUtil.isEmpty(endpoint)) {
            throw new CommonException("MINIO文件操作客户端未正确配置：endpoint为空");
        }

        /* 默认BucketName */
        defaultBucketName = devConfigApi.getValueByKey(SNOWY_FILE_MINIO_DEFAULT_BUCKET_NAME);

        if(ObjectUtil.isEmpty(defaultBucketName)) {
            throw new CommonException("MINIO文件操作客户端未正确配置：defaultBucketName为空");
        }

        client = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
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
        // 无需
    }

    /**
     * 获取操作的客户端
     *
     * @author xuyuxiang
     * @date 2022/1/5 23:24
     */
    public static MinioClient getClient() {
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
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            client.bucketExists(bucketExistsArgs);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
        return false;
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
        setFileAcl(bucketName, "*", devFileBucketAuthEnum);
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
            GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucketName).object(key).build();
            InputStream object = client.getObject(getObjectArgs);
            return !ObjectUtil.isEmpty(object);
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
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(key)
                    .contentType(getFileContentType(key)).stream(byteArrayInputStream, bytes.length, -1).build();
            client.putObject(putObjectArgs);
        } catch (Exception e) {
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
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(key)
                    .contentType(getFileContentType(key)).stream(inputStream, inputStream.available(), -1).build();
            client.putObject(putObjectArgs);
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
        try {
            initClient();
            GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucketName).object(key).build();
            InputStream inputStream = client.getObject(getObjectArgs);
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
     * @date 2022/1/5 23:24
     */
    public static void setFileAcl(String bucketName, String key, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        try {
            JSONObject configObject = JSONUtil.createObj().set("Version", "2012-10-17");
            JSONArray statementArray = JSONUtil.createArray();
            JSONArray actionArray = JSONUtil.createArray();
            if(devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ)) {
                actionArray.put("s3:GetObject");
            }
            if(devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
                actionArray.put("s3:GetObject");
                actionArray.put("s3:PutObject");
            }
            JSONObject statementObject = JSONUtil.createObj();
            statementObject.set("Effect", "Allow").set("Principal", JSONUtil.createObj().set("AWS", JSONUtil.createArray().put("*")))
                    .set("Action", actionArray).set("Resource", JSONUtil.createArray().put("arn:aws:s3:::" + bucketName + "/*"));
            statementArray.put(statementObject);
            configObject.set("Statement", statementArray);
            String config = JSONUtil.toJsonStr(configObject);
            SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs.builder().bucket(bucketName).config(config).build();
            client.setBucketPolicy(setBucketPolicyArgs);
        } catch (Exception e) {
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
            CopySource copySource = CopySource.builder().bucket(originBucketName).object(originFileKey).build();
            CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder().source(copySource).bucket(newBucketName).object(newFileKey).build();
            client.copyObject(copyObjectArgs);
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
     * @date 2022/1/5 23:24
     */
    public static String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
        try {
            initClient();
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder().bucket(bucketName)
                    .object(key).method(Method.GET).expiry(timeoutMillis.intValue()).build();
            return client.getPresignedObjectUrl(getPresignedObjectUrlArgs);
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
     * @date 2022/1/5 23:24
     */
    public static String getFileAuthUrl(String bucketName, String key) {
        try {
            initClient();
            DevConfigApi devConfigApi = SpringUtil.getBean(DevConfigApi.class);
            return devConfigApi.getValueByKey(SNOWY_FILE_MINIO_END_POINT_KEY) + StrUtil.SLASH + bucketName + StrUtil.SLASH + key;
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
     * @date 2022/1/5 23:24
     */
    public static void deleteFile(String bucketName, String key) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucketName).object(key).build();
            client.removeObject(removeObjectArgs);
        } catch (Exception e) {
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
