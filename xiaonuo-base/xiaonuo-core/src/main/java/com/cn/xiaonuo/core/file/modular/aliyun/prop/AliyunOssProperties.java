package com.cn.xiaonuo.core.file.modular.aliyun.prop;

import lombok.Data;

/**
 * 腾讯云cos文件存储配置
 *
 * @author xuyuxiang
 * @date 2020/5/22 6:56 下午
 */
@Data
public class AliyunOssProperties {

    /**
     * 默认北京，内网
     * <p>
     * https://help.aliyun.com/document_detail/31837.html?spm=a2c4g.11186623.2.17.467f45dcjB4WQQ#concept-zt4-cvy-5db
     */
    private String endPoint = "http://oss-cn-beijing.aliyuncs.com";

    /**
     * 秘钥id
     */
    private String accessKeyId;

    /**
     * 秘钥secret
     */
    private String accessKeySecret;

}
