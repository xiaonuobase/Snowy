/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.sys.modular.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vip.xiaonuo.core.pojo.base.entity.BaseEntity;

import java.util.Date;

/**
 * 系统用户表
 *
 * @author xuyuxiang
 * @date 2020/3/5 12:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号
     */
    @Excel(name = "账号", width = 20)
    private String account;

    /**
     * 密码哈希值
     */
    private String pwdHashValue;

    /**
     * 昵称
     */
    @Excel(name = "昵称", width = 20)
    private String nickName;

    /**
     * 姓名
     */
    @Excel(name = "姓名", width = 20)
    private String name;

    /**
     * 头像
     */
    private Long avatar;

    /**
     * 生日
     */
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "生日", databaseFormat = "yyyy-MM-dd HH:mm:ss", format = "yyyy-MM-dd", width = 20)
    private Date birthday;

    /**
     * 性别(字典 1男 2女 3未知)
     */
    @Excel(name = "性别", replace = {"男_1", "女_2"}, width = 20)
    private Integer sex;

    /**
     * 邮箱
     */
    @Excel(name = "邮箱", width = 30)
    private String email;

    /**
     * 手机
     */
    @Excel(name = "手机", width = 30)
    private String phone;

    /**
     * 电话
     */
    @Excel(name = "电话", width = 30)
    private String tel;

    /**
     * 最后登陆IP
     */
    @Excel(name = "最后登陆IP", width = 30)
    private String lastLoginIp;

    /**
     * 最后登陆时间
     */
    @Excel(name = "最后登陆时间", databaseFormat = "yyyy-MM-dd HH:mm:ss", format = "yyyy-MM-dd HH:mm:ss", width = 30)
    private Date lastLoginTime;

    /**
     * 管理员类型（1超级管理员 2非管理员）
     */
    @Excel(name = "管理员类型", replace = {"超级管理员_1", "非管理员_2"}, width = 20)
    private Integer adminType;

    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    @Excel(name = "状态", replace = {"正常_0", "停用_1", "删除_2"}, width = 20)
    private Integer status;


}
