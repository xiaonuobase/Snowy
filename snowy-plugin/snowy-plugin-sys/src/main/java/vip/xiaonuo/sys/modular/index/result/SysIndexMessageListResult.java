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
package vip.xiaonuo.sys.modular.index.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 站内信列表结果
 *
 * @author xuyuxiang
 * @date 2022/7/31 16:39
 */
@Getter
@Setter
public class SysIndexMessageListResult {

    /** id */
    @Schema(description = "id")
    private String id;

    /** 分类 */
    @Schema(description = "分类")
    private String category;

    /** 主题 */
    @Schema(description = "主题")
    private String subject;

    /** 正文 */
    @Schema(description = "正文")
    private String content;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private Date createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createUser;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private Date updateTime;

    /** 更新人 */
    @Schema(description = "更新人")
    private String updateUser;
}
