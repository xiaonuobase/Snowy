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
package vip.xiaonuo.dev.modular.sms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 短信实体
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
@Getter
@Setter
@TableName("DEV_SMS")
public class DevSms extends CommonEntity {

    /** id */
    @Schema(description = "id")
    private String id;

    /** 短信引擎 */
    @Schema(description = "短信引擎")
    private String engine;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phoneNumbers;

    /** 短信签名 */
    @Schema(description = "短信签名")
    private String signName;

    /** 模板编码 */
    @Schema(description = "模板编码")
    private String templateCode;

    /** 发送参数 */
    @Schema(description = "发送参数")
    private String templateParam;

    /** 回执信息 */
    @Schema(description = "回执信息")
    private String receiptInfo;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;
}
