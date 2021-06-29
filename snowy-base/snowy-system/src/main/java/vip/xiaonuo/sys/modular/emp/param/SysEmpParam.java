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
package vip.xiaonuo.sys.modular.emp.param;

import cn.hutool.core.lang.Dict;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vip.xiaonuo.core.pojo.base.param.BaseParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 员工参数
 *
 * @author xuyuxiang
 * @date 2020/4/1 19:28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysEmpParam extends BaseParam {

    /**
     * 主键
     */
    private Long id;

    /**
     * 工号
     */
    private String jobNum;

    /**
     * 所属机构id
     */
    @NotNull(message = "所属机构id不能为空，请检查sysEmpParam.orgId参数", groups = {add.class, edit.class})
    private Long orgId;

    /**
     * 所属机构名称
     */
    @NotBlank(message = "所属机构名称不能为空，请检查sysEmpParam.orgName参数", groups = {add.class, edit.class})
    private String orgName;

    /**
     * 附属机构id和附属职位id集合
     */
    @NotNull(message = "附属信息不能为空，请检查sysEmpParam.extIds参数", groups = {add.class, edit.class})
    private List<Dict> extIds;

    /**
     * 职位id集合
     */
    @NotNull(message = "所属职位不能为空，请检查sysEmpParam.posIdList参数", groups = {add.class, edit.class})
    private List<Long> posIdList;
}
