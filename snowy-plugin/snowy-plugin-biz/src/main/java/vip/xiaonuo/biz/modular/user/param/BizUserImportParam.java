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

import lombok.Getter;
import lombok.Setter;

/**
 * 人员导入参数
 *
 * @author xuyuxiang
 * @date 2022/7/8 13:22
 **/
@Getter
@Setter
public class BizUserImportParam {

    /** 账号 */
    private String account;

    /** 姓名 */
    private String name;

    /** 组织名称 */
    private String orgName;

    /** 职位名称 */
    private String positionName;

    /** 手机 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 主管名称 */
    private String directorName;

    /** 员工编号 */
    private String empNo;

    /** 入职日期 */
    private String entryDate;

    /** 职级 */
    private String positionLevel;

    /** 昵称 */
    private String nickname;

    /** 性别 */
    private String gender;

    /** 年龄 */
    private String age;

    /** 出生日期 */
    private String birthday;

    /** 民族 */
    private String nation;

    /** 籍贯 */
    private String nativePlace;

    /** 家庭住址 */
    private String homeAddress;

    /** 通信地址 */
    private String mailingAddress;

    /** 证件类型 */
    private String idCardType;

    /** 证件号码 */
    private String idCardNumber;

    /** 文化程度 */
    private String cultureLevel;

    /** 政治面貌 */
    private String politicalOutlook;

    /** 毕业院校 */
    private String college;

    /** 学历 */
    private String education;

    /** 学制 */
    private String eduLength;

    /** 学位 */
    private String degree;

    /** 家庭电话 */
    private String homeTel;

    /** 办公电话 */
    private String officeTel;

    /** 紧急联系人 */
    private String emergencyContact;

    /** 紧急联系人电话 */
    private String emergencyPhone;

    /** 紧急联系人地址 */
    private String emergencyAddress;
}
