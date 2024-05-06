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
package vip.xiaonuo.dev.modular.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 文件实体
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
@Getter
@Setter
@TableName("DEV_FILE")
public class DevFile extends CommonEntity {

    /** id */
    @Schema(description = "id")
    private String id;

    /** 存储引擎 */
    @Schema(description = "存储引擎")
    private String engine;

    /** 存储桶 */
    @Schema(description = "存储桶")
    private String bucket;

    /** 文件名称 */
    @Schema(description = "文件名称")
    private String name;

    /** 文件后缀 */
    @Schema(description = "文件后缀")
    private String suffix;

    /** 文件大小kb */
    @Schema(description = "文件大小kb")
    private String sizeKb;

    /** 文件大小（格式化后） */
    @Schema(description = "文件大小（格式化后）")
    private String sizeInfo;

    /** 文件的对象名（唯一名称） */
    @Schema(description = "文件的对象名（唯一名称）")
    private String objName;

    /** 文件存储路径 */
    @Schema(description = "文件存储路径")
    private String storagePath;

    /** 文件下载路径 */
    @Schema(description = "文件下载路径")
    private String downloadPath;

    /** 图片缩略图 */
    @Schema(description = "图片缩略图")
    private String thumbnail;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;
}
