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

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 人员导出结果集
 *
 * @author xuyuxiang
 * @date 2022/7/8 13:22
 **/
@Getter
@Setter
public class BizUserExportResult {

    /** 机构分组 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40)
    @ExcelProperty({"人员信息", "机构分组"})
    private String groupName;

    /** 头像 */
    @ColumnWidth(8)
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "头像"})
    private byte[] avatar;

    /** 账号 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "账号"})
    private String account;

    /** 姓名 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "姓名"})
    private String name;

    /** 昵称 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "昵称"})
    private String nickname;

    /** 性别 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "性别"})
    private String gender;

    /** 年龄 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "年龄"})
    private String age;

    /** 出生日期 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "出生日期"})
    private String birthday;

    /** 民族 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "民族"})
    private String nation;

    /** 籍贯 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "籍贯"})
    private String nativePlace;

    /** 家庭住址 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "家庭住址"})
    private String homeAddress;

    /** 通信地址 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "通信地址"})
    private String mailingAddress;

    /** 证件类型 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "证件类型"})
    private String idCardType;

    /** 证件号码 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "证件号码"})
    private String idCardNumber;

    /** 文化程度 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "文化程度"})
    private String cultureLevel;

    /** 政治面貌 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "政治面貌"})
    private String politicalOutlook;

    /** 毕业院校 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "毕业院校"})
    private String college;

    /** 学历 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "学历"})
    private String education;

    /** 学制 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "学制"})
    private String eduLength;

    /** 学位 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "学位"})
    private String degree;

    /** 手机 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "手机"})
    private String phone;

    /** 邮箱 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "邮箱"})
    private String email;

    /** 家庭电话 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "家庭电话"})
    private String homeTel;

    /** 办公电话 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "办公电话"})
    private String officeTel;

    /** 紧急联系人 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "紧急联系人"})
    private String emergencyContact;

    /** 紧急联系人电话 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "紧急联系人电话"})
    private String emergencyPhone;

    /** 紧急联系人地址 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"人员信息", "基本信息", "紧急联系人地址"})
    private String emergencyAddress;

    /** 员工编号 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 34)
    @ExcelProperty({"人员信息", "员工信息", "员工编号"})
    private String empNo;

    /** 入职日期 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 34)
    @ExcelProperty({"人员信息", "员工信息", "入职日期"})
    private String entryDate;

    /** 组织名称 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 34)
    @ExcelProperty({"人员信息", "员工信息", "组织名称"})
    private String orgName;

    /** 职位名称 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 34)
    @ExcelProperty({"人员信息", "员工信息", "职位名称"})
    private String positionName;

    /** 主管名称 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 34)
    @ExcelProperty({"人员信息", "员工信息", "主管名称"})
    private String directorName;

    /** 职级 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 34)
    @ExcelProperty({"人员信息", "员工信息", "职级"})
    private String positionLevel;

    /** 上次登录ip */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"人员信息", "系统信息", "上次登录ip"})
    private String lastLoginIp;

    /** 上次登录地点 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"人员信息", "系统信息", "上次登录地点"})
    private String lastLoginAddress;

    /** 上次登录时间 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty({"人员信息", "系统信息", "上次登录时间"})
    private Date lastLoginTime;

    /** 上次登录设备 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"人员信息", "系统信息", "上次登录设备"})
    private String lastLoginDevice;

    /** 最新登录ip */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"人员信息", "系统信息", "最新登录ip"})
    private String latestLoginIp;

    /** 最新登录地点 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"人员信息", "系统信息", "最新登录地点"})
    private String latestLoginAddress;

    /** 最新登录时间 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty({"人员信息", "系统信息", "最新登录时间"})
    private Date latestLoginTime;

    /** 最新登录设备 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"人员信息", "系统信息", "最新登录设备"})
    private String latestLoginDevice;

    /** 人员状态 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"人员信息", "系统信息", "人员状态"})
    private String userStatus;
}
