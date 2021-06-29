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
package vip.xiaonuo.sys.modular.notice.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vip.xiaonuo.core.pojo.base.param.BaseParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统通知公告参数
 *
 * @author xuyuxiang
 * @date 2020/6/28 17:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysNoticeParam extends BaseParam {

    /**
     * 主键
     */
    @NotNull(message = "id不能为空，请检查id参数", groups = {edit.class, delete.class, detail.class, changeStatus.class})
    private Long id;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空，请检查title参数", groups = {add.class, edit.class})
    private String title;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空，请检查content参数", groups = {add.class, edit.class})
    private String content;

    /**
     * 类型（字典 1通知 2公告）
     */
    @NotNull(message = "类型不能为空，请检查type参数", groups = {add.class, edit.class})
    @Min(value = 1, message = "类型格式错误，请检查type参数", groups = {add.class, edit.class})
    @Max(value = 2, message = "类型格式错误，请检查type参数", groups = {add.class, edit.class})
    private Integer type;

    /**
     * 状态（字典 0草稿 1发布 2撤回 3删除）
     */
    @NotNull(message = "状态不能为空，请检查status参数", groups = {add.class, edit.class, changeStatus.class})
    private Integer status;

    /**
     * 通知到的人
     */
    @NotNull(message = "通知到的人不能为空，请检查noticeUserIdList参数", groups = {add.class, edit.class})
    private List<Long> noticeUserIdList;
}
