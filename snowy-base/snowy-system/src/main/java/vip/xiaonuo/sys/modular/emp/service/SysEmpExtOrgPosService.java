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
package vip.xiaonuo.sys.modular.emp.service;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.sys.modular.emp.entity.SysEmpExtOrgPos;

import java.util.List;

/**
 * 员工附属机构service接口
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:10
 */
public interface SysEmpExtOrgPosService extends IService<SysEmpExtOrgPos> {

    /**
     * 保存或编辑附属机构相关信息
     *
     * @param empId     员工id（用户id）
     * @param extIdList 附属机构职位信息集合，格式：[{"orgId":1234, "posId":5678}]
     * @author xuyuxiang
     * @date 2020/4/2 8:59
     */
    void addOrEdit(Long empId, List<Dict> extIdList);

    /**
     * 获取附属机构和职位信息
     *
     * @param empId    员工id（用户id）
     * @param isFillId 是否需要返回id信息
     * @return 增强版hashMap，格式：[{"orgId":123, "orgCode":"yfb", "orgName":"研发部", "posId":456, "posCode":"zjl", "posName":"总经理"}]
     * @author xuyuxiang
     * @date 2020/4/2 20:07
     */
    List<Dict> getEmpExtOrgPosDictList(Long empId, boolean isFillId);

    /**
     * 根据机构id判断该附属机构下是否有员工
     *
     * @param orgId 机构id
     * @return 该附属机构下是否有员工，true是，false否
     * @author xuyuxiang
     * @date 2020/6/23 10:30
     */
    boolean hasExtOrgEmp(Long orgId);

    /**
     * 根据职位id判断该附属职位下是否有员工
     *
     * @param posId 职位id
     * @return 该附属职位下是否有员工，true是，false否
     * @author xuyuxiang
     * @date 2020/6/23 10:38
     */
    boolean hasExtPosEmp(Long posId);

    /**
     * 根据员工id删除对应的员工-附属信息
     *
     * @param empId 员工id（用户id）
     * @author xuyuxiang
     * @date 2020/6/28 14:57
     */
    void deleteEmpExtInfoByUserId(Long empId);
}
