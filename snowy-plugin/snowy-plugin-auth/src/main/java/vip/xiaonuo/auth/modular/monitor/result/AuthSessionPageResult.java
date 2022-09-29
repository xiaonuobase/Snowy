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
package vip.xiaonuo.auth.modular.monitor.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * B端会话结果集
 *
 * @author xuyuxiang
 * @date 2022/7/28 14:46
 **/
@Getter
@Setter
public class AuthSessionPageResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 头像 */
    @ApiModelProperty(value = "头像", position = 2)
    private String avatar;

    /** 账号 */
    @ApiModelProperty(value = "账号", position = 3)
    private String account;

    /** 姓名 */
    @ApiModelProperty(value = "姓名", position = 4)
    private String name;

    /** 上次登录ip */
    @ApiModelProperty(value = "上次登录ip", position = 5)
    private String lastLoginIp;

    /** 上次登录地点 */
    @ApiModelProperty(value = "上次登录地点", position = 6)
    private String lastLoginAddress;

    /** 上次登录时间 */
    @ApiModelProperty(value = "上次登录时间", position = 7)
    private Date lastLoginTime;

    /** 上次登录设备 */
    @ApiModelProperty(value = "上次登录设备", position = 8)
    private String lastLoginDevice;

    /** 最新登录ip */
    @ApiModelProperty(value = "最新登录ip", position = 9)
    private String latestLoginIp;

    /** 最新登录地点 */
    @ApiModelProperty(value = "最新登录地点", position = 10)
    private String latestLoginAddress;

    /** 最新登录时间 */
    @ApiModelProperty(value = "最新登录时间", position = 11)
    private Date latestLoginTime;

    /** 最新登录设备 */
    @ApiModelProperty(value = "最新登录设备", position = 12)
    private String latestLoginDevice;

    /** 会话id */
    @ApiModelProperty(value = "会话id", position = 13)
    private String sessionId;

    /** 会话创建时间 */
    @ApiModelProperty(value = "会话创建时间", position = 14)
    private Date sessionCreateTime;

    /** 会话剩余有效期 */
    @ApiModelProperty(value = "会话剩余有效期", position = 15)
    private String sessionTimeout;

    /** 令牌数量 */
    @ApiModelProperty(value = "令牌数量", position = 16)
    private Integer tokenCount;

    /** 令牌信息集合 */
    @ApiModelProperty(value = "令牌信息集合", position = 17)
    private List<TokenSignInfo> tokenSignList;

    /**
     * 令牌信息类
     *
     * @author xuyuxiang
     * @date 2022/7/28 15:04
     **/
    @Getter
    @Setter
    public static class TokenSignInfo {

        /** token值 */
        @ApiModelProperty(value = "token值", position = 1)
        private String tokenValue;

        /** 登录设备 */
        @ApiModelProperty(value = "登录设备", position = 2)
        private String tokenDevice;

        /** token剩余有效期 */
        @ApiModelProperty(value = "token剩余有效期", position = 3)
        private String tokenTimeout;

        /** token剩余有效期百分比 */
        @ApiModelProperty(value = "token剩余有效期百分比", position = 4)
        private Double tokenTimeoutPercent;
    }
}
