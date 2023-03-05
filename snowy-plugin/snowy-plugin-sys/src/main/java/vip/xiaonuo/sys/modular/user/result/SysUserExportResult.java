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

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
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
public class SysUserExportResult {

    /** 头像 */
    @ExcelProperty({"基本信息", "头像"})
    private byte[] avatar;

    /** 账号 */
    @ExcelProperty({"基本信息", "账号"})
    private String account;

    /** 姓名 */
    @ExcelProperty({"基本信息", "姓名"})
    private String name;

    /** 昵称 */
    @ExcelProperty({"基本信息", "昵称"})
    private String nickname;

    /** 性别 */
    @ExcelProperty({"基本信息", "性别"})
    private String gender;

    /** 年龄 */
    @ExcelProperty({"基本信息", "年龄"})
    private String age;

    /** 出生日期 */
    @ExcelProperty({"基本信息", "出生日期"})
    private String birthday;

    /** 民族 */
    @ExcelProperty({"基本信息", "民族"})
    private String nation;

    /** 籍贯 */
    @ExcelProperty({"基本信息", "籍贯"})
    private String nativePlace;

    /** 家庭住址 */
    @ExcelProperty({"基本信息", "家庭住址"})
    private String homeAddress;

    /** 通信地址 */
    @ExcelProperty({"基本信息", "通信地址"})
    private String mailingAddress;

    /** 证件类型 */
    @ExcelProperty({"基本信息", "证件类型"})
    private String idCardType;

    /** 证件号码 */
    @ExcelProperty({"基本信息", "证件号码"})
    private String idCardNumber;

    /** 文化程度 */
    @ExcelProperty({"基本信息", "文化程度"})
    private String cultureLevel;

    /** 政治面貌 */
    @ExcelProperty({"基本信息", "政治面貌"})
    private String politicalOutlook;

    /** 毕业院校 */
    @ExcelProperty({"基本信息", "毕业院校"})
    private String college;

    /** 学历 */
    @ExcelProperty({"基本信息", "学历"})
    private String education;

    /** 学制 */
    @ExcelProperty({"基本信息", "学制"})
    private String eduLength;

    /** 学位 */
    @ExcelProperty({"基本信息", "学位"})
    private String degree;

    /** 手机 */
    @ExcelProperty({"基本信息", "手机"})
    private String phone;

    /** 邮箱 */
    @ExcelProperty({"基本信息", "邮箱"})
    private String email;

    /** 家庭电话 */
    @ExcelProperty({"基本信息", "家庭电话"})
    private String homeTel;

    /** 办公电话 */
    @ExcelProperty({"基本信息", "办公电话"})
    private String officeTel;

    /** 紧急联系人 */
    @ExcelProperty({"基本信息", "紧急联系人"})
    private String emergencyContact;

    /** 紧急联系人电话 */
    @ExcelProperty({"基本信息", "紧急联系人电话"})
    private String emergencyPhone;

    /** 紧急联系人地址 */
    @ExcelProperty({"基本信息", "紧急联系人地址"})
    private String emergencyAddress;

    /** 员工编号 */
    @ExcelProperty({"员工信息", "员工编号"})
    private String empNo;

    /** 入职日期 */
    @ExcelProperty({"员工信息", "入职日期"})
    private String entryDate;

    /** 组织名称 */
    @ExcelProperty({"员工信息", "组织名称"})
    private String orgName;

    /** 职位名称 */
    @ExcelProperty({"员工信息", "职位名称"})
    private String positionName;

    /** 主管名称 */
    @ExcelProperty({"员工信息", "主管名称"})
    private String directorName;

    /** 职级 */
    @ExcelProperty({"员工信息", "职级"})
    private String positionLevel;

    /** 上次登录ip */
    @ExcelProperty({"系统信息", "上次登录ip"})
    private String lastLoginIp;

    /** 上次登录地点 */
    @ExcelProperty({"系统信息", "上次登录地点"})
    private String lastLoginAddress;

    /** 上次登录时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty({"系统信息", "上次登录时间"})
    private Date lastLoginTime;

    /** 上次登录设备 */
    @ExcelProperty({"系统信息", "上次登录设备"})
    private String lastLoginDevice;

    /** 最新登录ip */
    @ExcelProperty({"系统信息", "最新登录ip"})
    private String latestLoginIp;

    /** 最新登录地点 */
    @ExcelProperty({"系统信息", "最新登录地点"})
    private String latestLoginAddress;

    /** 最新登录时间 */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty({"系统信息", "最新登录时间"})
    private Date latestLoginTime;

    /** 最新登录设备 */
    @ExcelProperty({"系统信息", "最新登录设备"})
    private String latestLoginDevice;

    /** 用户状态 */
    @ExcelProperty(value = {"系统信息", "用户状态"})
    private String userStatus;
}
