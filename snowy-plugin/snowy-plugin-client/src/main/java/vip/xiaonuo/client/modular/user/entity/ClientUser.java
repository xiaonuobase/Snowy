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
package vip.xiaonuo.client.modular.user.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fhs.core.trans.anno.Trans;
import com.fhs.core.trans.constant.TransType;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.handler.CommonSm4CbcTypeHandler;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.util.Date;

/**
 * C端用户实体
 *
 * @author xuyuxiang
 * @date 2022/4/21 16:13
 **/
@Getter
@Setter
@TableName(value = "CLIENT_USER", autoResultMap = true)
public class ClientUser extends CommonEntity implements TransPojo {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 头像 */
    @ApiModelProperty(value = "头像，图片base64", position = 2)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String avatar;

    /** 签名 */
    @ApiModelProperty(value = "签名，图片base64", position = 3)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String signature;

    /** 账号 */
    @ApiModelProperty(value = "账号", position = 4)
    private String account;

    /** 密码 */
    @ApiModelProperty(value = "密码", position = 5)
    private String password;

    /** 姓名 */
    @ApiModelProperty(value = "姓名", position = 6)
    private String name;

    /** 昵称 */
    @ApiModelProperty(value = "昵称", position = 7)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String nickname;

    /** 性别 */
    @ApiModelProperty(value = "性别", position = 8)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    @Trans(type = TransType.DICTIONARY, key = "GENDER")
    private String gender;

    /** 年龄 */
    @ApiModelProperty(value = "年龄", position = 9)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String age;

    /** 出生日期 */
    @ApiModelProperty(value = "出生日期", position = 10)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String birthday;

    /** 民族 */
    @ApiModelProperty(value = "民族", position = 11)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String nation;

    /** 籍贯 */
    @ApiModelProperty(value = "籍贯", position = 12)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String nativePlace;

    /** 家庭住址 */
    @ApiModelProperty(value = "家庭住址", position = 13)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String homeAddress;

    /** 通信地址 */
    @ApiModelProperty(value = "通信地址", position = 14)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String mailingAddress;

    /** 证件类型 */
    @ApiModelProperty(value = "证件类型", position = 15)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String idCardType;

    /** 证件号码 */
    @ApiModelProperty(value = "证件号码", position = 16)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED, typeHandler = CommonSm4CbcTypeHandler.class)
    private String idCardNumber;

    /** 文化程度 */
    @ApiModelProperty(value = "文化程度", position = 17)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String cultureLevel;

    /** 政治面貌 */
    @ApiModelProperty(value = "政治面貌", position = 18)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String politicalOutlook;

    /** 毕业院校 */
    @ApiModelProperty(value = "毕业院校", position = 19)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String college;

    /** 学历 */
    @ApiModelProperty(value = "学历", position = 20)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String education;

    /** 学制 */
    @ApiModelProperty(value = "学制", position = 21)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String eduLength;

    /** 学位 */
    @ApiModelProperty(value = "学位", position = 22)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String degree;

    /** 手机 */
    @ApiModelProperty(value = "手机", position = 23)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED, typeHandler = CommonSm4CbcTypeHandler.class)
    private String phone;

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱", position = 24)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String email;

    /** 家庭电话 */
    @ApiModelProperty(value = "家庭电话", position = 25)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String homeTel;

    /** 办公电话 */
    @ApiModelProperty(value = "办公电话", position = 26)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String officeTel;

    /** 紧急联系人 */
    @ApiModelProperty(value = "紧急联系人", position = 27)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String emergencyContact;

    /** 紧急联系人电话 */
    @ApiModelProperty(value = "紧急联系人电话", position = 28)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED, typeHandler = CommonSm4CbcTypeHandler.class)
    private String emergencyPhone;

    /** 紧急联系人地址 */
    @ApiModelProperty(value = "紧急联系人地址", position = 29)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String emergencyAddress;

    /** 上次登录ip */
    @ApiModelProperty(value = "上次登录ip", position = 30)
    private String lastLoginIp;

    /** 上次登录地点 */
    @ApiModelProperty(value = "上次登录地点", position = 31)
    private String lastLoginAddress;

    /** 上次登录时间 */
    @ApiModelProperty(value = "上次登录时间", position = 32)
    private Date lastLoginTime;

    /** 上次登录设备 */
    @ApiModelProperty(value = "上次登录设备", position = 33)
    private String lastLoginDevice;

    /** 最新登录ip */
    @ApiModelProperty(value = "最新登录ip", position = 34)
    private String latestLoginIp;

    /** 最新登录地点 */
    @ApiModelProperty(value = "最新登录地点", position = 35)
    private String latestLoginAddress;

    /** 最新登录时间 */
    @ApiModelProperty(value = "最新登录时间", position = 36)
    private Date latestLoginTime;

    /** 最新登录设备 */
    @ApiModelProperty(value = "最新登录设备", position = 37)
    private String latestLoginDevice;

    /** 用户状态 */
    @ApiModelProperty(value = "用户状态", position = 38)
    private String userStatus;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 39)
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 40)
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String extJson;
}
