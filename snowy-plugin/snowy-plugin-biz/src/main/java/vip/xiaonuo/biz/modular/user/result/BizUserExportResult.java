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
package vip.xiaonuo.biz.modular.user.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户导出结果集
 *
 * @author xuyuxiang
 * @date 2022/7/8 13:22
 **/
@Getter
@Setter
public class BizUserExportResult {

    /** 头像 */
    private String avatar;

    /** 头像字节数组 */
    @Excel(name = "头像", type = 2, imageType = 2)
    private byte[] avatarByte;

    /** 账号 */
    @Excel(name = "账号")
    private String account;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 昵称 */
    @Excel(name = "姓名")
    private String nickname;

    /** 性别 */
    @Excel(name = "性别")
    private String gender;

    /** 年龄 */
    @Excel(name = "年龄")
    private String age;

    /** 出生日期 */
    @Excel(name = "出生日期")
    private String birthday;

    /** 民族 */
    @Excel(name = "民族")
    private String nation;

    /** 籍贯 */
    @Excel(name = "籍贯")
    private String nativePlace;

    /** 家庭住址 */
    @Excel(name = "家庭住址")
    private String homeAddress;

    /** 通信地址 */
    @Excel(name = "通信地址")
    private String mailingAddress;

    /** 证件类型 */
    @Excel(name = "证件类型")
    private String idCardType;

    /** 证件号码 */
    @Excel(name = "证件号码")
    private String idCardNumber;

    /** 文化程度 */
    @Excel(name = "文化程度")
    private String cultureLevel;

    /** 政治面貌 */
    @Excel(name = "政治面貌")
    private String politicalOutlook;

    /** 毕业院校 */
    @Excel(name = "毕业院校")
    private String college;

    /** 学历 */
    @Excel(name = "学历")
    private String education;

    /** 学制 */
    @Excel(name = "学制")
    private String eduLength;

    /** 学位 */
    @Excel(name = "学位")
    private String degree;

    /** 手机 */
    @Excel(name = "手机")
    private String phone;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 家庭电话 */
    @Excel(name = "家庭电话")
    private String homeTel;

    /** 办公电话 */
    @Excel(name = "办公电话")
    private String officeTel;

    /** 紧急联系人 */
    @Excel(name = "紧急联系人")
    private String emergencyContact;

    /** 紧急联系人电话 */
    @Excel(name = "紧急联系人电话")
    private String emergencyPhone;

    /** 紧急联系人地址 */
    @Excel(name = "紧急联系人地址")
    private String emergencyAddress;

    /** 员工编号 */
    @Excel(name = "员工编号")
    private String empNo;

    /** 入职日期 */
    @Excel(name = "入职日期")
    private String entryDate;

    /** 组织名称 */
    @Excel(name = "组织名称")
    private String orgName;

    /** 职位名称 */
    @Excel(name = "职位名称")
    private String positionName;

    /** 主管名称 */
    @Excel(name = "主管名称")
    private String directorName;

    /** 职级 */
    @Excel(name = "职级")
    private String positionLevel;

    /** 上次登录ip */
    @Excel(name = "上次登录ip")
    private String lastLoginIp;

    /** 上次登录地点 */
    @Excel(name = "上次登录地点")
    private String lastLoginAddress;

    /** 上次登录时间 */
    @Excel(name = "上次登录时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /** 上次登录设备 */
    @Excel(name = "上次登录设备")
    private String lastLoginDevice;

    /** 最新登录ip */
    @Excel(name = "最新登录ip")
    private String latestLoginIp;

    /** 最新登录地点 */
    @Excel(name = "最新登录地点")
    private String latestLoginAddress;

    /** 最新登录时间 */
    @Excel(name = "最新登录时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date latestLoginTime;

    /** 最新登录设备 */
    @Excel(name = "最新登录设备")
    private String latestLoginDevice;

    /** 用户状态 */
    @Excel(name = "用户状态", replace = { "正常_ENABLE", "停用_DISABLED" })
    private String userStatus;
}
