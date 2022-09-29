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
package vip.xiaonuo.sys.modular.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 编辑个人信息参数
 *
 * @author xuyuxiang
 * @date 2022/7/27 17:08
 **/
@Getter
@Setter
public class SysUserUpdateInfoParam {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 姓名 */
    @ApiModelProperty(value = "姓名", required = true, position = 2)
    @NotBlank(message = "name不能为空")
    private String name;

    /** 手机 */
    @ApiModelProperty(value = "手机", position = 3)
    private String phone;

    /** 昵称 */
    @ApiModelProperty(value = "昵称", position = 4)
    private String nickname;

    /** 性别 */
    @ApiModelProperty(value = "性别", position = 5)
    private String gender;

    /** 出生日期 */
    @ApiModelProperty(value = "出生日期", position = 6)
    private String birthday;

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱", position = 7)
    private String email;

    /** 签名 */
    @ApiModelProperty(value = "签名，图片base64", position = 8)
    private String signature;
}
