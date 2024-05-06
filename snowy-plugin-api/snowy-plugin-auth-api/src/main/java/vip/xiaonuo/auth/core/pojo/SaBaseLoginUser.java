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
package vip.xiaonuo.auth.core.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 基础的B端登录用户对象，可继承此类扩展更多属性
 *
 * @author xuyuxiang
 * @date 2021/12/23 21:49
 */
@Getter
@Setter
public abstract class SaBaseLoginUser {

    /** id */
    @Schema(description = "id")
    private String id;

    /** 头像 */
    @Schema(description = "头像")
    private String avatar;

    /** 签名 */
    @Schema(description = "签名")
    private String signature;

    /** 账号 */
    @Schema(description = "账号")
    private String account;

    /** 姓名 */
    @Schema(description = "姓名")
    private String name;

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
    @Schema(description = "入职日期")
    private String entryDate;

    /** 组织id */
    @Schema(description = "组织id")
    private String orgId;

    /** 组织名称 */
    @Schema(description = "组织名称")
    private String orgName;

    /** 职位id */
    @Schema(description = "职位id")
    private String positionId;

    /** 职位名称 */
    @Schema(description = "职位名称")
    private String positionName;

    /** 职级 */
    @Schema(description = "职级")
    private String positionLevel;

    /** 主管id */
    @Schema(description = "主管id")
    private String directorId;

    /** 兼任信息 */
    @Schema(description = "兼任信息")
    private String positionJson;

    /** 上次登录ip */
    @Schema(description = "上次登录ip")
    private String lastLoginIp;

    /** 上次登录地点 */
    @Schema(description = "上次登录地点")
    private String lastLoginAddress;

    /** 上次登录时间 */
    @Schema(description = "上次登录时间")
    private Date lastLoginTime;

    /** 上次登录设备 */
    @Schema(description = "上次登录设备")
    private String lastLoginDevice;

    /** 最新登录ip */
    @Schema(description = "最新登录ip")
    private String latestLoginIp;

    /** 最新登录地点 */
    @Schema(description = "最新登录地点")
    private String latestLoginAddress;

    /** 最新登录时间 */
    @Schema(description = "最新登录时间")
    private Date latestLoginTime;

    /** 最新登录设备 */
    @Schema(description = "最新登录设备")
    private String latestLoginDevice;

    /** 用户状态 */
    @Schema(description = "用户状态")
    private String userStatus;

    /** 排序码 */
    @Schema(description = "排序码")
    private Integer sortCode;

    /** 扩展信息 */
    @Schema(description = "扩展信息")
    private String extJson;

    /** 按钮码集合 */
    @Schema(description = "按钮码集合")
    private List<String> buttonCodeList;

    /** 移动端按钮码集合 */
    @Schema(description = "移动端按钮码集合")
    private List<String> mobileButtonCodeList;

    /** 权限码集合 */
    @Schema(description = "权限码集合", hidden = true)
    private List<String> permissionCodeList;

    /** 角色码集合 */
    @Schema(description = "角色码集合", hidden = true)
    private List<String> roleCodeList;

    /** 数据范围集合 */
    @Schema(description = "数据范围集合", hidden = true)
    private List<DataScope> dataScopeList;

    /** 用户密码hash值 */
    @JsonIgnore
    @Schema(description = "用户密码hash值")
    private String password;

    /** 是否可登录，由继承类实现 */
    public abstract Boolean getEnabled();

    /**
     * 数据范围类
     *
     * @author xuyuxiang
     * @date 2022/8/15 13:57
     **/
    @Getter
    @Setter
    public static class DataScope {

        /** API接口 */
        @Schema(description = "API接口")
        private String apiUrl;

        /** 数据范围 */
        @Schema(description = "数据范围")
        private List<String> dataScope;
    }
}
