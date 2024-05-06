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
package vip.xiaonuo.sys.modular.index.result;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 操作日志结果
 *
 * @author xuyuxiang
 * @date 2022/9/2 15:02
 */
@Getter
@Setter
public class SysIndexOpLogListResult {

    /** id */
    @Schema(description = "id")
    private String id;

    /** 日志分类 */
    @Schema(description = "日志分类")
    private String category;

    /** 日志名称 */
    @Schema(description = "日志名称")
    private String name;

    /** 执行状态 */
    @Schema(description = "执行状态")
    private String exeStatus;

    /** 具体消息 */
    @Schema(description = "具体消息")
    private String exeMessage;

    /** 操作ip */
    @Schema(description = "操作ip")
    private String opIp;

    /** 操作地址 */
    @Schema(description = "操作地址")
    private String opAddress;

    /** 操作浏览器 */
    @Schema(description = "操作浏览器")
    private String opBrowser;

    /** 操作系统 */
    @Schema(description = "操作系统")
    private String opOs;

    /** 类名称 */
    @Schema(description = "类名称")
    private String className;

    /** 方法名称 */
    @Schema(description = "方法名称")
    private String methodName;

    /** 请求方式 */
    @Schema(description = "请求方式")
    private String reqMethod;

    /** 请求地址 */
    @Schema(description = "请求地址")
    private String reqUrl;

    /** 请求参数 */
    @Schema(description = "请求参数")
    private String paramJson;

    /** 返回结果 */
    @Schema(description = "返回结果")
    private String resultJson;

    /** 操作时间 */
    @Schema(description = "操作时间")
    private Date opTime;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名")
    private String opUser;

    /** 签名数据 */
    @Schema(description = "签名数据")
    private String signData;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 创建人 */
    @Schema(description = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createUser;

    /** 更新时间 */
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 更新人 */
    @Schema(description = "更新人")
    @TableField(fill = FieldFill.UPDATE)
    private String updateUser;
}
