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
package vip.xiaonuo.sys.modular.menu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vip.xiaonuo.core.pojo.base.entity.BaseEntity;
import vip.xiaonuo.core.pojo.base.node.BaseTreeNode;

import java.util.List;

/**
 * 系统菜单表
 *
 * @author xuyuxiang
 * @date 2020/3/11 11:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_menu")
public class SysMenu extends BaseEntity implements BaseTreeNode {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 父id
     */
    private Long pid;

    /**
     * 父ids
     */
    private String pids;

    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 菜单类型（字典 0目录 1菜单 2按钮）
     */
    private Integer type;

    /**
     * 图标
     */
    private String icon;

    /**
     * 路由地址
     */
    private String router;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 应用分类（应用编码）
     */
    private String application;

    /**
     * 打开方式（字典 0无 1组件 2内链 3外链）
     */
    private Integer openType;

    /**
     * 是否可见（Y-是，N-否）
     */
    private String visible;

    /**
     * 内链地址
     */
    private String link;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * 权重（字典 1系统权重 2业务权重）
     */
    private Integer weight;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    private Integer status;

    /**
     * 子节点（表中不存在，用于构造树）
     */
    @TableField(exist = false)
    private List children;

    @Override
    public void setChildren(List children) {
        this.children = children;
    }
}
