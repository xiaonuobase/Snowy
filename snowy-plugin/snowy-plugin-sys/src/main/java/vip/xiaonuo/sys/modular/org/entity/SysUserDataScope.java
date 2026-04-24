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
package vip.xiaonuo.sys.modular.org.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户数据范围预计算实体
 * <p>
 * 通过SCOPE_KEY将相同orgId集合的API分组，避免重复存储，大幅减少数据量。
 * </p>
 *
 * @author yubaoshan
 * @date 2026/2/12
 **/
@Getter
@Setter
@TableName("SYS_USER_DATA_SCOPE")
public class SysUserDataScope implements Serializable {

    /** 用户ID */
    @Schema(description = "用户ID")
    private String userId;

    /** 作用域KEY（orgId集合的MD5摘要） */
    @Schema(description = "作用域KEY")
    private String scopeKey;

    /** 机构ID */
    @Schema(description = "机构ID")
    private String orgId;
}
