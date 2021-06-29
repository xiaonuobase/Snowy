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
package vip.xiaonuo.sys.modular.menu.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vip.xiaonuo.core.pojo.base.param.BaseParam;
import vip.xiaonuo.core.validation.flag.FlagValue;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 系统菜单参数
 *
 * @author xuyuxiang
 * @date 2020/3/26 20:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysMenuParam extends BaseParam {

    /**
     * 主键
     */
    @NotNull(message = "id不能为空，请检查id参数", groups = {edit.class, delete.class, detail.class})
    private Long id;

    /**
     * 父id
     */
    @NotNull(message = "pid不能为空，请检查pid参数", groups = {add.class, edit.class})
    private Long pid;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空，请检查name参数", groups = {add.class, edit.class})
    private String name;

    /**
     * 编码
     */
    @NotBlank(message = "编码不能为空，请检查code参数", groups = {add.class, edit.class})
    private String code;

    /**
     * 菜单类型（字典 0目录 1菜单 2按钮）
     */
    @NotNull(message = "菜单类型不能为空，请检查type参数", groups = {add.class, edit.class})
    @Min(value = 0, message = "菜单类型格式错误，请检查type参数", groups = {add.class, edit.class})
    @Max(value = 2, message = "菜单类型格式错误，请检查type参数", groups = {add.class, edit.class})
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
    @NotBlank(message = "应用分类不能为空，请检查application参数", groups = {add.class, edit.class, change.class})
    private String application;

    /**
     * 打开方式（字典 0无 1组件 2内链 3外链）
     */
    @NotNull(message = "打开方式不能为空，请检查openType参数", groups = {add.class, edit.class})
    @Min(value = 0, message = "打开方式格式错误，请检查openType参数", groups = {add.class, edit.class})
    @Max(value = 3, message = "打开方式格式错误，请检查openType参数", groups = {add.class, edit.class})
    private Integer openType;

    /**
     * 是否可见（Y-是，N-否）
     */
    @NotBlank(message = "是否可见不能为空，请检查visible参数", groups = {add.class, edit.class})
    @FlagValue(message = "是否可见格式错误，正确格式应该Y或者N，请检查visible参数", groups = {add.class, edit.class})
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
    @NotNull(message = "权重不能为空，请检查weight参数", groups = {add.class, edit.class})
    @Min(value = 0, message = "权重格式错误，请检查weight参数", groups = {add.class, edit.class})
    @Max(value = 2, message = "权重格式错误，请检查weight参数", groups = {add.class, edit.class})
    private Integer weight;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空，请检查sort参数", groups = {add.class, edit.class})
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
