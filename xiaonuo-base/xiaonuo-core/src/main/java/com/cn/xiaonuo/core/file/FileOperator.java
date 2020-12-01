package com.cn.xiaonuo.core.file;


import com.cn.xiaonuo.core.file.common.enums.BucketAuthEnum;

import java.io.InputStream;

/**
 * 文件操纵者（内网操作）
 * <p>
 * 如果存在未包含的操作，可以调用getClient()自行获取client进行操作
 *
 * @author xuyuxiang
 * @date 2018-06-27-下午12:37
 */
public interface FileOperator {

    /**
     * 初始化操作的客户端
     *
     * @author xuyuxiang
     * @date 2020/5/23 2:32 下午
     */
    void initClient();

    /**
     * 销毁操作的客户端
     *
     * @author xuyuxiang
     * @date 2020/5/23 2:32 下午
     */
    void destroyClient();

    /**
     * 获取操作的客户端
     *
     * @author xuyuxiang
     * @date 2020/5/23 2:58 下午
     */
    Object getClient();

    /**
     * 查询存储桶是否存在
     * <p>
     * 例如：传入参数examplebucket-1250000000，返回true代表存在此桶
     *
     * @author xuyuxiang
     * @date 2020/5/23 2:29 下午
     */
    boolean doesBucketExist(String bucketName);

    /**
     * 设置预定义策略
     * <p>
     * 预定义策略如公有读、公有读写、私有读
     *
     * @author xuyuxiang
     * @date 2020/5/23 3:02 下午
     */
    void setBucketAcl(String bucketName, BucketAuthEnum bucketAuthEnum);

    /**
     * 判断是否存在文件
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @author xuyuxiang
     * @date 2018/6/27 下午1:14
     */
    boolean isExistingFile(String bucketName, String key);

    /**
     * 存储文件
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @param bytes      文件字节数组
     * @author xuyuxiang
     * @date 2018/6/27 下午1:16
     */
    void storageFile(String bucketName, String key, byte[] bytes);

    /**
     * 存储文件（存放到指定的bucket里边）
     *
     * @param bucketName  桶名称
     * @param key         唯一标示id，例如a.txt, doc/a.txt
     * @param inputStream 文件流
     * @author xuyuxiang
     * @date 2018年10月19日13:20:37
     */
    void storageFile(String bucketName, String key, InputStream inputStream);

    /**
     * 获取某个bucket下的文件字节
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     * @author xuyuxiang
     * @date 2018/6/27 下午1:15
     */
    byte[] getFileBytes(String bucketName, String key);

    /**
     * 文件访问权限管理
     *
     * @param bucketName     桶名称
     * @param key            唯一标示id，例如a.txt, doc/a.txt
     * @param bucketAuthEnum 文件权限
     * @author xuyuxiang
     * @date 2020/5/23 5:30 下午
     */
    void setFileAcl(String bucketName, String key, BucketAuthEnum bucketAuthEnum);

    /**
     * 拷贝文件
     *
     * @param originBucketName 源文件桶
     * @param originFileKey    源文件名称
     * @param newBucketName    新文件桶
     * @param newFileKey       新文件名称
     * @author xuyuxiang
     * @date 2020/5/23 6:09 下午
     */
    void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey);

    /**
     * 获取文件的下载地址（带鉴权的），生成外网地址
     *
     * @param bucketName 文件桶
     * @param key        文件唯一标识
     * @author xuyuxiang
     * @date 2018/7/7 上午11:27
     */
    String getFileAuthUrl(String bucketName, String key, Long timeoutMillis);

    /**
     * 删除文件
     *
     * @param bucketName 文件桶
     * @param key        文件唯一标识
     * @author xuyuxiang
     * @date 2020/9/18
     */
    void deleteFile(String bucketName, String key);

}
