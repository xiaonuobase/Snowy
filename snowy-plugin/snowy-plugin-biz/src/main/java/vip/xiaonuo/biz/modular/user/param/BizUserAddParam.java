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
package vip.xiaonuo.biz.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 人员添加参数
 *
 * @author xuyuxiang
 * @date 2022/7/26 15:36
 **/
@Getter
@Setter
public class BizUserAddParam {

    /** 账号 */
    @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "account不能为空")
    private String account;

    /** 姓名 */
    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "name不能为空")
    private String name;

    /** 机构id */
    @Schema(description = "机构id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "orgId不能为空")
    private String orgId;

    /** 岗位id */
    @Schema(description = "岗位id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "positionId不能为空")
    private String positionId;

    /** 岗级 */
    @Schema(description = "岗级")
    private String positionLevel;

    /** 主管id */
    @Schema(description = "主管id")
    private String directorId;

    /** 头像 */
    @Schema(description = "头像，图片base64")
    private String avatar;

    /** 签名 */
    @Schema(description = "签名，图片base64")
    private String signature;

    /** 昵称 */
    @Schema(description = "昵称")
    private String nickname;

    /** 性别 */
    @Schema(description = "性别")
    private String gender;

    /** 年龄 */
    @Schema(description = "年龄")
    private String age;

    /** 出生日期 */
    @Schema(description = "出生日期")
    private String birthday;

    /** 民族 */
    @Schema(description = "民族")
    private String nation;

    /** 籍贯 */
    @Schema(description = "籍贯")
    private String nativePlace;

    /** 家庭住址 */
    @Schema(description = "家庭住址")
    private String homeAddress;

    /** 通信地址 */
    @Schema(description = "通信地址")
    private String mailingAddress;

    /** 证件类型 */
    @Schema(description = "证件类型")
    private String idCardType;

    /** 证件号码 */
    @Schema(description = "证件号码")
    private String idCardNumber;

    /** 文化程度 */
    @Schema(description = "文化程度")
    private String cultureLevel;

    /** 政治面貌 */
    @Schema(description = "政治面貌")
    private String politicalOutlook;

    /** 毕业院校 */
    @Schema(description = "毕业院校")
    private String college;

    /** 学历 */
    @Schema(description = "学历")
    private String education;

    /** 学制 */
    @Schema(description = "学制")
    private String eduLength;

    /** 学位 */
    @Schema(description = "学位")
    private String degree;

    /** 手机 */
    @Schema(description = "手机")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 家庭电话 */
    @Schema(description = "家庭电话")
    private String homeTel;

    /** 办公电话 */
    @Schema(description = "办公电话")
    private String officeTel;

    /** 紧急联系人 */
    @Schema(description = "紧急联系人")
    private String emergencyContact;

    /** 紧急联系人电话 */
    @Schema(description = "紧急联系人电话")
    private String emergencyPhone;

    /** 紧急联系人地址 */
    @Schema(description = "紧急联系人地址")
    private String emergencyAddress;

    /** 员工编号 */
    @Schema(description = "员工编号")
    private String empNo;

    /** 入职日期 */
    @Schema(description = "员工编号")
    private String entryDate;

    /** 兼任信息 */
    @Schema(description = "兼任信息")
    private String positionJson;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;
}
