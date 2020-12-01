package com.cn.xiaonuo.core.tenant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 租户表
 * </p>
 *
 * @author yubaoshan
 * @since 2019-06-16
 */
@TableName("sys_tenant_info")
@Data
public class TenantInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户名称
     */
    @TableField("name")
    private String name;

    /**
     * 租户的编码
     */
    @TableField("code")
    private String code;

    /**
     * 关联的数据库名称
     */
    @TableField("db_name")
    private String dbName;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 更新人
     */
    @TableField(value = "update_user", fill = FieldFill.UPDATE)
    private Long updateUser;

}
