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
package vip.xiaonuo.sys.modular.user.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 动口令信息结果
 *
 * @author xuyuxiang
 * @date 2022/7/8 9:28
 **/
@Getter
@Setter
@Builder
public class SysUserOtpInfoResult {

    /** 动态口令信息，Base64 */
    @Schema(description = "动态口令信息，Base64")
    private String otpInfoBase64;

    /** 动态口令信息，JSON */
    @Schema(description = "动态口令信息，JSON")
    private OtpInfo otpInfo;

    /**
     * 动态口令信息类
     *
     * @author xuyuxiang
     * @date 2022/4/28 23:19
     */
    @Getter
    @Setter
    @Builder
    public static class OtpInfo {

        /** 发行者 */
        @Schema(description = "发行者")
        private String issuer;

        /** 账号 */
        @Schema(description = "账号")
        private String account;

        /** 密钥 */
        @Schema(description = "密钥")
        private String secretKey;

        /** 算法 */
        @Schema(description = "算法")
        private String algorithm;

        /** 位数 */
        @Schema(description = "位数")
        private String digits;

        /** 周期 */
        @Schema(description = "周期")
        private String period;
    }
}
